package perspectiveprojection.projections;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Frustum;
import perspectiveprojection.Game;
import perspectiveprojection.HelperFunctions;
import perspectiveprojection.LineSegment;
import perspectiveprojection.Point3D;
import perspectiveprojection.ViewportTransformation;

public abstract class Projection {
	private final Camera cam;
	protected SimpleMatrix projectionMatrix = SimpleMatrix.identity(4);
	private Frustum frustum; //Viewing frustum in clip space
	
	public Projection(Camera cam) {
		this.cam = cam;
	}
	
	public Point3D project(SimpleMatrix point) {
		return project(Point3D.fromMatrix(point));
	}
	
	public Point3D project(Point3D point) {
		SimpleMatrix clipSpace = projectToClipSpace(point);
		
		return ViewportTransformation.fromClipSpaceToScreenSpace(clipSpace, Game.WIDTH, Game.HEIGHT);
	}
	
	public Point3D[] projectPoints(Point3D... points) {
		Point3D[] projectedPoints = new Point3D[points.length];
		
		for (int i = 0; i < points.length; i++) {
			projectedPoints[i] = project(points[i]);
		}
		
		return projectedPoints;
	}
	
	public Point3D[] projectPointsInt(Point3D... points) {
		Point3D[] projectedPoints = new Point3D[points.length];
		
		for (int i = 0; i < points.length; i++) {
			projectedPoints[i] = project(points[i]);
		}
		
		return projectedPoints;
	}
	
	/**
	 * Projects the point from world space to clip space, and returns homogeneous coordinates.
	 * Still have to do perspective divide to get Normalized Device Coordinates,
	 * and to do viewport transformation to get to screen space.
	 * @param point
	 * @return 
	 */
	public SimpleMatrix projectToClipSpace(Point3D point) {
		return projectToClipSpace(point.asHomogeneousVector());
	}
	
	public SimpleMatrix projectToClipSpace(SimpleMatrix point) {
		SimpleMatrix v = point; //point is in world space
		SimpleMatrix projectionViewMatrix = projectionMatrix.mult(cam.getViewMatrix());
		
		//From world space to viewSpace to clipSpace with one matrix:
		//res is now in clip space. We still have to do perspective divide, to normalize the coordinates to normalized device coordinates (NDC)
		SimpleMatrix clipSpace = projectionViewMatrix.mult(v);
		
		return clipSpace;
	}
	
	public LineSegment projectLineSegment(LineSegment line) {
		return projectLineSegment(line.getStart(), line.getEnd());
	}
	
	public LineSegment projectLineSegment(Point3D a, Point3D b) {
		return projectLineSegment(a.asHomogeneousVector(), b.asHomogeneousVector());
	}
	
	/**
	 * Projects a line segment from world space to screen space.
	 * Performs frustum cli
	 * @param a
	 * @param b
	 * @return 
	 */
	public LineSegment projectLineSegment(SimpleMatrix a, SimpleMatrix b) {
		SimpleMatrix clipSpaceA = projectToClipSpace(a);
		SimpleMatrix clipSpaceB = projectToClipSpace(b);
		
		
		//Frustum clipping (if (-w < x, y, z < w) then the point is valid. If it's outside the w's, then it's clipped, see http://www.songho.ca/opengl/gl_projectionmatrix.html )
		
		//Do frustum culling:
		SimpleMatrix[] clipped = clipLine(clipSpaceA, clipSpaceB);
		if (clipped == null) {
			return null;
		}
		
		Point3D viewA = ViewportTransformation.fromClipSpaceToScreenSpace(clipped[0], Game.WIDTH, Game.HEIGHT);
		Point3D viewB = ViewportTransformation.fromClipSpaceToScreenSpace(clipped[1], Game.WIDTH, Game.HEIGHT);
		
		return new LineSegment(viewA, viewB);
	}
	
