package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class Camera {
	private SimpleMatrix viewMatrix;	//This contains the location and the orientation + it can be used straight in the projection calculation.
										//4x4 matrix. The first 3 column vectors without the last row are the right, up and forward vectors.
										//The last column without the last row is the location negated (because in projection you need to
										//translate points by subtracting the location of the camera to set the new origo where the camera is).
	
	public Camera() {
		this(new Point3D());
	}
	
	public Camera(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Camera(Point3D loc) {
		viewMatrix = new SimpleMatrix(new double[][] {
					{ -1, 0, 0, -loc.x }, //x is negated because it's the right from the camera's perspective
					{  0, 1, 0, -loc.y },
					{  0, 0, 1, -loc.z },
					{  0, 0, 0,      1 }
				});
		
		if (!loc.isOrigo()) {
			lookAt(new Point3D(0, 0, 0));
		}
	}
	
	public Point3D getLoc() {
		return new Point3D(-viewMatrix.get(0, 3), -viewMatrix.get(1, 3), -viewMatrix.get(2, 3));
	}
	
	public Point3D getRight() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(1, 0), viewMatrix.get(2, 0));
	}
	
	public Point3D getUp() {
		return new Point3D(viewMatrix.get(0, 1), viewMatrix.get(1, 1), viewMatrix.get(2, 1));
	}
	
	public Point3D getForward() {
		return new Point3D(viewMatrix.get(0, 2), viewMatrix.get(1, 2), viewMatrix.get(2, 2));
	}
	
	public void setLoc(Point3D loc) {
		viewMatrix.setColumn(3, 0, -loc.x, -loc.y, -loc.z);
	}
	
	private void setForward(Point3D forward) {
		viewMatrix.setColumn(2, 0, forward.x, forward.y, forward.z);
	}
	
	private void setUp(Point3D up) {
		viewMatrix.setColumn(1, 0, up.x, up.y, up.z);
	}
	
	private void setRight(Point3D right) {
		viewMatrix.setColumn(0, 0, right.x, right.y, right.z);
	}
	
	/**
	 * Moves the camera forward by the amount.
	 * Negative moves backwards.
	 * @param amount 
	 */
	public void moveForward(double amount) {
		setLoc(getLoc().add(getForward().mult(amount)));
	}
	
	/**
	 * Moves the camera up in the world space.
	 * @param amount 
	 */
	public void moveUp(double amount) {
		setLoc(getLoc().add(Point3D.getUP().mult(amount)));
	}
	
	/**
	 * Moves the camera right relative to the camera orientation.
	 * Positive is right, negative is left.
	 * @param amount 
	 */
	public void moveRight(double amount) {
		setLoc(getLoc().add(getRight().mult(amount)));
	}
	
	/**
	 * Turns horizontally amount degrees.
	 * Positive to the right, negative to the left.
	 * @param amount 
	 */
	public void turn(double amount) {
		Point3D forward = getForward();
		Point3D up = getUp();
		setDir(forward.rotateAroundAxis(up, -amount)); //amount negated so that positive is to the right
	}
	
	/**
	 * Turns vertically amount degrees.
	 * Positive to the up, negative to the down.
	 * @param amount 
	 */
	public void pitch(double amount) {
		Point3D forward = getForward();
		Point3D right = getRight();
		setDir(forward.rotateAroundAxis(right, amount));
	}
	
	/**
	 * Sets the direction based off the input vector.
	 * @param forward 
	 */
	public final void setDir(Point3D forward) {
		//This uses the Gram-Schmidt orthogonalization to keep up pointing upwards etc.
		forward = forward.normalized();
		setForward(forward);
		
		Point3D right = forward.cross(getUp()).normalize();
		setRight(right);
		
		Point3D up = right.cross(forward).normalize();
		setUp(up);
	}
	
	public final void lookAt(Point3D point) {
		Point3D newDir = point.copy().subtract(getLoc()).normalize();
		setDir(newDir);
	}
	
	public SimpleMatrix getViewMatrix() {
		return viewMatrix;
	}
}
