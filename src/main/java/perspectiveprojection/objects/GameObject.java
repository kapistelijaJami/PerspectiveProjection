package perspectiveprojection.objects;

import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.HasBoundingBox;
import perspectiveprojection.interfaces.Hoverable;
import perspectiveprojection.interfaces.Selectable;
import perspectiveprojection.objects.MoveArrows;


public abstract class GameObject implements HasBoundingBox, Selectable, Hoverable {
	public MoveArrows moveArrows;
	
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
