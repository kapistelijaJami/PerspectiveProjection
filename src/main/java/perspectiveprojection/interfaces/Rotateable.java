package perspectiveprojection.interfaces;

import org.ejml.simple.SimpleMatrix;

public interface Rotateable {
	public SimpleMatrix getModelMatrix();
	public void setModelMatrix(SimpleMatrix modelMatrix);
	
	public default void rotate(SimpleMatrix rotate) { //TODO: If we do multiple rotations, now we have to apply them in reverse order to be correct, fix this.
		setModelMatrix(getModelMatrix().mult(rotate)); //this way so that model matrix is applied last, because it includes the translate information. If other way, it would rotate around origo, and the location information is lost from the last column.
	}
}
