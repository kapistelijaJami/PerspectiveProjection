package perspectiveprojection.objects;

import perspectiveprojection.transformations.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.Game;
import static perspectiveprojection.Game.ambientLight;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.util.HelperFunctions;

public class Cube extends GameObject {
	private SimpleMatrix modelMatrix; //Converts the object from model space to world space. Contains the information for object location, scale and rotation.
	
	private final List<Face> faces = new ArrayList<>();
	public boolean renderDots = false;
	
	public Cube(double cubeSize, boolean multipleColors) { //If cube size is 100, then the cube is 100x100x100, it will be -50 to 50 around origo if no other transformations are added.
		modelMatrix = SimpleMatrix.diag(cubeSize / 2, cubeSize / 2, cubeSize / 2, 1);
		
		faces.add(new Face(new Point3D(-1,  1,  1), new Point3D(-1, -1,  1), new Point3D( 1, -1,  1), new Point3D( 1,  1,  1))); //front
		faces.add(new Face(new Point3D( 1,  1,  1), new Point3D( 1, -1,  1), new Point3D( 1, -1, -1), new Point3D( 1,  1, -1))); //right
		faces.add(new Face(new Point3D(-1,  1, -1), new Point3D(-1, -1, -1), new Point3D(-1, -1,  1), new Point3D(-1,  1,  1))); //left
		faces.add(new Face(new Point3D( 1,  1, -1), new Point3D( 1, -1, -1), new Point3D(-1, -1, -1), new Point3D(-1,  1, -1))); //back
		faces.add(new Face(new Point3D(-1, -1, -1), new Point3D( 1, -1, -1), new Point3D( 1, -1,  1), new Point3D(-1, -1,  1))); //bottom
		faces.add(new Face(new Point3D(-1,  1, -1), new Point3D(-1,  1,  1), new Point3D( 1,  1,  1), new Point3D( 1,  1, -1))); //top
		
		if (multipleColors) {
			faces.get(0).color = Color.RED;
			faces.get(1).color = Color.BLUE;
			faces.get(2).color = Color.ORANGE;
			faces.get(3).color = Color.PINK;
			faces.get(4).color = Color.CYAN;
			faces.get(5).color = Color.GREEN;
		}
	}
	
	@Override
	public Point3D getLocation() {
		return Point3D.fromMatrix(modelMatrix.extractVector(false, 3));
	}
	
	@Override
	public void setLocation(Point3D loc) {
		modelMatrix.setColumn(3, 0, loc.x, loc.y, loc.z);
	}
	
	public void rotate(SimpleMatrix rotate) {
		modelMatrix = modelMatrix.mult(rotate); //this way so that model matrix is applied last, because it includes the translate information. If other way, it would rotate around origo, and the location information is lost from the last column.
	}
	
	public void scale(double scalar) {
		modelMatrix = modelMatrix.mult(SimpleMatrix.diag(scalar, scalar, scalar, 1));
	}
	
	public void renderWireframe(Graphics2D g, Projection projection) {
		renderWireframe(g, projection, Color.RED);
	}
	
	public void renderWireframe(Graphics2D g, Projection projection, Color color) {
		for (Face face : getWorldSpaceFaces(null)) {
			LineSegment[] lines = face.getLines();
			for (LineSegment line : lines) {
				Point3D start = line.getStart();
				Point3D end = line.getEnd();
				
				Optional<LineSegment> result = projection.projectLineSegment(line);
				if (result.isEmpty()) {
					continue;
				}
				line = result.get();
				
				int pointSize = 10;
				double startSize = projection.getProjectedSize(start, pointSize);
				double endSize = projection.getProjectedSize(end, pointSize);
				
				double sRadius = Math.max(startSize / 2, 5);
				double eRadius = Math.max(endSize / 2, 5);
				
				line.renderDots = renderDots;
				line.render(g, color, sRadius, eRadius);
			}
		}
	}
	
	public List<Face> getLocalFaces() {
		return faces;
	}
	
	/**
	 * Gets the faces in world space and calculates the color multipliers based on lights.
	 * @param lights
	 * @return 
	 */
	public List<Face> getWorldSpaceFaces(Light[] lights) {
		List<Face> transformed = new ArrayList<>();
		
		for (Face face : faces) {
			face = face.applyMatrix(modelMatrix);
			face.calculateColorMultiplier(lights);
			
			transformed.add(face);
		}
		
		return transformed;
	}

	@Override
	public List<SimpleMatrix> getListOfPoints() {
		List<SimpleMatrix> points = new ArrayList<>();
		for (Face face : getWorldSpaceFaces(null)) {
			points.addAll(face.getListOfPoints());
		}
		return points;
	}

	@Override
	public void renderSelected(Graphics2D g, Projection projection) {
		renderWireframe(g, projection);
	}

	@Override
	public void renderHover(Graphics2D g, Projection projection) {
		renderWireframe(g, projection, Color.yellow);
	}
}
