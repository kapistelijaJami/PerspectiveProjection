package perspectiveprojection;

import java.awt.Point;
import org.ejml.simple.SimpleMatrix;

public class Point3D {
	public double x, y, z; //Mostly used as immutable unless specified otherwise.
	
	public Point3D() {
		this(0, 0, 0);
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Point3D getX() {
		return new Point3D(1, 0, 0);
	}
	
	public static Point3D getUP() {
		return new Point3D(0, 1, 0);
	}
	
	public static Point3D getY() {
		return getUP();
	}
	
	public static Point3D getZ() {
		return new Point3D(0, 0, 1);
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
	
	public Point3D add(double a) {
		return add(a, a, a);
	}
	
	public Point3D add(Point3D o) {
		return add(o.x, o.y, o.z);
	}
	
	public Point3D add(double x, double y, double z) {
		return new Point3D(this.x + x, this.y + y, this.z + z);
	}

	public Point3D subtract(double a) {
		return subtract(a, a, a);
	}
	
	public Point3D subtract(Point3D o) {
		return subtract(o.x, o.y, o.z);
	}
	
	public Point3D subtract(double x, double y, double z) {
		return new Point3D(this.x - x, this.y - y, this.z - z);
	}
	
	public Point3D mult(double mult) {
		return new Point3D(this.x * mult, this.y * mult, this.z * mult);
	}
	
	public Point3D divide(double w) {
		if (w == 0) {
			return this;
		}
		return new Point3D(this.x / w, this.y / w, this.z / w);
	}
	
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
		return 0;
	}
	
	public Point3D rotatedX(double degrees) { //rotates with right hand rule (thumb towards x-axis and curled fingers are positive)
		SimpleMatrix m = HelperFunctions.getRotationMatrixAroundX3By3(degrees);
		
		SimpleMatrix res = m.mult(asMatrix());
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	public Point3D rotatedY(double degrees) { //rotates with right hand rule (thumb towards y-axis and curled fingers are positive)
		SimpleMatrix m = HelperFunctions.getRotationMatrixAroundY3By3(degrees);
		
		SimpleMatrix res = m.mult(asMatrix());
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	public Point3D rotatedZ(double degrees) { //rotates with right hand rule (thumb towards z-axis and curled fingers are positive)
		SimpleMatrix m = HelperFunctions.getRotationMatrixAroundZ3By3(degrees);
		
		SimpleMatrix res = m.mult(asMatrix());
		return new Point3D(res.get(0), res.get(1), res.get(2));
	}
	
	public Point3D rotateAroundAxis(Point3D axis, double degrees) { //rotates with right hand rule (thumb towards axis positive direction and curled fingers are positive)
		SimpleMatrix rotationMatrix = HelperFunctions.getRotationMatrixAroundAxis3By3(axis, degrees);
		
		SimpleMatrix res = rotationMatrix.mult(asMatrix());
		return new Point3D(res.get(0), res.get(1), res.get(2));
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
	public Point3D normalize() {
		double magnitude = magnitude();
		if (magnitude > 0) {
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}
		return this;
	}
	
	public Point3D normalized() {
		double magnitude = magnitude();
		return new Point3D(x / magnitude, y / magnitude, z / magnitude);
	}
	
	/**
	 * Negates the vector to point to the opposite direction.
	 * Updates this point, doesn't create a copy.
	 * Returns this.
	 * @return 
	 */
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
		return cross(new Point3D(0, 1, 0)).normalized();
	}
	
	public double dot(Point3D p) {
		return x * p.x + y * p.y + z * p.z;
	}
	
	/**
	 * Gets the perpendicular vector to both this and p.
	 * According to right hand rule where index finger is this, middle finger is p, and result is thumb.
	 * @param p
	 * @return 
	 */
	public Point3D cross(Point3D p) {
		return new Point3D(y * p.z - p.y * z, p.x * z - x * p.z, x * p.y - p.x * y);
	}
	
	public boolean isOrigo() {
		return x == 0 && y == 0 && z == 0;
	}
	
	public Point3D negateX() {
		return new Point3D(x * -1, y, z);
	}
	
	public Point3D negateY() {
		return new Point3D(x, y * -1, z);
	}
	
	public Point3D negateZ() {
		return new Point3D(x, y, z * -1);
	}
	
	public double distanceFrom(Point3D p) {
		return HelperFunctions.distance3D(this, p);
	}
	
	public SimpleMatrix asMatrix() {
		return new SimpleMatrix(new double[][] {{x}, {y}, {z}});
	}
	
	public SimpleMatrix asHomogeneousVector() {
		return asHomogeneousVector(1);
	}
	
	public SimpleMatrix asHomogeneousVector(double newW) {
		return new SimpleMatrix(new double[][] {{x}, {y}, {z}, {newW}});
	}
	
	public static Point3D fromMatrix(SimpleMatrix m) {
		return new Point3D(m.get(0), m.get(1), m.get(2));
	}
	
	public static Point3D fromMatrixDivideByW(SimpleMatrix m) {
		return new Point3D(m.get(0) / m.get(3), m.get(1) / m.get(3), m.get(2) / m.get(3));
	}
	
	/**
	 * Creates a new Point that is the orthogonal projection of this to p.
	 * @param p
	 * @return 
	 */
	public Point3D projectTo(Point3D p) {
		return p.mult(this.dot(p) / p.dot(p));
	}
	
	/**
	 * Returns the angle between the two vectors.
	 * Formula works for unit vectors, that's why it normalizes them first.
	 * @param p
	 * @return 
	 */
	public double angleBetween(Point3D p) {
		return angleBetweenUnitVectors(normalized(), p.normalized()); //Source: https://www.youtube.com/watch?v=DPfxjQ6sqrc
	}
	
	/**
	 * Returns the angle between the two vectors.
	 * Assumes the vectors are unit vectors (length 1).
	 * Doesn't use many computationally intensive calculations, like sqrt. (Only acos, which is a must anyway)
	 * @param a
	 * @param b
	 * @return 
	 */
	public static double angleBetweenUnitVectors(Point3D a, Point3D b) {
		return Math.toDegrees(Math.acos(a.dot(b))); //Source: https://www.youtube.com/watch?v=DPfxjQ6sqrc
	}
	
	public boolean isPerpendicular(Point3D p) {
		if (isZero() || p.isZero()) {
			return false;
		}
		return this.dot(p) == 0;
	}
	
	public boolean isSameDirection(Point3D p) {
		return this.dot(p) > 0;
	}
	
	public boolean isOppositeDirection(Point3D p) {
		return this.dot(p) < 0;
	}
	
	public boolean isParallel(Point3D p) {
		if (isZero() || p.isZero()) {
			return false;
		}
		return cross(p).isZero();
	}
	
	public boolean isZero() {
		return x == 0 && y == 0 && z ==0;
	}
	
	public Point2D getAs2D() {
		return new Point2D(x, y);
	}
	
	public Point getAs2DInt() {
		return new Point((int) x, (int) y);
	}

	public Point3D abs() {
		return new Point3D(Math.abs(x), Math.abs(y), Math.abs(z));
	}
}
