package perspectiveprojection.projections;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;

public class OrtographicProjection extends Projection {
	private SimpleMatrix projectionMatrix = SimpleMatrix.identity(4);
	
	public OrtographicProjection(Camera cam) {
		super(cam);
		
		double n = 0.1; //distance to near plane.
		double f = 10000; //distance to far plane.
		
		
		//I calculated these myself, differs slightly from opengl one, because I had Zclip going from 0 to 1 instead of -1 to 1
		double A = 2 / (n - f);
		double B = n / (n - f);
		
		//Straight from opengl (doesn't seem to be any better):
		/*A = -2 / (f - n);
		B = -(f + n) / (f - n);*/
		
		double aspect = 16.0 / 9.0; //horizontal aspect ratio (only used to calculate the right distance from the vertical fov)
		
		//Calculate top and right (from the center of the near plane). Use aspect ratio to convert vertical to horizontal:
		double t = 1000;
		double r = aspect * t;
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{    1 / r,       0,      0,     0 },
					{        0,   1 / t,      0,     0 },
					{        0,       0,      A,     B },
					{        0,       0,      0,     1 }
				});
		
		//This should just map the viewSpace frustum between the -1 and 1 values for x and y, and 0 and 1 for z
		/*
		Zn = 2 / (n - f) * Ze + n / (n - f)
		*/
	}
	
	@Override
	public Point2D project(Point3D point) {
		SimpleMatrix res = cam.getViewMatrix().mult(point.asHomogeneousMatrix());
		
		SimpleMatrix NDCSpace = projectionMatrix.mult(res);
		Point3D result = Point3D.fromMatrix(NDCSpace);
		
		int width = 1280;
		int height = 720;
		result.x = (width * result.x + width) / 2;
		result.y = (height * -result.y + height) / 2; //This should flip the coordinates for y
		
		//System.out.println(result);
		return new Point2D(result.x, result.y);
	}

	@Override
	public SimpleMatrix getProjectionMatrix() {
		return projectionMatrix;
	}
}
