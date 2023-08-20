package perspectiveprojection.camera;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.util.HelperFunctions;
import perspectiveprojection.linear_algebra.Point3D;

public class Camera {
	private Point3D location;
	private SimpleMatrix viewMatrix;	//This contains the information how points are translated and oriented (in that order), so that they
										//are being viewed from the camera's pov, it can be used straight in the projection calculation.
										//4x4 matrix. The first 3 elements of the first 3 row vectors are the -left, up and -forward vectors.
										//In projection you need to translate points by subtracting the location of the camera to set the new
										//origo where the camera is and then rotate the view so that the camera points straight to either +Z or -Z).
										//The last column does not have the correct negated location information for the camera, because the translate
										//in this matrix is applied first and then the rotation. The translate information will be combined in the
										//last column with the rotation information, and wont match the coordinates anymore.
										//We could either extract the coordinates from the matrix by inverse rotation, or keep track of
										//them separately, which is done with the location variable.
	
	public double orbitPointDistance = -1;
	
	public Camera() {
		this(new Point3D());
	}
	
	public Camera(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Camera(Point3D loc) {
		location = loc;
		
		//Row vectors disregarding the last column are -left, up, -forward and last column is -location.
		//This is the inverse matrix of how you would position the camera from model space to world space.
		//Inverse of a rotation matrix is same as transpose, that's why they are row vectors, instead of column.
		viewMatrix = new SimpleMatrix(new double[][] {
					{ -1, 0,  0, -loc.x },
					{  0, 1,  0, -loc.y },
					{  0, 0, -1, -loc.z },
					{  0, 0,  0,      1 }
				});
		
		if (!loc.isOrigo()) {
			lookAt(new Point3D(0, 0, 0));
		}
	}
	
	public Point3D getLoc() {
		return location;
	}
	
	//AS ROW VECTORS (since as column/basis vectors they transform unit vectors to these bases,
	//which means that points will move to the wrong direction. It has to be the inverse, and transpose is inverse of rotation matrix)
	//Forward and left will be negated for the matrix so that the view will end up pointing to the negative Z.
	public Point3D getLeft() {
		return new Point3D(viewMatrix.get(0, 0), viewMatrix.get(0, 1), viewMatrix.get(0, 2)).negate();
	}
	
	public Point3D getUp() {
		return new Point3D(viewMatrix.get(1, 0), viewMatrix.get(1, 1), viewMatrix.get(1, 2));
	}
	
	public Point3D getForward() {
		return new Point3D(viewMatrix.get(2, 0), viewMatrix.get(2, 1), viewMatrix.get(2, 2)).negate();
	}
	
	private void setLeft(Point3D left) {
		viewMatrix.setRow(0, 0, -left.x, -left.y, -left.z);
		updateMatrix();
	}
	
	private void setUp(Point3D up) {
		viewMatrix.setRow(1, 0, up.x, up.y, up.z);
		updateMatrix();
	}
	
	private void setForward(Point3D forward) {
		viewMatrix.setRow(2, 0, -forward.x, -forward.y, -forward.z);
		updateMatrix();
	}
	
	public void setLoc(Point3D loc) {
		location = loc;
		updateMatrix();
	}
	
	/**
	 * Only calculates the last column of the matrix.
	 * Other elements can be set manually.
	 * This needs to be called even after rotations, because
	 * the last column will be affected by the rotation information.
	 */
	private void updateMatrix() {
		//The top left 3x3 submatrix won't change when location changes, but for the last column,
		//we need to also apply the rotation, because it happens after the translation.
		//We can do so by just calculating the last column manually with dot product.
		//Also if the rotation changes, we still have to calculate the last column even though location didn't change.
		
		//a.x * -b.x + a.y * -b.y + a.z * -b.z
		//is the same result as
		//-(a.x * b.x + a.y * b.y + a.z * b.z)
		//So a.dot(-b) = -(a.dot(b))
		
		double x = -Point3D.fromMatrix(viewMatrix.extractVector(true, 0)).dot(location);
		double y = -Point3D.fromMatrix(viewMatrix.extractVector(true, 1)).dot(location);
		double z = -Point3D.fromMatrix(viewMatrix.extractVector(true, 2)).dot(location);
		
		viewMatrix.setColumn(3, 0, x, y, z);
	}
	
	/**
	 * Moves the camera forward by the amount.
	 * Negative moves backwards.
	 * @param amount 
	 */
	public void moveForward(double amount) {
		setLoc(getLoc().add(getForward().mult(amount)));
		if (orbitPointDistance != -1) {
			orbitPointDistance -= amount;
		}
	}
	
	/**
	 * Moves the camera up in the world space.
	 * @param amount 
	 */
	public void moveUpWorld(double amount) {
		setLoc(getLoc().add(Point3D.getUP().mult(amount)));
	}
	
	/**
	 * Moves the camera up relative to the camera orientation.
	 * @param amount 
	 */
	public void moveUp(double amount) {
		setLoc(getLoc().add(getUp().mult(amount)));
	}
	
	/**
	 * Moves the camera right relative to the camera orientation.
	 * Positive is right, negative is left.
	 * @param amount 
	 */
	public void moveLeft(double amount) {
		setLoc(getLoc().add(getLeft().mult(amount)));
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
		Point3D left = getLeft();
		
		Point3D UP = Point3D.getUP();
		if (amount < 0) { //pitching down, so we want DOWN vector instead
			UP.negate();
		}
		double maxAngle = Math.max(0, Point3D.angleBetweenUnitVectors(forward, UP) - 1); //Stays 1 degree off from vertical
		amount = HelperFunctions.clamp(amount, -maxAngle, maxAngle);
		
		setDir(forward.rotateAroundAxis(left, -amount)); //amount negated so that positive is to up
	}
	
	public void orbitAroundPoint(Point3D point, double amountYaw, double amountPitch) {
		Point3D forward = getForward();
		Point3D left = getLeft(); //This has to be variable, because invert is before it's used
		
		Point3D UP = Point3D.getUP();
		if (amountPitch < 0) { //pitching down, so we want DOWN vector instead
			UP.negate();
		}
		double maxAngle = Math.max(0, Point3D.angleBetweenUnitVectors(forward, UP) - 1); //Stays 1 degree off from vertical
		amountPitch = HelperFunctions.clamp(amountPitch, -maxAngle, maxAngle);
		
		
		viewMatrix = viewMatrix.invert();
		
		SimpleMatrix rotationMatrix = HelperFunctions.getRotationMatrixAroundY4By4(-amountYaw);
		rotationMatrix = rotationMatrix.mult(HelperFunctions.getRotationMatrixAroundAxis4By4(left, -amountPitch));
		SimpleMatrix translationByPoint = HelperFunctions.getTranslationMatrix(point.negated());
		SimpleMatrix translateBack = HelperFunctions.getTranslationMatrix(point);
		
		viewMatrix = translationByPoint.mult(viewMatrix);
		viewMatrix = rotationMatrix.mult(viewMatrix);
		viewMatrix = translateBack.mult(viewMatrix);
		
		location = Point3D.fromMatrix(viewMatrix.extractVector(false, 3));
		viewMatrix = viewMatrix.invert();
	}
	
	/**
	 * Sets the direction based off the input vector.
	 * @param forward 
	 */
	public final void setDir(Point3D forward) {
		//This uses the Gram-Schmidt orthogonalization to keep up pointing upwards etc.
		forward = forward.normalized();
		setForward(forward);
		
		Point3D left = Point3D.getUP().cross(forward).normalized(); //Using (0, 1, 0) as the UP in this calculation prevent camera from tilting.
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
	
	/**
	 * Calculates the current pitch.
	 * Positive is up, negative is down. Horizontal is 0, 90 is up etc.
	 * @return 
	 */
	public double getPitch() {
		return Math.toDegrees(Math.asin(getForward().y));
	}
	
	/**
	 * Calculates the current yaw.
	 * Positive is to the right, negative to the left.
	 * 
	 * @return 
	 */
	public double getYaw() {
		Point3D forward = getForward().negate();
		return Math.toDegrees(-Math.atan2(forward.x, forward.z));
	}
	
	public void setYawAndPitch(double yaw, double pitch) {
		pitch = HelperFunctions.clamp(pitch, -89, 89);
		
		double cosPitch = Math.cos(Math.toRadians(pitch));
		double sinPitch = Math.sin(Math.toRadians(pitch));
		double cosYaw = Math.cos(Math.toRadians(yaw));
		double sinYaw = Math.sin(Math.toRadians(yaw));
		
		double x = cosPitch * sinYaw;
		double y = sinPitch;
		double z = cosPitch * -cosYaw;
		
		setDir(new Point3D(x, y, z));
	}
}
