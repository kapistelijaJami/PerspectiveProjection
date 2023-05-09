package perspectiveprojection.projections;

import java.awt.Point;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;

public class PerspectiveProjection extends Projection {
	private SimpleMatrix projectionMatrix = SimpleMatrix.identity(4);
	
	public PerspectiveProjection(Camera cam) {
		super(cam);
		
		double fov = 60; // def: 90
		double aspect = 9.0 / 16.0;
		double zNear = 0.1;
		double zFar = 10000;
		double fovMult = 1.0 / Math.tan(Math.toRadians(fov / 2.0));
		double lambda = zFar / (zFar - zNear);
		
		System.out.println("lambda: " + lambda + " zNear: " + zNear);
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{ aspect * fovMult,       0,      0,               0 },
					{                0, fovMult,      0,               0 },
					{                0,       0, lambda, -lambda * zNear },
					{                0,       0,      1,               0 }
				});
	}

	@Override
	public Point2D project(Point3D p) {
		//SimpleMatrix wholeProjection = projectionMatrix.mult(viewMatrix);
		
		SimpleMatrix v = new SimpleMatrix(new double[][]{{p.x}, {p.y}, {p.z}, {1}});
		
		SimpleMatrix res = projectionMatrix.mult(viewMatrix.mult(v));
		Point3D result = new Point3D(res.get(0), res.get(1), res.get(2));
		double w = res.get(3);
		
		if (w != 0.0) {
			result.x /= w;
			result.y /= w;
			result.z /= w;
		}
		
		//System.out.print(p + " -> " + result);
		
		//width = 1280, height = 720
		result.x = ((result.x + 1) / 2.0) * 1280;
		result.y = ((result.y + 1) / 2.0) * 720;
		
		//System.out.println(" -> " + result);
		
		return new Point2D(result.x, result.y);
	}
}
