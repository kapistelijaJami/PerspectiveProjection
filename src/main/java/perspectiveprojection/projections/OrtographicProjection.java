package perspectiveprojection.projections;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;

public class OrtographicProjection extends Projection {
	public OrtographicProjection(Camera cam) {
		super(cam);
	}
	
	@Override
	public Point2D project(Point3D point) {
		SimpleMatrix v = new SimpleMatrix(new double[][]{{point.x}, {point.y}, {point.z}, {1}});
		SimpleMatrix res = viewMatrix.mult(v);
		
		return new Point2D(res.get(0), -res.get(1));
	}
}
