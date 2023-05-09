package perspectiveprojection;

import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

public class Cube {
	private double cubeSize;
	
	//points are between negative cubeSize and positive cubeSize
	private Point3D[] starts = new Point3D[12];
	private Point3D[] ends = new Point3D[12];
	
	public Cube(double cubeSize) { //if cube size is 100, then the whole cube is 200x200x200, because it will be -100 to 100 around the offset that's sent to render.
		this.cubeSize = cubeSize;
		
		//4 corners that are not touching each other all have 3 unique lines. Those define the whole cube.
		//So 3 lines start from the same point, then the next point same thing etc. starts[0] connects to ends[0] etc.
		starts[0] = new Point3D(cubeSize, cubeSize, cubeSize);
		starts[1] = starts[0].copy();
		starts[2] = starts[0].copy();
		starts[3] = new Point3D(-cubeSize, cubeSize, -cubeSize);
		starts[4] = starts[3].copy();
		starts[5] = starts[3].copy();
		starts[6] = new Point3D(-cubeSize, -cubeSize, cubeSize);
		starts[7] = starts[6].copy();
		starts[8] = starts[6].copy();
		starts[9] = new Point3D(cubeSize, -cubeSize, -cubeSize);
		starts[10] = starts[9].copy();
		starts[11] = starts[9].copy();
		
		//first we reflect x to the other side of origo (negate it), then y, then z for the 3 end points per start point
		for (int i = 0; i < 12; i++) {
			Point3D p = starts[i].copy();
			int mod = i % 3;
			
			if (mod == 0) {
				ends[i] = p.negateX();
			} else if (mod == 1) {
				ends[i] = p.negateY();
			} else if (mod == 2) {
				ends[i] = p.negateZ();
			}
		}
	}
	
	public void render(Graphics2D g, Point3D offset, Projection projection) {
		//color this corner red:
		Point3D vertex = new Point3D(1, 1, 1);
		vertex.mult(cubeSize);
		
		for (int i = 0; i < starts.length; i++) {
			Point3D start = starts[i];
			Point3D end = ends[i];
			
			Point2D[] projected = projection.projectPoints(start.copy().add(offset.x, offset.y, offset.z), end.copy().add(offset.x, offset.y, offset.z));
			Point s = projected[0].asPoint();
			Point e = projected[1].asPoint();
			
			Paint paint = new GradientPaint(s, Color.white, e, Color.white);
			if (start.equals(vertex)) {
				paint = new GradientPaint(s, Color.red, e, Color.white);
			} else if (end.equals(vertex)) {
				paint = new GradientPaint(s, Color.white, e, Color.red);
			}
			
			g.setPaint(paint);
			g.drawLine(s.x, s.y, e.x, e.y);
		}
	}
}
