package perspectiveprojection;

public interface HasBoundingBox extends HasListOfPoints {
	public default BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBox(this);
	}
}
