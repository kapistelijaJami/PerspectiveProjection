package perspectiveprojection;


public abstract class GameObject implements HasBoundingBox, Selectable, Hoverable {
	public MoveArrows moveArrows;
	protected boolean hovering = false;
	
	@Override
	public void hover() {
		hovering = true;
	}
	
	public void unhover() {
		hovering = false;
	}
	
	public abstract void setLocation(Point3D loc);
	public abstract Point3D getLocation();
	
	public void moveUp(double amount) {
		setLocation(getLocation().add(Point3D.getUP().mult(amount)));
	}
	
	public void moveRight(double amount) {
		setLocation(getLocation().add(Point3D.getX().mult(amount)));
	}
	
	public void moveForward(double amount) {
		setLocation(getLocation().add(Point3D.getZ().mult(amount)));
	}
}
