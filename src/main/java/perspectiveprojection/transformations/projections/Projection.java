package perspectiveprojection.transformations.projections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.camera.Camera;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.camera.Frustum;
import perspectiveprojection.Game;
import perspectiveprojection.util.HelperFunctions;
import perspectiveprojection.objects.Light;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.Renderable;
import perspectiveprojection.transformations.ViewportTransformation;
import static perspectiveprojection.Game.AMBIENT_LIGHT;

public abstract class Projection {
	private final Camera cam;
	protected SimpleMatrix projectionMatrix = SimpleMatrix.identity(4);
	private Frustum frustum; //Viewing frustum in clip space
	
	public Projection(Camera cam) {
		this.cam = cam;
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	public Point3D project(SimpleMatrix point, boolean clipPoint) {
		return project(Point3D.fromMatrix(point), clipPoint);
	}
	
	public Point3D project(Point3D point, boolean clipPoint) {
		SimpleMatrix clipSpacePoint = projectToClipSpace(point);
		
		if (clipPoint && !pointInside(clipSpacePoint)) {
			return null;
		}
		
		return ViewportTransformation.fromClipSpaceToScreenSpace(clipSpacePoint, Game.WIDTH, Game.HEIGHT);
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
	
	public SimpleMatrix projectToClipSpace(SimpleMatrix point) { //point is in world space
		//SimpleMatrix projectionViewMatrix = projectionMatrix.mult(cam.getViewMatrix());
		
		SimpleMatrix projectionViewMatrix = projectionMatrix;
		point = cam.getViewMatrix().mult(point);
		
		//From world space to viewSpace to clipSpace with one matrix:
		//res is now in clip space. We still have to do perspective divide, to normalize the coordinates to normalized device coordinates (NDC)
		SimpleMatrix clipSpace = projectionViewMatrix.mult(point);
		
		return clipSpace;
	}
	
	public Optional<LineSegment> projectLineSegment(LineSegment line) {
		return projectLineSegment(line.getStart(), line.getEnd());
	}
	
	public Optional<LineSegment> projectLineSegment(Point3D a, Point3D b) {
		return projectLineSegment(a.asHomogeneousVector(), b.asHomogeneousVector());
	}
	
	public Optional<LineSegment> projectLineSegment(Point3D end) {
		return projectLineSegment(new Point3D(0, 0, 0), end);
	}
	
	/**
	 * Projects a line segment from world space to screen space.
	 * Performs frustum clipping and culling during clip space.
	 * @param a
	 * @param b
	 * @return 
	 */
	public Optional<LineSegment> projectLineSegment(SimpleMatrix a, SimpleMatrix b) {
		SimpleMatrix clipSpaceA = projectToClipSpace(a);
		SimpleMatrix clipSpaceB = projectToClipSpace(b);
		
		
		//Frustum clipping (if (-w <= (x, y) <= w and 0 <= z <= w) then the point is valid. If it's outside the w's, then it's clipped, see http://www.songho.ca/opengl/gl_projectionmatrix.html )
		
		//Do frustum clipping/culling:
		SimpleMatrix[] clipped = clipLine(clipSpaceA, clipSpaceB);
		if (clipped == null) {
			return Optional.empty();
		}
		
		Point3D viewA = ViewportTransformation.fromClipSpaceToScreenSpace(clipped[0], Game.WIDTH, Game.HEIGHT);
		Point3D viewB = ViewportTransformation.fromClipSpaceToScreenSpace(clipped[1], Game.WIDTH, Game.HEIGHT);
		
		return Optional.of(new LineSegment(viewA, viewB));
	}
	
	/**
	 * Performs line clipping in clip space coordinates.
	 * Moves the point that is outside of the viewing frustum
	 * to the intersection point of the line and frustum.
	 * If both points are outside the frustum, null is returned.
	 * @param A
	 * @param B
	 * @return 
	 */
	private SimpleMatrix[] clipLine(SimpleMatrix a, SimpleMatrix b) {
		if (bothPointsInside(a, b)) {
			return new SimpleMatrix[] {a, b};
		}
		
		//TODO: this breaks it, find out why:
		/*a = Point3D.fromMatrixDivideByW(a).asHomogeneousVector();
		b = Point3D.fromMatrixDivideByW(b).asHomogeneousVector();*/
		
		
		//LEFT
		SimpleMatrix pointOnLeftPlane = new Point3D(-1, 0, 0).asHomogeneousVector();
		if (a.get(0) < -a.get(3)) { //a is outside left plane and will be moved to the plane (this is: x < -w, where w itself will be positive)
			a = HelperFunctions.intersectionPointWithPlane(pointOnLeftPlane, frustum.left, a, b);
		} else if (b.get(0) < -b.get(3)) { //b is outside left plane and will be moved to the plane
			b = HelperFunctions.intersectionPointWithPlane(pointOnLeftPlane, frustum.left, a, b);
		}
		
		if (a == null || b == null) return null; //If one of them is null, then the whole line segment was outside of the plane
		
		//RIGHT
		SimpleMatrix pointOnRightPlane = new Point3D(1, 0, 0).asHomogeneousVector();
		if (a.get(0) > a.get(3)) { //a is outside right plane and will be moved to the plane (this is: x > w)
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
		if (a.get(2) < 0) { //z is from 0 to 1, so between 0 and w.
			a = HelperFunctions.intersectionPointWithPlane(pointOnNearPlane, frustum.near, a, b);
		} else if (b.get(2) < 0) {
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
	
	
	/**
	 * Transforms the faces from world space to screen space.
	 * @param faces
	 * @return 
	 */
	public List<Renderable> projectFaces(List<Face> faces) {
		List<Renderable> transformed = new ArrayList<>();
		for (Face face : faces) {
			
			//Transform face to view space:
			face = face.applyMatrix(cam.getViewMatrix());
			//Backface culling:
			if (face.getFaceNormal().dot(face.getAverageLocation()) >= 0) { //If the normal points to the same direction as camera, we see the back of the face.
				continue;
			}
			
			//To clip space:
			face = face.applyMatrix(projectionMatrix);
			//Frustum culling (all points outside) (TODO: plane can still be visible even if all points are out):
			boolean allOutside = true;
			for (SimpleMatrix p : face.points) {
				if (pointInside(p)) {
					allOutside = false;
				}
			}
			if (allOutside) {
				continue;
			}
			
			//TODO: do frustum clipping here (should remove the above after this is done)
			
			face = ViewportTransformation.fromClipSpaceToScreenSpace(face, Game.WIDTH, Game.HEIGHT);
			transformed.add(face);
		}
		return transformed;
	}
	
	private static boolean bothPointsInside(SimpleMatrix a, SimpleMatrix b) {
		return pointInside(a) && pointInside(b);
	}
	
	private static boolean pointInside(SimpleMatrix a) {
		return componentInside(a.get(0), a.get(3)) && componentInside(a.get(1), a.get(3)) && a.get(2) >= 0 && a.get(2) <= a.get(3);
	}
	
	private static boolean componentInside(double a, double w) {
		return a >= -w && a <= w;
	}
	
	public SimpleMatrix getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public SimpleMatrix getViewMatrix() {
		return cam.getViewMatrix();
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
	
	public SimpleMatrix fromClipSpaceToWorldSpace(Point3D p) {
		SimpleMatrix invertProjection = projectionMatrix.invert();
		SimpleMatrix invertViewMatrix = cam.getViewMatrix().invert();
		
		SimpleMatrix transformed = invertProjection.mult(p.asHomogeneousVector());
		transformed = invertViewMatrix.mult(transformed);
		
		return transformed;
	}
	
	public double getProjectedSizeMultiplier(Point3D location) {
		Point3D start = project(location, false);
		Point3D end = project(location.add(cam.getLeft()), false);
		
		return start.getAs2D().subtract(end.getAs2D()).magnitude();
	}
	
	public double getProjectedSize(Point3D location, double size) {
		return getProjectedSizeMultiplier(location) * size;
	}
}
