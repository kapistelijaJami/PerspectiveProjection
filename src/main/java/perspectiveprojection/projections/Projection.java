package perspectiveprojection.projections;

import java.awt.Point;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Frustum;
import perspectiveprojection.Game;
import perspectiveprojection.HelperFunctions;
import perspectiveprojection.LineSegment;
import perspectiveprojection.Point3D;
import perspectiveprojection.ViewportTransformation;

public abstract class Projection {
	protected Camera cam;
	protected SimpleMatrix projectionMatrix = SimpleMatrix.identity(4);
	protected Frustum frustum; //Viewing frustum in clip space
	
	public Projection(Camera cam) {
		this.cam = cam;
	}
	
	public Point3D project(SimpleMatrix point) {
		return project(Point3D.fromMatrix(point));
	}
	
	public abstract Point3D project(Point3D point);
	
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
		SimpleMatrix v = point; //p is in world space
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
	
	public LineSegment projectLineSegment(SimpleMatrix a, SimpleMatrix b) {
		SimpleMatrix clipA = projectToClipSpace(a);
		SimpleMatrix clipB = projectToClipSpace(b);
		
		//Do frustum culling:
		SimpleMatrix[] clipped = clipLine(clipA, clipB);
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
	
	/*public final void updateViewMatrix() {
		//double yaw = Math.toRadians(cam.getYaw());
		//double pitch = Math.toRadians(cam.getPitch());
		Point3D loc = cam.getLoc();
		System.out.println("loc " + loc + " yaw " + cam.getYaw() + " pitch " + cam.getPitch());
		//System.out.println("cosyaw " + Math.cos(yaw) + " sinyaw " + Math.sin(yaw));
		//System.out.println("cospit " + Math.cos(pitch) + " sinpit " + Math.sin(pitch));
		
		//viewMatrix = new SimpleMatrix(new double[][] {
		//			{ Math.cos(yaw) * Math.cos(pitch),  Math.sin(pitch), Math.sin(yaw) * Math.cos(pitch), -loc.x },
		//			{ Math.cos(yaw) * Math.sin(pitch),  -Math.cos(pitch), Math.sin(yaw) * Math.sin(pitch), -loc.y },
		//			{                  -Math.sin(yaw),                0,                   Math.cos(yaw), -loc.z },
		//			{                               0,                0,                               0,      1 }
		//		});
		
		//viewMatrix = HelperFunctions.getRotateYThenXTranslateMatrix(cam.getPitch(), cam.getYaw(), cam.getLoc().negated());
		
		//SimpleMatrix traslateMatrix = new SimpleMatrix(new double[][] {
		//			{ 1, 0, 0, loc.x },
		//			{ 0, 1, 0, loc.y },
		//			{ 0, 0, 1, loc.z },
		//			{ 0, 0, 0,     1 }
		//		});
		
		//viewMatrix = HelperFunctions.getRotateYThenXMatrix(-cam.getPitch(), -cam.getYaw()).mult(traslateMatrix); //Confirmed that translate is first when doing this way.
		
		//viewMatrix = new SimpleMatrix(new double[][] {
		//			{ Math.cos(yaw) * Math.cos(pitch), -Math.sin(yaw), Math.cos(yaw) * Math.sin(pitch), 0 },
		//			{ Math.sin(yaw) * Math.cos(pitch),  Math.cos(yaw), Math.sin(yaw) * Math.sin(pitch), 0 },
		//			{                -Math.sin(pitch),              0,                 Math.cos(pitch), 0 },
		//			{                          -loc.x,         -loc.y,                          -loc.z, 1 }
		//		});
		
		// wrong way:
		//viewMatrix = new SimpleMatrix(
		//		new double[][] {
		//			{ Math.cos(yaw) * Math.cos(pitch), Math.cos(yaw) * Math.sin(pitch), -Math.sin(yaw),  0 },
		//			{                 Math.sin(pitch),                -Math.cos(pitch),              0,  0 },
		//			{ Math.sin(yaw) * Math.cos(pitch), Math.sin(yaw) * Math.sin(pitch),  Math.cos(yaw),  0 },
		//			{                          -loc.x,                          -loc.y,         -loc.z,  1 }
		//		});
	}*/
	
	public abstract SimpleMatrix getProjectionMatrix();
}
