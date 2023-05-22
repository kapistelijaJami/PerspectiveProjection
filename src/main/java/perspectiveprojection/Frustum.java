package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class Frustum {
	public SimpleMatrix top, bottom, left, right, near, far;
	
	/*public Frustum(SimpleMatrix top, SimpleMatrix bottom, SimpleMatrix left, SimpleMatrix right, SimpleMatrix near, SimpleMatrix far) {
		this.top = HelperFunctions.normalize4DVector(top);
		this.bottom = HelperFunctions.normalize4DVector(bottom);
		this.left = HelperFunctions.normalize4DVector(left);
		this.right = HelperFunctions.normalize4DVector(right);
		this.near = HelperFunctions.normalize4DVector(near);
		this.far = HelperFunctions.normalize4DVector(far);
	}*/
	
	public Frustum(SimpleMatrix top, SimpleMatrix bottom, SimpleMatrix left, SimpleMatrix right, SimpleMatrix near, SimpleMatrix far) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.near = near;
		this.far = far;
	}
}
