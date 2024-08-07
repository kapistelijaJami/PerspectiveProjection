package perspectiveprojection.objects;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.HasBoundingBox;
import perspectiveprojection.interfaces.Hoverable;
import perspectiveprojection.interfaces.Selectable;
import perspectiveprojection.primitives.BoundingBox;

public abstract class GameObject implements HasBoundingBox, Selectable, Hoverable {
	protected SimpleMatrix modelMatrix; //Converts the object from model space to world space. Contains the information for object location, scale and rotation.
	public MoveArrows moveArrows;
	private BoundingBox boundingBox;
	protected double size;
	
	public GameObject() {
		this(1);
	}
	
	public GameObject(double size) {
		modelMatrix = SimpleMatrix.diag(size, size, size, 1);
		this.size = size;
	}
	
	public Point3D getLocation() {
		return Point3D.fromMatrix(modelMatrix.extractVector(false, 3)); //TODO: see if you need to keep track of location in a vector if rotation doesn't work otherwise.
	}
	
	public void setLocation(Point3D loc) {
		modelMatrix.setColumn(3, 0, loc.x, loc.y, loc.z);
		
		if (getBoundingBoxType() == BoundingBoxType.AXIS_ALIGNED_BOX) {
			boundingBox = HasBoundingBox.super.getBoundingBox();
		} else {
			boundingBox = BoundingBox.createBoundingBoxAroundPoint(getLocation(), size, BoundingBoxType.SPHERE);
		}
	}
	
	public void moveUp(double amount) { //Up is towards positive Y axis
		setLocation(getLocation().add(Point3D.getUP().mult(amount)));
	}
	
	public void moveLeft(double amount) { //Left is towards positive X axis
		setLocation(getLocation().add(Point3D.getX().mult(amount)));
	}
	
	public void moveForward(double amount) { //Forward is towards positive z axis
		setLocation(getLocation().add(Point3D.getZ().mult(amount)));
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			if (getBoundingBoxType() == BoundingBoxType.AXIS_ALIGNED_BOX) {
				boundingBox = HasBoundingBox.super.getBoundingBox();
			} else {
				boundingBox = BoundingBox.createBoundingBoxAroundPoint(getLocation(), size, BoundingBoxType.SPHERE);
			}
		}
		return boundingBox;
	}
	
	public SimpleMatrix getModelMatrix() {
		return modelMatrix;
	}
	
	public void setModelMatrix(SimpleMatrix modelMatrix) {
		this.modelMatrix = modelMatrix;
	}
}
