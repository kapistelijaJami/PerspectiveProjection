package perspectiveprojection.linear_algebra;

import java.awt.Point;
import perspectiveprojection.util.HelperFunctions;

public class Point2D {
	public double x, y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point2D copy() {
		return new Point2D(x, y);
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public Point2D add(double val) {
		return add(val, val);
	}
	
	public Point2D add(Point2D p) {
		return add(p.x, p.y);
	}
	
	public Point2D add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Point2D subtract(double a) {
		return subtract(a, a);
	}
	
	public Point2D subtract(Point2D o) {
		return subtract(o.x, o.y);
	}
	
	public Point2D subtract(double x, double y) {
		return new Point2D(this.x - x, this.y - y);
	}
	
	public Point2D mult(double mult) {
		x *= mult;
		y *= mult;
		return this;
	}
	
	public Point2D mult(Point2D p) {
		x *= p.x;
		y *= p.y;
		return this;
	}
	
	public Point2D div(Point2D p) {
		x /= p.x;
		y /= p.y;
		return this;
	}
	
	//distance from origo
	public double magnitude() {
		return HelperFunctions.pythagoras(x, y);
	}
	
	public Point2D normalize() {
		double magnitude = magnitude();
		if (magnitude > 0) {
			x /= magnitude;
			y /= magnitude;
		}
		return this;
	}
	
	public Point2D normalized() {
		return copy().normalize();
	}
	
	public Point2D negate() {
		x *= -1;
		y *= -1;
		return this;
	}
	
	public Point2D negated() {
		return copy().negate();
	}
	
	public double dot(Point2D p) {
		return x * p.x + y * p.y;
	}
	
	public Point2D perpendicular(boolean right) {
		Point2D p = new Point2D(y, -x);
		
		return right ? p : p.negate();
	}
	
	public boolean isPerpendicular(Point2D p) {
		if (isZero() || p.isZero()) {
			return false;
		}
		return dot(p) == 0;
	}
	
	public boolean isSameDirection(Point2D p) {
		return dot(p) > 0;
	}
	
	public boolean isOppositeDirection(Point2D p) {
		return dot(p) < 0;
	}
	
	public boolean isParallel(Point2D p) {
		if (isZero() || p.isZero()) {
			return false;
		}
		return cross(p) == 0;
	}
	
	public boolean isZero() {
		return x == 0 && y == 0;
	}
	
	/**
	 * Length of the would be 3D cross product (which is perpendicular to both of the vectors).
	 * Also the area of the parallellogram made by the two vectors.
	 * Is positive, when this is on the right side of p. Negative otherwise.
	 * If 0, vectors are parallel, or at least one vector has a magnitude of 0.
	 * @param p
	 * @return 
	 */
	public double cross(Point2D p) {
		return x * p.y - p.x * y;
	}
	
	/**
	 * Returns the angle between the two vectors.
	 * Formula works for unit vectors, that's why it normalizes them first.
	 * @param p
	 * @return 
	 */
	public double angleBetween(Point2D p) {
		return Math.toDegrees(Math.acos(normalized().dot(p.normalized()))); //Source: https://www.youtube.com/watch?v=DPfxjQ6sqrc
	}
	
	/**
	 * Returns the angle between the two vectors.
	 * Assumes the vectors are unit vectors (length 1).
	 * Doesn't use many computationally intensive calculations. (Only acos, which is a must anyway)
	 * @param p
	 * @return 
	 */
	public double angleBetweenUnitVectors(Point2D p) {
		return Math.toDegrees(Math.acos(dot(p))); //Source: https://www.youtube.com/watch?v=DPfxjQ6sqrc
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public Point asPoint() {
		return new Point((int) x, (int) y);
	}
}
