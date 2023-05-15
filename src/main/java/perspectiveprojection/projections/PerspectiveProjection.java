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
(see http://www.songho.ca/opengl/gl_transform.html )
Projection pipeline goes like this:
	
	First we have the object space / local space / model space, which are the points of the object relative to its local origin.
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
		the view space, which is what creates the perspective effect.
		Here we would do the frustum culling / clipping (if  -w < x, y, z < w then the point is valid. If it's outside the w's, then it's culled, see http://www.songho.ca/opengl/gl_projectionmatrix.html)
		The homogeneous coordinates basically have the perspective divide done already, the clip space is just w times bigger than the NDC is. 
		And because the w coordinate for homogeneous coordinates will always be used to divide the other coordinates anyway when tranforming it to
		normal coordinates, it already has the perspective effect applied.
	Then we have Normalized Device Coordinates (NDC), which is after we divide by w.
		The values are between -1 and 1 for x and y, and 0 and 1 for z. If they are not, they are outside of the viewing frustum.
		But in that case they would have already been clipped in clip space.
	The last one is screen space / window space. These are the 2D coordinates that can be rendered to screen.
		To get to these we do viewport transformation. The NDC are scaled and translated to fit in the rendering screen.
		x = -1 in NDC will be 0 in screen space, and x = 1 in NDC will be screen width in screen space etc.
		y value should be mapped so that decreasing y in NDC is increasing y in screen space, since usually y grows down when rendering in 2D.
		The z component doesn't affect the rendering location, but it tells which points are closest to the camera,
		so you can decide which poins get rendered first, or occlusion culled away (but I think this should be done earlier).
*/
public class PerspectiveProjection extends Projection {
	//Some sources: https://www.youtube.com/watch?v=EqNcqBdrNyI
	//Very good, especially with the opengl guide: https://www.youtube.com/watch?v=U0_ONQQ5ZNM
	//This seems pretty good: https://www.youtube.com/watch?v=LhQ85bPCAJ8 (uses left handed coordinates and camera looks at positive z though)
	
	//NEW VERSION USING OPENGL GUIDE: http://www.songho.ca/opengl/gl_projectionmatrix.html
	public PerspectiveProjection(Camera cam) {
		super(cam);
		
		//These depend on how big and far your objects are. These have same units:
		double n = 1; //distance to near plane.
		double f = 10000; //distance to far plane.
		
		
		//E is point in eyeSpace/viewSpace, P is projected point (the point gets projected to near plane in x and y direction)
		//Px is projected X on near plane, Ez is point's z location in eEySpace
		//Px = -n * Ex / Ez   =   n * Ex / -Ez
		//Py = -n * Ey / Ez   =   n * Ey / -Ez
		//Pz = -n
		
		//I calculated these myself, differs slightly from opengl one, because I had Zclip going from 0 to 1 instead of -1 to 1
		double A = f / (n - f);
		double B = n * A; //was n * f / (n - f)
		
		double fov = 60; //vertical fov (If you want to use horizontal fov, you just calculate right first, and either divide the right with horizontal aspect, or multiply it with vertical aspect to get top.)
		double aspect = Game.WIDTH / (double) Game.HEIGHT; //horizontal aspect ratio (only used to calculate the right distance from the vertical fov)
		
		//Calculate top and right (from the center of the near plane to the edge) with the field of view and aspect ratio:
		double top = n * Math.tan(Math.toRadians(fov / 2.0)); //half fov for the right triangle and to get the distance from center of near to the edge, we just want the half fov.
		double right = aspect * top;
		
		projectionMatrix = new SimpleMatrix(new double[][] {
					{  n / right,        0,     0,    0 },
					{          0,  n / top,     0,    0 },
					{          0,        0,     A,    B },
					{          0,        0,    -1,    0 }
				});
		
		calculateViewingFrustumFromProjectionMatrix();
	}
}
