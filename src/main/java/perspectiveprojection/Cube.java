package perspectiveprojection;

import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

public class Cube extends GameObject {
	private final SimpleMatrix modelMatrix; //Converts the object from model space to world space. Contains the information for object location, scale and rotation.

	private final List<Face> faces = new ArrayList<>();
	
	public Cube(double cubeSize, boolean multipleColors) { //if cube size is 100, then the cube is 100x100x100, it will be -50 to 50 around origo if no other transformations are added.
		modelMatrix = SimpleMatrix.diag(cubeSize / 2, cubeSize / 2, cubeSize / 2, 1);
		
		faces.add(new Face(new Point3D(-1, 1, 1), new Point3D(-1, -1, 1), new Point3D(1, -1, 1), new Point3D(1, 1, 1))); //front
		faces.add(new Face(new Point3D(1, 1, 1), new Point3D(1, -1, 1), new Point3D(1, -1, -1), new Point3D(1, 1, -1))); //right
		faces.add(new Face(new Point3D(-1, 1, -1), new Point3D(-1, -1, -1), new Point3D(-1, -1, 1), new Point3D(-1, 1, 1))); //left
		faces.add(new Face(new Point3D(1, 1, -1), new Point3D(1, -1, -1), new Point3D(-1, -1, -1), new Point3D(-1, 1, -1))); //back
		faces.add(new Face(new Point3D(-1, -1, -1), new Point3D(1, -1, -1), new Point3D(1, -1, 1), new Point3D(-1, -1, 1))); //bottom
		faces.add(new Face(new Point3D(-1, 1, -1), new Point3D(-1, 1, 1), new Point3D(1, 1, 1), new Point3D(1, 1, -1))); //top
		
		if (multipleColors) {
			faces.get(0).color = Color.RED;
			faces.get(1).color = Color.BLUE;
			faces.get(2).color = Color.ORANGE;
			faces.get(3).color = Color.PINK;
			faces.get(4).color = Color.CYAN;
			faces.get(5).color = Color.GREEN;
		}
		
		
		/*int offset = 1;
		
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
		}*/
	}
	
	@Override
	public Point3D getLocation() {
		return Point3D.fromMatrix(modelMatrix.extractVector(false, 3));
	}
	
	@Override
	public void setLocation(Point3D loc) {
		modelMatrix.setColumn(3, 0, loc.x, loc.y, loc.z);
	}
	
	public void renderWireframe(Graphics2D g, Projection projection) {
		if (hovering) {
			renderWireframe(g, projection, Color.YELLOW);
		}
		renderWireframe(g, projection, Color.RED);
	}
	
	public void renderWireframe(Graphics2D g, Projection projection, Color color) {
		for (Face face : getWorldSpaceFaces()) {
			
			LineSegment[] lines = face.getLines();
			for (LineSegment line : lines) {
				Point3D start = line.getStart();
				Point3D end = line.getEnd();
				line = projection.projectLineSegment(line);
				if (line == null) {
					continue;
				}
				
				int pointSize = 10;
				Double startSize = projection.getProjectedSize(start, pointSize);
				Double endSize = projection.getProjectedSize(end, pointSize);
				if (startSize == null) {
					startSize = (double) pointSize;
				}
				if (endSize == null) {
					endSize = (double) pointSize;
				}
				double sRadius = Math.max(startSize / 2, 5);
				double eRadius = Math.max(endSize / 2, 5);
				
				line.renderDots = false;
				line.render(g, color, sRadius, eRadius);
			}
		}
	}
	
	public List<Face> getLocalFaces() {
		return faces;
	}
	
	public List<Face> getWorldSpaceFaces() {
		List<Face> transformed = new ArrayList<>();
		
		for (Face face : faces) {
			transformed.add(face.applyMatrix(modelMatrix));
		}
		
		return transformed;
	}

	@Override
	public List<SimpleMatrix> getListOfPoints() {
		List<SimpleMatrix> points = new ArrayList<>();
		for (Face face : getWorldSpaceFaces()) {
			points.addAll(face.getListOfPoints());
		}
		return points;
	}

	@Override
	public void renderSelected(Graphics2D g, Projection projection) {
		renderWireframe(g, projection);
	}
}
