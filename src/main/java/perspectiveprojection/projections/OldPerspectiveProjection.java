package perspectiveprojection.projections;

import java.awt.Point;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;

public class OldPerspectiveProjection extends Projection {
	// The distance of the viewer from the image plane
	private static final double D = 1000;
	
	public OldPerspectiveProjection(Camera cam) {
		super(cam);
	}
	
	@Override
	public Point2D project(Point3D p) {
		SimpleMatrix v = new SimpleMatrix(new double[][]{{p.x}, {p.y}, {p.z}, {1}});
		SimpleMatrix res = viewMatrix.mult(v);
		p = new Point3D(res.get(0), res.get(1), res.get(2));

		double x = p.x * D / (D - p.z);
		double y = p.y * D / (D - p.z);
		return new Point2D(x, -y);
	}
}
