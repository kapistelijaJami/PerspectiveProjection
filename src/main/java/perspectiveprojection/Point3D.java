package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class Point3D {
	public double x, y, z;
	
	public Point3D() {}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D copy() {
		return new Point3D(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Point3D o = (Point3D) obj;
		
		return this.x == o.x && this.y == o.y && this.z == o.z;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public Point3D add(double add) {
		return add(add, add, add);
	}
	
	public Point3D add(Point3D o) {
		return add(o.x, o.y, o.z);
	}
	
	public Point3D add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Point3D mult(double mult) {
		x *= mult;
		y *= mult;
		z *= mult;
		return this;
	}
	
	public Point3D rotatedX(double degrees) { //rotates with right hand rule (thumb towards x-axis and curled fingers are positive)
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{1, 0, 0},
					{0, Math.cos(rad), -Math.sin(rad)},
					{0, Math.sin(rad), Math.cos(rad)}
				});
		
		SimpleMatrix res = m.mult(new SimpleMatrix(new double[][] {{x}, {y}, {z}}));
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	public Point3D rotatedY(double degrees) { //rotates with right hand rule (thumb towards y-axis and curled fingers are positive)
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{Math.cos(rad), 0, Math.sin(rad)},
					{0, 1, 0},
					{-Math.sin(rad), 0, Math.cos(rad)}
				});
		
		SimpleMatrix res = m.mult(new SimpleMatrix(new double[][] {{x}, {y}, {z}}));
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	public Point3D rotatedZ(double degrees) { //rotates with right hand rule (thumb towards z-axis and curled fingers are positive)
		double rad = Math.toRadians(degrees);
		SimpleMatrix m = new SimpleMatrix(
				new double[][] {
					{Math.cos(rad), -Math.sin(rad), 0},
					{Math.sin(rad), Math.cos(rad), 0},
					{0, 0, 1}
				});
		
		SimpleMatrix res = m.mult(new SimpleMatrix(new double[][] {{x}, {y}, {z}}));
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	//distance from origo
	public double magnitude() {
		return HelperFunctions.pythagoras3D(x, y, z);
	}
	
	public Point3D normalize() {
		double magnitude = magnitude();
		if (magnitude > 0) {
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}
		return this;
	}
	
	public Point3D negate() {
		x *= -1;
		y *= -1;
		z *= -1;
		return this;
	}
	
	public Point3D negated() {
		return copy().negate();
	}
	
	public Point3D normalWithUp() {
		return cross(new Point3D(0, 1, 0)).normalize();
	}
	
	public double dot(Point3D p) {
		return x * p.x + y * p.y + z * p.z;
	}
	
	public Point3D cross(Point3D p) {
		return new Point3D(y * p.z - p.y * z, p.x * z - x * p.z, x * p.y - p.x * y);
	}

	public boolean isOrigo() {
		return x == 0 && y == 0 && z == 0;
	}
	
	public Point3D negateX() {
		this.x *= -1;
		return this;
	}
	
	public Point3D negateY() {
		this.y *= -1;
		return this;
	}
	
	public Point3D negateZ() {
		this.z *= -1;
		return this;
	}
}
