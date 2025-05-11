package perspectiveprojection.objects;

import perspectiveprojection.transformations.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.interfaces.Rotateable;
import perspectiveprojection.interfaces.Scaleable;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.linear_algebra.Point3D;

public class Cube extends GameObject implements Rotateable, Scaleable { //TODO: maybe extend Any3DObject?
	private final List<Face> faces = new ArrayList<>();
	public boolean renderDots = false;
	public boolean renderBoundingBox = false;
	
	public Cube(double cubeSize, boolean multipleColors) { //If cubeSize is 100, then the cube is 100x100x100, it will be -50 to 50 around origo if no other transformations are added.
		super(cubeSize / 2);
		
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
		
		if (renderBoundingBox) {
			getBoundingBox().render(g, projection, Color.BLACK);
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

	@Override
	public BoundingBoxType getBoundingBoxType() {
		return BoundingBoxType.AXIS_ALIGNED_BOX;
	}
}
