package perspectiveprojection;

import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import org.ejml.simple.SimpleMatrix;

public class Cube {
	private SimpleMatrix modelMatrix; //Converts the object from model space to world space. Contains the information for object location, scale and rotation.
	//private double cubeSize;
	
	//points are between negative cubeSize and positive cubeSize
	private Point3D[] starts = new Point3D[12];
	private Point3D[] ends = new Point3D[12];
	
	public Cube(double cubeSize) { //if cube size is 100, then the whole cube is 200x200x200, because it will be -100 to 100 around the offset that's sent to render.
		//this.cubeSize = cubeSize;
		modelMatrix = SimpleMatrix.diag(cubeSize, cubeSize, cubeSize, 1);
		
		int offset = 1;
		
		//4 corners that are not touching each other all have 3 unique lines. Those define the whole cube.
		//So 3 lines start from the same point, then the next point same thing etc. starts[0] connects to ends[0] etc.
		starts[0] = new Point3D(offset, offset, offset);
		starts[1] = starts[0].copy();
		starts[2] = starts[0].copy();
		starts[3] = new Point3D(-offset, offset, -offset);
		starts[4] = starts[3].copy();
		starts[5] = starts[3].copy();
		starts[6] = new Point3D(-offset, -offset, offset);
		starts[7] = starts[6].copy();
		starts[8] = starts[6].copy();
		starts[9] = new Point3D(offset, -offset, -offset);
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
	
	public void setLocation(Point3D loc) {
		modelMatrix.setColumn(3, 0, loc.x, loc.y, loc.z);
	}
	
	public void render(Graphics2D g, Projection projection) {
		//color this corner red:
		Point3D vertex = new Point3D(1, 1, 1);
		
		/*LineSegment p = projection.projectLineSegment(modelMatrix.mult(starts[1].asHomogeneousMatrix()), modelMatrix.mult(ends[1].asHomogeneousMatrix()));
		if (p == null) {
			System.out.println("tuli");
			return;
		} else {
			//g.setColor(Color.BLUE);
			//g.fillOval((int) p.getEnd().x, (int) p.getEnd().y, 3, 3);
		}*/
		
		
		
		for (int i = 0; i < starts.length; i++) {
			Point3D start = starts[i];
			Point3D end = ends[i];
			
			SimpleMatrix worldS = modelMatrix.mult(start.asHomogeneousVector());
			SimpleMatrix worldE = modelMatrix.mult(end.asHomogeneousVector());
			
			LineSegment line = projection.projectLineSegment(Point3D.fromMatrix(worldS), Point3D.fromMatrix(worldE));
			
			
			if (line == null) { //the whole line is outside the near and far clipping planes
				continue;
			}
			
			Point3D s = line.getStart();
			Point3D e = line.getEnd();
			
			Paint paint = new GradientPaint(s.getAs2DInt(), Color.white, e.getAs2DInt(), Color.white);
			if (start.equals(vertex)) {
				paint = new GradientPaint(s.getAs2DInt(), Color.red, e.getAs2DInt(), Color.white);
			} else if (end.equals(vertex)) {
				paint = new GradientPaint(s.getAs2DInt(), Color.white, e.getAs2DInt(), Color.red);
			}
			
			g.setPaint(paint);
			g.drawLine((int) s.x, (int) s.y, (int) e.x, (int) e.y);
			
			
			g.setColor(Color.LIGHT_GRAY);
			g.fillOval((int) s.x - 3, (int) s.y - 3, 6, 6);
			g.fillOval((int) e.x - 3, (int) e.y - 3, 6, 6);
		}
	}
}
