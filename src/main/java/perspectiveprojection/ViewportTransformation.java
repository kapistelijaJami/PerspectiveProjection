package perspectiveprojection;

import org.ejml.simple.SimpleMatrix;

public class ViewportTransformation {
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
}
