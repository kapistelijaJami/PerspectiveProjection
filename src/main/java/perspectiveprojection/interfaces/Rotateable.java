package perspectiveprojection.interfaces;

import org.ejml.simple.SimpleMatrix;

public interface Rotateable {
	public SimpleMatrix getModelMatrix();
	public void setModelMatrix(SimpleMatrix modelMatrix);
	
	//TODO: If we do multiple rotations, now we have to apply them in reverse order to be correct, fix this. Yeah, new rotations need to be on the left, but on the right of the model matrix. Might need to separate rotation matrix or something?
	//Or we would need to keep track of the location and when rotating tranform it back to origo and then rotate and transform back to where it would be in world space. (So this to the model matrix)
	public default void rotate(SimpleMatrix rotate) {
		setModelMatrix(getModelMatrix().mult(rotate)); //this way so that model matrix is applied last, because it includes the translate information. If other way, it would rotate around origo, and the location information is lost from the last column.
	}
}
