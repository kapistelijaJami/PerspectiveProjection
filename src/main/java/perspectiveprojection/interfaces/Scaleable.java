package perspectiveprojection.interfaces;

import org.ejml.simple.SimpleMatrix;

public interface Scaleable {
	public SimpleMatrix getModelMatrix();
	public void setModelMatrix(SimpleMatrix modelMatrix);
	
	public default void scale(double scalar) {
		setModelMatrix(getModelMatrix().mult(SimpleMatrix.diag(scalar, scalar, scalar, 1)));
	}
	
	public default void scale(double x, double y, double z) {
		setModelMatrix(getModelMatrix().mult(SimpleMatrix.diag(x, y, z, 1)));
	}
}
