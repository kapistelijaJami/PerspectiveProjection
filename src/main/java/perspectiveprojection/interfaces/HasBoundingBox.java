package perspectiveprojection.interfaces;

import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.primitives.BoundingBox;

public interface HasBoundingBox extends HasListOfPoints {
	public abstract BoundingBoxType getBoundingBoxType();
	
	public default BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBox(this);
	}
}
