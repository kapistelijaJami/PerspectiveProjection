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
		//Column vectors disregarding the last row are right, up, forward and location negated.
		viewMatrix = new SimpleMatrix(new double[][] {
					{  1, 0, 0, -loc.x }, //x is negated because it's the right from the camera's perspective (I think it has to be left)
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
	
	//AS COLUMN VECTORS
	/*@Deprecated
	public Point3D getRight() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(1, 0), viewMatrix.get(2, 0));
	}
	
	public Point3D getLeft() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(1, 0), viewMatrix.get(2, 0));
	}
	
	public Point3D getUp() {
		return new Point3D(viewMatrix.get(0, 1), viewMatrix.get(1, 1), viewMatrix.get(2, 1));
	}
	
	public Point3D getForward() {
		return new Point3D(viewMatrix.get(0, 2), viewMatrix.get(1, 2), viewMatrix.get(2, 2));
	}
	
	@Deprecated
	private void setRight(Point3D right) {
		viewMatrix.setColumn(0, 0, right.x, right.y, right.z);
	}
	
	private void setLeft(Point3D left) {
		viewMatrix.setColumn(0, 0, left.x, left.y, left.z);
	}
	
	private void setUp(Point3D up) {
		viewMatrix.setColumn(1, 0, up.x, up.y, up.z);
	}
	
	private void setForward(Point3D forward) {
		viewMatrix.setColumn(2, 0, forward.x, forward.y, forward.z);
	}*/
	
	
	//AS ROW VECTORS (this should be correct, since as column/basis vectors they transform unit vectors to these bases,
	//which means that points will move to the wrong direction. It has to be the inverse, and transpose is inverse of rotation matrix (I think))
	//But I probably need this forward to point to the positive Z instead of the direction of the camera, which is negative Z,
	//otherwise the camera turns everything around anyway and we will end up looking towards positive Z.
	//(Then the left has to change as well I think)
	@Deprecated
	public Point3D getRight() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(0, 1), viewMatrix.get(0, 2));
	}
	
	public Point3D getLeft() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(0, 1), viewMatrix.get(0, 2));
	}
	
	public Point3D getUp() {
		return new Point3D(viewMatrix.get(1, 0), viewMatrix.get(1, 1), viewMatrix.get(1, 2));
	}
	
	public Point3D getForward() {
		return new Point3D(viewMatrix.get(2, 0), viewMatrix.get(2, 1), viewMatrix.get(2, 2));
	}
	
	@Deprecated
	private void setRight(Point3D right) {
		viewMatrix.setRow(0, 0, right.x, right.y, right.z);
	}
	
	private void setLeft(Point3D left) {
		viewMatrix.setRow(0, 0, left.x, left.y, left.z);
	}
	
	private void setUp(Point3D up) {
		viewMatrix.setRow(1, 0, up.x, up.y, up.z);
	}
	
	private void setForward(Point3D forward) {
		viewMatrix.setRow(2, 0, forward.x, forward.y, forward.z);
	}
	
	public void setLoc(Point3D loc) {
		viewMatrix.setColumn(3, 0, -loc.x, -loc.y, -loc.z);
	}
	
	/**
	 * Moves the camera forward by the amount.
	 * Negative moves backwards.
	 * @param amount 
	 */
	public void moveForward(double amount) {
		setLoc(getLoc().add(getForward().mult(amount)));
		System.out.println(getLoc());
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
		setLoc(getLoc().add(getLeft().mult(-amount)));
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
	
	public void turn2(double amount) {
		Point3D forward = getForward();
		setDir(forward.rotatedY(-amount));
	}
	
	/**
	 * Turns vertically amount degrees.
	 * Positive to the up, negative to the down.
	 * @param amount 
	 */
	public void pitch(double amount) {
		Point3D forward = getForward();
		Point3D left = getLeft();
		setDir(forward.rotateAroundAxis(left, -amount)); //amount negated so that positive is to up
	}
	
	/**
	 * Sets the direction based off the input vector.
	 * @param forward 
	 */
	public final void setDir(Point3D forward) {
		//This uses the Gram-Schmidt orthogonalization to keep up pointing upwards etc.
		forward = forward.normalized();
		setForward(forward);
		
		Point3D left = getUp().cross(forward).normalized();
		setLeft(left);
		
		Point3D up = forward.cross(left).normalized();
		setUp(up);
	}
	
	public final void lookAt(Point3D point) {
		Point3D newDir = point.subtract(getLoc()).normalized();
		setDir(newDir);
	}
	
	public SimpleMatrix getViewMatrix() {
		return viewMatrix;
	}
}
