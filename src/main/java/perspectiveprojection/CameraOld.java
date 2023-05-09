package perspectiveprojection;

public class CameraOld {
	private Point3D loc;
	private double yaw, pitch; //pitch = positive is nose up. yaw = positive is nose left (we could negate this)
	
	public CameraOld() {
		this(new Point3D());
	}
	
	public CameraOld(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public CameraOld(Point3D loc) {
		this.loc = loc;
		
		if (!loc.isOrigo()) {
			lookAt(new Point3D(0, 0, 0));
		}
	}
	
	public CameraOld(Point3D loc, double yaw, double pitch) {
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
