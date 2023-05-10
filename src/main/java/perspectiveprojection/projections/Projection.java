package perspectiveprojection.projections;

import java.awt.Point;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.HelperFunctions;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;

public abstract class Projection {
	protected Camera cam;
	
	public Projection(Camera cam) {
		this.cam = cam;
	}
	
	public abstract Point2D project(Point3D point);
	
	public Point2D[] projectPoints(Point3D... points) {
		Point2D[] projectedPoints = new Point2D[points.length];
		
		for (int i = 0; i < points.length; i++) {
			projectedPoints[i] = project(points[i]);
		}
		
		return projectedPoints;
	}
	
	public Point[] projectPointsInt(Point3D... points) {
		Point[] projectedPoints = new Point[points.length];
		
		for (int i = 0; i < points.length; i++) {
			projectedPoints[i] = project(points[i]).asPoint();
		}
		
		return projectedPoints;
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
