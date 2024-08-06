package perspectiveprojection.interfaces;

import perspectiveprojection.primitives.BoundingBox;

public interface HasBoundingBox extends HasListOfPoints {
	public default BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBox(this);
	}
}
