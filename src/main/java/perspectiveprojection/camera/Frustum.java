package perspectiveprojection.camera;

import org.ejml.simple.SimpleMatrix;

public class Frustum {
	public SimpleMatrix top, bottom, left, right, near, far; //normals
	
	public Frustum(SimpleMatrix top, SimpleMatrix bottom, SimpleMatrix left, SimpleMatrix right, SimpleMatrix near, SimpleMatrix far) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.near = near;
		this.far = far;
	}
}
