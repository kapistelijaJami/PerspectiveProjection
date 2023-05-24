package perspectiveprojection.objects;

import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.HasBoundingBox;
import perspectiveprojection.interfaces.Hoverable;
import perspectiveprojection.interfaces.Selectable;


public abstract class GameObject implements HasBoundingBox, Selectable, Hoverable {
	public MoveArrows moveArrows;
	
	public abstract void setLocation(Point3D loc);
	public abstract Point3D getLocation();
	
	public void moveUp(double amount) { //Up is towards positive Y axis
		setLocation(getLocation().add(Point3D.getUP().mult(amount)));
	}
	
	public void moveLeft(double amount) { //Left is towards positive X axis
		setLocation(getLocation().add(Point3D.getX().mult(amount)));
	}
	
	public void moveForward(double amount) { //Forward is towards positive z axis
		setLocation(getLocation().add(Point3D.getZ().mult(amount)));
	}
}
