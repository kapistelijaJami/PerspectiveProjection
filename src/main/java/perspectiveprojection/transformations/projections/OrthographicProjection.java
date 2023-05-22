package perspectiveprojection.transformations.projections;

import perspectiveprojection.transformations.projections.Projection;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.camera.Camera;
import perspectiveprojection.Game;

public class OrthographicProjection extends Projection {
	public OrthographicProjection(Camera cam) {
		super(cam);
		
		double n = 0.1; //distance to near plane.
		double f = 10000; //distance to far plane.
		
		//I calculated these myself, differs slightly from opengl one, because I had Zclip going from 0 to 1 instead of -1 to 1
		double A = 2 / (n - f);
		double B = n / (n - f);
		
		double aspect = Game.WIDTH / (double) Game.HEIGHT; //horizontal aspect ratio (only used to calculate the right distance from the top distance)
		
		//Calculate top and right (from the center of the near plane). Use aspect ratio to convert vertical to horizontal:
		double t = 1000;
		double r = aspect * t;
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{    1 / r,       0,      0,     0 },
					{        0,   1 / t,      0,     0 },
					{        0,       0,      A,     B },
					{        0,       0,      0,     1 }
				});
		
		//This should just map the viewSpace frustum values for x and y to -1 and 1, and for z to 0 and 1.
		/*
		Zn = 2 / (n - f) * Ze + n / (n - f)
		*/
		
		calculateViewingFrustumFromProjectionMatrix();
	}
}
