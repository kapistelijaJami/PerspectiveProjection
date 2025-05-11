package perspectiveprojection.primitives;

import perspectiveprojection.linear_algebra.Point3D;

public class Plane {
	private Point3D normal;
	private double distance; //distance from origo (I would think this is signed distance, and positive direction is where the normal points)
	
	public Plane(Point3D normal, double distance) {
		this.normal = normal.normalize();
		this.distance = distance;
	}
	
	public  boolean isPointInFrontOfPlane(Point3D point) {
		Point3D toPoint = point.subtract(getPlanePoint());
		return normal.dot(toPoint) > 0;
	}
	
	public Point3D getPlanePoint() {
		return normal.mult(distance); //TODO: check if you need to negate distance (- Don't think so if the signed distance is to the direction of the normal)
	}
}
