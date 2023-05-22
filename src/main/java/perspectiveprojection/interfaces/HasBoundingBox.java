package perspectiveprojection.interfaces;

import perspectiveprojection.primitives.BoundingBox;
import perspectiveprojection.interfaces.HasListOfPoints;

public interface HasBoundingBox extends HasListOfPoints {
	public default BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBox(this);
	}
}
