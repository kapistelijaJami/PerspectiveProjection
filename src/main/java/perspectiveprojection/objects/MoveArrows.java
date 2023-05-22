package perspectiveprojection.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.primitives.BoundingBox;
import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.primitives.Face;
import perspectiveprojection.util.HelperFunctions;
import perspectiveprojection.primitives.LineSegment;
import perspectiveprojection.enums.MoveDirection;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.Renderable;
import perspectiveprojection.transformations.projections.Projection;

public class MoveArrows extends GameObject {
	public int lineThickness = 3;
	public int hitboxThickness = 15;
	public int length = 60;
	
	private Point3D origin;
	private MoveDirection selectedDirection;
	private MoveDirection hoverDirection;
	
	public MoveArrows(Point3D origin) {
		this.origin = origin;
	}
	
	public void render(Graphics2D g, Projection projection) {
		renderXZ(g, projection, null);
		renderXY(g, projection, null);
		renderYZ(g, projection, null);
		
		renderX(g, projection, Color.BLACK);
		renderY(g, projection, Color.BLACK);
		renderZ(g, projection, Color.BLACK);
		
		renderCenter(g, projection, null);
	}
	
	private void renderX(Graphics2D g, Projection projection, Color background) {
		Optional<LineSegment> result = projection.projectLineSegment(getXLineSegment());
		if (result.isPresent()) {
			LineSegment line = result.get();
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.RED, lineThickness, 0, 5, true);
		}
	}
	
	private void renderY(Graphics2D g, Projection projection, Color background) {
		Optional<LineSegment> result = projection.projectLineSegment(getYLineSegment());
		if (result.isPresent()) {
			LineSegment line = result.get();
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.GREEN, lineThickness, 0, 5, true);
		}
	}
	
	private void renderZ(Graphics2D g, Projection projection, Color background) {
		Optional<LineSegment> result = projection.projectLineSegment(getZLineSegment());
		if (result.isPresent()) {
			LineSegment line = result.get();
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.BLUE, lineThickness, 0, 5, true);
		}
	}
	
	private void renderXZ(Graphics2D g, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getXZFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable XZ = faces.get(0);
			XZ.render(g);
			if (XZ instanceof Face && background != null) {
				((Face) XZ).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderXY(Graphics2D g, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getXYFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable XY = faces.get(0);
			XY.render(g);
			if (XY instanceof Face && background != null) {
				((Face) XY).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderYZ(Graphics2D g, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getYZFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable YZ = faces.get(0);
			YZ.render(g);
			if (YZ instanceof Face && background != null) {
				((Face) YZ).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderCenter(Graphics2D g, Projection projection, Color color) {
		Point3D p = projection.project(origin, true);
		if (p == null) {
			return;
		}
		
		double unit = length / 3.0 / 2.0;
		unit = projection.getProjectedSize(origin, unit);

		if (color != null) {
			g.setColor(color);
			g.setStroke(new BasicStroke(3));
		} else {
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(1));
		}
		g.drawRect((int) (p.x - unit), (int) (p.y - unit), (int) (unit * 2), (int) (unit * 2));
	}
	
	private void renderCurrentHover(Graphics2D g, Projection projection) {
		switch (hoverDirection) {
			case X:
				renderX(g, projection, Color.YELLOW);
				break;
			case Y:
				renderY(g, projection, Color.YELLOW);
				break;
			case Z:
				renderZ(g, projection, Color.YELLOW);
				break;
			case XZ:
				renderXZ(g, projection, Color.YELLOW);
				break;
			case XY:
				renderXY(g, projection, Color.YELLOW);
				break;
			case YZ:
				renderYZ(g, projection, Color.YELLOW);
				break;
			case ALL:
				renderCenter(g, projection, Color.YELLOW);
				break;
		}
	}
	
	public BoundingBox getALLBoundingBox(Point3D origin) {
		double unit = length / 3.0;
		return BoundingBox.createBoundingBoxAroundPoint(origin, unit, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getXBoundingBox(Point3D origin) {
		return BoundingBox.createBoundingBoxAroundLine(getXLineSegment(), hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getYBoundingBox(Point3D origin) {
		return BoundingBox.createBoundingBoxAroundLine(getYLineSegment(), hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getZBoundingBox(Point3D origin) {
		return BoundingBox.createBoundingBoxAroundLine(getZLineSegment(), hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	private LineSegment getXLineSegment() {
		double unit = length / 3.0;
		Point3D offset = origin.add(new Point3D(unit, 0, 0));
		return new LineSegment(offset, offset.add(new Point3D(length, 0, 0)));
	}
	
	private LineSegment getYLineSegment() {
		double unit = length / 3.0;
		Point3D offset = origin.add(new Point3D(0, unit, 0));
		return new LineSegment(offset, offset.add(new Point3D(0, length, 0)));
	}
	
	private LineSegment getZLineSegment() {
		double unit = length / 3.0;
		Point3D offset = origin.add(new Point3D(0, 0, unit));
		return new LineSegment(offset, offset.add(new Point3D(0, 0, length)));
	}
	
	private Face getXZFace() {
		Face XZ = new Face(Color.GREEN);
		
		double offset = length / 6.0;
		double unit = length / 2.6;
		Point3D offsetPoint = new Point3D(offset, -unit, offset);
		
		XZ.addPoint(new Point3D(0, 0, 0).add(offsetPoint));
		XZ.addPoint(new Point3D(0, 0, unit).add(offsetPoint));
		XZ.addPoint(new Point3D(unit, 0, unit).add(offsetPoint));
		XZ.addPoint(new Point3D(unit, 0, 0).add(offsetPoint));
		
		return XZ;
	}
	
	private Face getXYFace() {
		Face XY = new Face(Color.BLUE);
		
		double offset = length / 6.0;
		double unit = length / 2.6;
		Point3D offsetPoint = new Point3D(offset, offset, -unit);
		
		XY.addPoint(new Point3D(0, 0, 0).add(offsetPoint));
		XY.addPoint(new Point3D(unit, 0, 0).add(offsetPoint));
		XY.addPoint(new Point3D(unit, unit, 0).add(offsetPoint));
		XY.addPoint(new Point3D(0, unit, 0).add(offsetPoint));
		
		return XY;
	}
	
	private Face getYZFace() {
		Face YZ = new Face(Color.RED);
		
		double offset = length / 6.0;
		double unit = length / 2.6;
		Point3D offsetPoint = new Point3D(-unit, offset, offset);
		
		YZ.addPoint(new Point3D(0, 0, 0).add(offsetPoint));
		YZ.addPoint(new Point3D(0, unit, 0).add(offsetPoint));
		YZ.addPoint(new Point3D(0, unit, unit).add(offsetPoint));
		YZ.addPoint(new Point3D(0, 0, unit).add(offsetPoint));
		
		return YZ;
	}
	
	private Face getXZFaceWorldSpace(SimpleMatrix translationMatrix) {
		return getXZFace().applyMatrix(translationMatrix);
	}
	
	private Face getXYFaceWorldSpace(SimpleMatrix translationMatrix) {
		return getXYFace().applyMatrix(translationMatrix);
	}
	
	private Face getYZFaceWorldSpace(SimpleMatrix translationMatrix) {
		return getYZFace().applyMatrix(translationMatrix);
	}
	
	private Face getXZFaceWorldSpace(Point3D origin) {
		return getXZFace().applyMatrix(HelperFunctions.getTranslationMatrix(origin));
	}
	
	private Face getXYFaceWorldSpace(Point3D origin) {
		return getXYFace().applyMatrix(HelperFunctions.getTranslationMatrix(origin));
	}
	
	private Face getYZFaceWorldSpace(Point3D origin) {
		return getYZFace().applyMatrix(HelperFunctions.getTranslationMatrix(origin));
	}
	
	private List<Face> getPlaneFaces() {
		return Arrays.asList(getXZFace(), getXYFace(), getYZFace());
	}
	
	private List<Face> getPlaneFacesWorldSpace(Point3D origin) {
		SimpleMatrix translationMatrix = HelperFunctions.getTranslationMatrix(origin);
		
		return Arrays.asList(getXZFaceWorldSpace(translationMatrix), getXYFaceWorldSpace(translationMatrix), getYZFaceWorldSpace(translationMatrix));
	}
	
	public BoundingBox getXZFaceBoundingBox(Point3D origin) {
		SimpleMatrix translationMatrix = HelperFunctions.getTranslationMatrix(origin);
		return BoundingBox.createBoundingBox(getXZFaceWorldSpace(translationMatrix).points);
	}
	
	public BoundingBox getXYFaceBoundingBox(Point3D origin) {
		SimpleMatrix translationMatrix = HelperFunctions.getTranslationMatrix(origin);
		return BoundingBox.createBoundingBox(getXYFaceWorldSpace(translationMatrix).points);
	}
	
	public BoundingBox getYZFaceBoundingBox(Point3D origin) {
		SimpleMatrix translationMatrix = HelperFunctions.getTranslationMatrix(origin);
		return BoundingBox.createBoundingBox(getYZFaceWorldSpace(translationMatrix).points);
	}
	
	@Override
	public void setLocation(Point3D loc) {
		origin = loc;
	}
	
	@Override
	public Point3D getLocation() {
		return new Point3D();
	}

	@Override
	public List<SimpleMatrix> getListOfPoints() {
		return new ArrayList<>();
	}

	@Override
	public void renderSelected(Graphics2D g, Projection projection) {
		
	}
	
	public void setHoverDirection(MoveDirection hoverDirection) {
		this.hoverDirection = hoverDirection;
	}
	
	public void setSelectedDirection(MoveDirection selectedDirection) {
		this.selectedDirection = selectedDirection;
	}

	@Override
	public void renderHover(Graphics2D g, Projection projection) {
		renderCurrentHover(g, projection);
	}
}
