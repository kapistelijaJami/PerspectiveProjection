package perspectiveprojection;

public class Plane {
	private Point3D normal;
	private double distance; //distance from origo
	
	public Plane(Point3D normal, double distance) {
		this.normal = normal.normalize();
		this.distance = distance;
	}
	
	public  boolean isPointInFrontOfPlane(Point3D point) {
		Point3D toPoint = point.subtract(getPlanePoint());
		return normal.dot(toPoint) > 0;
	}
	
	public Point3D getPlanePoint() {
		return normal.mult(distance); //TODO: check if you need to negate distance
	}
}
