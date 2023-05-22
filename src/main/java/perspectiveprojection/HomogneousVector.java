package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class HomogneousVector {
	public double x, y, z, w;
	
	public HomogneousVector() {
		this(0, 0, 0);
	}
	
	public HomogneousVector(double x, double y, double z) {
		this(x, y, z, 1);
	}
	
	public HomogneousVector(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public static HomogneousVector fromMatrix(SimpleMatrix m) {
		return new HomogneousVector(m.get(0), m.get(1), m.get(2), m.get(3));
	}
	
	public SimpleMatrix asMatrix() {
		return new SimpleMatrix(new double[][] {{x}, {y}, {z}, {w}});
	}
	
	public static HomogneousVector getX() {
		return new HomogneousVector(1, 0, 0, 1);
	}
	
	public static HomogneousVector getUP() {
		return new HomogneousVector(0, 1, 0, 1);
	}
	
	public static HomogneousVector getY() {
		return getUP();
	}
	
	public static HomogneousVector getZ() {
		return new HomogneousVector(0, 0, 1, 1);
	}
	
	public HomogneousVector copy() {
		return new HomogneousVector(x, y, z, w);
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
		final HomogneousVector o = (HomogneousVector) obj;
		
		return this.x == o.x && this.y == o.y && this.z == o.z && this.w == o.w;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.w) ^ (Double.doubleToLongBits(this.w) >>> 32));
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
	
	//TODO: not sure how to do the addition and subtraction etc with the w component
	/*public HomogneousVector add(double a) {
		return add(a, a, a);
	}
	
	public HomogneousVector add(HomogneousVector o) {
		return add(o.x, o.y, o.z);
	}
	
	public HomogneousVector add(double x, double y, double z) {
		return new HomogneousVector(this.x + x, this.y + y, this.z + z, this.w);
	}

	public HomogneousVector subtract(double a) {
		return subtract(a, a, a);
	}
	
	public HomogneousVector subtract(Point3D o) {
		return subtract(o.x, o.y, o.z);
	}
	
	public HomogneousVector subtract(double x, double y, double z) {
		return new HomogneousVector(this.x - x, this.y - y, this.z - z, this.w);
	}
	
	public HomogneousVector mult(double mult) {
		return new HomogneousVector(this.x * mult, this.y * mult, this.z * mult, this.w);
	}
	
	public HomogneousVector divide(double a) {
		if (a == 0) {
			return this;
		}
		return new HomogneousVector(this.x / a, this.y / a, this.z / a, this.w);
	}*/
	
	public double get(int i) {
		if (i == 0) {
			return x;
		}
		if (i == 1) {
			return y;
		}
		if (i == 2) {
			return z;
		}
		if (i == 3) {
			return w;
		}
		return 0;
	}
	
	//distance from origo
	public double magnitude() {
		return HelperFunctions.pythagoras3D(x, y, z);
	}
	
	/**
	 * Normalizes the vector, so its length is 1.
	 * Updates this point, doesn't create a copy.
	 * Returns this.
	 * @return 
	 */
	public HomogneousVector normalize() {
		double magnitude = magnitude();
		if (magnitude > 0) {
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}
		return this;
	}
	
	public HomogneousVector normalized() {
		double magnitude = magnitude();
		return new HomogneousVector(x / magnitude, y / magnitude, z / magnitude, w);
	}
	
	public HomogneousVector normalizeByW() {
		x /= w;
		y /= w;
		z /= w;
		return this;
	}
	
	public HomogneousVector normalizedByW() {
		return copy().normalizeByW();
	}
	
	public HomogneousVector negate() {
		x *= -1;
		y *= -1;
		z *= -1;
		return this;
	}
	
	public HomogneousVector negated() {
		return copy().negate();
	}
	
	public double dot(HomogneousVector p) {
		return x * p.x + y * p.y + z * p.z; //I do not include w component at the dot product
	}
}
