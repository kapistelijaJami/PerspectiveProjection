package perspectiveprojection.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.OBJFileReader;
import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.interfaces.Rotateable;
import perspectiveprojection.interfaces.Scaleable;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.transformations.projections.Projection;

public class Any3DObject extends GameObject implements Rotateable, Scaleable {
	private List<Face> faces = new ArrayList<>();
	public boolean renderDots = false;
	public boolean renderBoundingBox = true;
	
	public Any3DObject(List<Face> faces, double size) { //Default size = 1
		super(size);
		this.faces = faces;
	}
	
	public static Any3DObject createFromFile(String path, double size) {
		return createFromFile(new File(path), size);
	}
	
	
	public static Any3DObject createFromFile(File file, double size) {
		return OBJFileReader.readOBJ(file, size);
	}
	
	@Override
	public List<SimpleMatrix> getListOfPoints() {
		List<SimpleMatrix> points = new ArrayList<>();
		for (Face face : getWorldSpaceFaces(null)) {
			points.addAll(face.getListOfPoints());
		}
		return points;
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