	/**
	 * Performs line clipping in clip space coordinates.
	 * Moves the point that is outside of the viewing frustum
	 * to the intersection point of the line and frustum.
	 * If both points are outside the frustum, null is returned.
	 * @param a
	 * @param b
	 * @return 
	 */
	private SimpleMatrix[] clipLine(SimpleMatrix a, SimpleMatrix b) {
		if (bothPointsInside(a, b)) {
			return new SimpleMatrix[] {a, b};
		}
		
		//LEFT
		SimpleMatrix pointOnLeftPlane = new Point3D(-1, 0, 0).asHomogeneousVector();
		if (a.get(0) < -a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnLeftPlane, frustum.left, a, b);
		} else if (b.get(0) < -b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnLeftPlane, frustum.left, a, b);
		}
		
		if (a == null || b == null) return null; //If one of them is null, then the whole line segment was outside of the plane
		
		//RIGHT
		SimpleMatrix pointOnRightPlane = new Point3D(1, 0, 0).asHomogeneousVector();
		if (a.get(0) > a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnRightPlane, frustum.right, a, b);
		} else if (b.get(0) > b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnRightPlane, frustum.right, a, b);
		}
		
		if (a == null || b == null) return null;
		
		//BOTTOM
		SimpleMatrix pointOnBottomPlane = new Point3D(0, -1, 0).asHomogeneousVector();
		if (a.get(1) < -a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnBottomPlane, frustum.bottom, a, b);
		} else if (b.get(1) < -b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnBottomPlane, frustum.bottom, a, b);
		}
		
		if (a == null || b == null) return null;
		
		//TOP
		SimpleMatrix pointOnTopPlane = new Point3D(0, 1, 0).asHomogeneousVector();
		if (a.get(1) > a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnTopPlane, frustum.top, a, b);
		} else if (b.get(1) > b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnTopPlane, frustum.top, a, b);
		}
		
		if (a == null || b == null) return null;
		
		//NEAR
		SimpleMatrix pointOnNearPlane = new Point3D(0, 0, 0).asHomogeneousVector();
		if (a.get(2) < -a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnNearPlane, frustum.near, a, b);
		} else if (b.get(2) < -b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnNearPlane, frustum.near, a, b);
		}
		
		if (a == null || b == null) return null;
		
		//FAR
		SimpleMatrix pointOnFarPlane = new Point3D(0, 0, 1).asHomogeneousVector();
		if (a.get(2) > a.get(3)) {
			a = HelperFunctions.intersectionPointWithPlane(pointOnFarPlane, frustum.far, a, b);
		} else if (b.get(2) > b.get(3)) {
			b = HelperFunctions.intersectionPointWithPlane(pointOnFarPlane, frustum.far, a, b);
		}
		
		if (a == null || b == null) return null;
		
		return new SimpleMatrix[] {a, b};
	}
	
	private static boolean bothPointsInside(SimpleMatrix a, SimpleMatrix b) {
		return pointInside(a) && pointInside(b);
	}
	
	private static boolean pointInside(SimpleMatrix a) {
		return componentInside(a.get(0), a.get(3)) && componentInside(a.get(1), a.get(3)) && componentInside(a.get(2), a.get(3));
	}
	
	private static boolean componentInside(double a, double w) {
		return a >= -w && a <= w;
	}
	
	public SimpleMatrix getProjectionMatrix() {
		return projectionMatrix;
	}
	
	protected void calculateViewingFrustumFromProjectionMatrix() {
		//This transforms the normals correctly from view space to clip space, since they are not points, but directions.
		SimpleMatrix normalTransform = projectionMatrix.invert().transpose(); //Inverse and transpose order doesn't matter.
		
		//Have to transpose these so that they are column vectors, instead of row vectors.
		SimpleMatrix leftNormal = normalTransform.mult(projectionMatrix.extractVector(true, 3).plus(projectionMatrix.extractVector(true, 0)).transpose());
		SimpleMatrix rightNormal = normalTransform.mult(projectionMatrix.extractVector(true, 3).minus(projectionMatrix.extractVector(true, 0)).transpose());
		
		SimpleMatrix bottomNormal = normalTransform.mult(projectionMatrix.extractVector(true, 3).plus(projectionMatrix.extractVector(true, 1)).transpose());
		SimpleMatrix topNormal = normalTransform.mult(projectionMatrix.extractVector(true, 3).minus(projectionMatrix.extractVector(true, 1)).transpose());
		
		SimpleMatrix nearNormal = normalTransform.mult(projectionMatrix.extractVector(true, 2).transpose()); //different cause z is from 0 to 1, just the third row
		SimpleMatrix farNormal = normalTransform.mult(projectionMatrix.extractVector(true, 3).minus(projectionMatrix.extractVector(true, 2)).transpose());
		
		//Viewing frustum in clip space:
		frustum = new Frustum(topNormal, bottomNormal, leftNormal, rightNormal, nearNormal, farNormal);
	}
}
