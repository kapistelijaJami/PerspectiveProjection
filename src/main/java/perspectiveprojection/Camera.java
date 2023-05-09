package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class Camera {
	//private SimpleMatrix viewMatrix;	//This contains the location and the orientation + it can be used straight in the projection calculation.
										//4x4 matrix. The first 3 column vectors without the last row are the right, up and forward vectors.
										//The last column without the last row is the location negated (because in projection you need to
										//translate points by subtracting the location of the camera to set the new origo where the camera is).
	
	private Point3D loc;
	private double yaw, pitch; //pitch = positive is nose up. yaw = positive is nose left (we could negate this)
	
	public Camera() {
		this(new Point3D());
	}
	
	public Camera(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Camera(Point3D loc) {
		this.loc = loc;
		/*viewMatrix = new SimpleMatrix(new double[][] {
					{ 1, 0, 0, -loc.x },
					{ 0, 1, 0, -loc.y },
					{ 0, 0, 1, -loc.z },
					{ 0, 0, 0,      1 }
				});*/
		
		if (!loc.isOrigo()) {
			lookAt(new Point3D(0, 0, 0));
		}
	}
	
	public Camera(Point3D loc, double yaw, double pitch) {
		this.loc = loc;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Point3D getLoc() {
		return loc;
	}
	
	public Point3D getYawPitchRoll() {
		return new Point3D(yaw, pitch, 0); //roll is always 0
	}
	
	public Point3D getDir() {
		return new Point3D(0, 0, 1).rotatedY(yaw).rotatedX(pitch).normalize();
	}
	
	public double getYaw() {
		return yaw;
	}
	
	public double getPitch() {
		return pitch;
	}
	
	public void setLoc(Point3D loc) {
		this.loc = loc;
	}
	
	public void setYaw(double yaw) {
		this.yaw = HelperFunctions.fixAngle180(yaw);
	}
	
	public void setPitch(double pitch) {
		this.pitch = HelperFunctions.fixAngle180(pitch);
		/*if () {
			
		}*/
		
		/*if (this.pitch > 90) { //clamps the angle so that Y cant point down.
			this.pitch = 90;
		}
		if (this.pitch < -90) {
			this.pitch = -90;
		}*/
	}
	
	/**
	 * Sets the direction based off the input vector.
	 * Direction is normalized.
	 * @param direction 
	 */
	public final void setDir(Point3D direction) {
		this.yaw = HelperFunctions.fixAngle180(Math.toDegrees(Math.atan2(direction.x, direction.z)));
		this.pitch = HelperFunctions.fixAngle180(Math.toDegrees(Math.atan2(direction.y, HelperFunctions.pythagoras(direction.x, direction.z))));
		System.out.println("cam, setting yaw and pitch: " + yaw + " " + pitch);
	}
	
	public final void lookAt(Point3D point) {
		setDir(new Point3D(point.x - loc.x, point.y - loc.y, point.z - loc.z).normalize());
	}
}
