package perspectiveprojection.projections;

import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Camera;
import perspectiveprojection.Frustum;
import perspectiveprojection.Game;
import perspectiveprojection.HelperFunctions;
import perspectiveprojection.Point2D;
import perspectiveprojection.Point3D;
import perspectiveprojection.ViewportTransformation;

/*
	(see http://www.songho.ca/opengl/gl_transform.html)
	Projection pipeline goes like this:
	
	First we have the object space, which are the points of the object relative to its local origin.
	Then we have the world space, which are points of the objects relative to the world origo.
		To get to the world space, you just apply translation by the location of the object's local origin, called model transform,
		or just keep track of this in the first place.
		It could also do rotations and scaling to the whole object.
	Then we have view space AKA eye space or camera space, which are the points relative to the camera location. This is the POV of the camera.
		To get to the view space, you apply viewMatrix. It just moves all the points so that camera is at the origo, and
		rotates all the points around the camera so that the viewing direction is how the camera would see it.
		Camera looks towards the negative Z direction. So points infront of the camera will have negative z values.
	Then we have clip space, which are the points in homogeneous coordinates, not yet divided by w.
		To get to the clip space, you apply projectionMatrix. It projects the points to the near plane/projection plane.
		It doesn't have the finished values in x, y and z yet, they still need to be divided by w, which contains the original z value of
		the viewSpace, which is what creates the perspective effect.
		Here we would do the frustum culling / clipping (if  -w < x, y, z < w then the point is valid. If it's outside the w's, then it's culled, see http://www.songho.ca/opengl/gl_projectionmatrix.html)
	Then we have Normalized Device Coordinates (NDC), which is after we divide by w.
		The values are between -1 and 1 for x and y, and 0 and 1 for z. If they are not, they should be outside of the viewing frustum.
	The last one is screen space / window space. These are the 2D coordinates that can be rendered to screen.
		To get to these we do viewport transformation. The NDC are scaled and translated to fit in the rendering screen.
		x = -1 in NDC will be 0 in screen space, and x = 1 in NDC will be screen width in screen space etc.
		y value should be mapped so that decreasing y in NDC is increasing y in screen space, since usually y grows down when rendering in 2D.
		You could ignore the z coordinate, it doesn't affect the rendering location, but it tells which points are closest to the camera.
*/

public class PerspectiveProjection extends Projection {
	/*public PerspectiveProjection(Camera cam) {
		super(cam);
		
		//source: https://www.youtube.com/watch?v=EqNcqBdrNyI
		//Very good, especially with the opengl guide: https://www.youtube.com/watch?v=U0_ONQQ5ZNM
		//This seems pretty good: https://www.youtube.com/watch?v=LhQ85bPCAJ8 (uses left handed coordinates and camera looks at positive z though I think)
		double fov = 60; // def: 90. This should be vertical fov (I think)
		double aspect = 9.0 / 16.0; //for some reason this is height / width (I think if it's the other way, it multiplies the y instead, so in [1, 1] in the matrix)
		double zNear = 0.1; //maybe these 2 needs to be negative?
		double zFar = 10000;
		double fovMult = 1.0 / Math.tan(Math.toRadians(fov / 2.0)); //How the field of view scales the objects. (Figher fov = more objects = smaller objects, and the other way around)
		double lambda = zFar / (zFar - zNear);
		
		//lambda - lambda * zNear is separated inside the matrix.
		
		//System.out.println("lambda: " + lambda);
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{ aspect * fovMult,       0,      0,               0 },
					{                0, fovMult,      0,               0 },
					{                0,       0, lambda, -lambda * zNear },
					{                0,       0,      1,               0 }
				});
	}*/
	
	//NEW VERSION USING OPENGL GUIDE: http://www.songho.ca/opengl/gl_projectionmatrix.html
	public PerspectiveProjection(Camera cam) {
		super(cam);
		
		//These depend on how big and far your objects are. These have same units:
		double n = 1; //distance to near plane.
		double f = 10000; //distance to far plane.
		
		
		//p subscript is projected value, e subscript is eyeSpace/viewSpace
		//Xp is projected X on near plane, Ze is point's z location in eyeSpace
		//Xp = -n * Xe / Ze   =   n * Xe / -Ze
		//Yp = -n * Ye / Ze   =   n * Ye / -Ze
		//Zp = -n
		
		//I calculated these myself, differs slightly from opengl one, because I had Zclip going from 0 to 1 instead of -1 to 1
		double A = f / (n - f);
		double B = f * n / (n - f);
		
		double fov = 60; //vertical fov (If you want to use horizontal fov, you just calculate right first, and either divide the right with horizontal aspect, or multiply it with vertical aspect to get top.)
		double aspect = Game.WIDTH / (double) Game.HEIGHT; //horizontal aspect ratio (only used to calculate the right distance from the vertical fov)
		
		//Calculate top and right (from the center of the near plane) with the field of view and aspect ratio:
		double top = n * Math.tan(Math.toRadians(fov / 2.0)); //half fov for the right triangle and to get the distance from center of near to the edge, we just want the half fov.
		double right = aspect * top;
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{  n / right,        0,     0,    0 },
					{          0,  n / top,     0,    0 },
					{          0,        0,     A,    B },
					{          0,        0,    -1,    0 }
				});
		
		// params: left, right, bottom, top, near, far
		// Frustum(-right, right, -top, top, n, f); //Near and far might have to be negated
		
		calculateViewingFrustumFromProjectionMatrix();
	}
}
