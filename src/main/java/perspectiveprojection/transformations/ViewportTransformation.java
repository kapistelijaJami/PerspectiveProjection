package perspectiveprojection.transformations;

import perspectiveprojection.linear_algebra.Point3D;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.linear_algebra.Point2D;

public class ViewportTransformation {
	public static Point3D fromClipSpaceToScreenSpace(Point3D point, int width, int height) {
		return fromClipSpaceToScreenSpace(point.asHomogeneousVector(), width, height);
	}
	
	/**
	 * Transforms the point from clip space to screen space.
	 * The point is in homogeneous coordinates.
	 * @param point
	 * @param width
	 * @param height
	 * @return 
	 */
	public static Point3D fromClipSpaceToScreenSpace(SimpleMatrix point, int width, int height) {
		Point3D p = Point3D.fromMatrix(point);
		double w = point.get(3);
		
		if (w != 0.0) {
			p = p.divide(w); //NDC / image space (x and y should be between -1 and 1, and z should be between 0 and 1)
		}
		
		p.x = (width * p.x + width) / 2;
		p.y = (height * -p.y + height) / 2; //This flips the coordinates for y
		
		return p;
	}
	
	public static Face fromClipSpaceToScreenSpace(Face face, int width, int height) {
		Face f = face.copyWithoutPoints();
		for (SimpleMatrix p : face.getListOfPoints()) {
			f.addPoint(fromClipSpaceToScreenSpace(p, width, height).asHomogeneousVector());
		}
		return f;
	}
	
	public static Point3D fromScreenSpaceToClipSpace(Point2D point, int width, int height) {
		double x = (point.x * 2 - width) / width;
		double y = (point.y * 2 - height) / -height;
		
		return new Point3D(x, y, 0);
	}
}
