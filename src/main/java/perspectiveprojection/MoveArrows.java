package perspectiveprojection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.projections.Projection;

public class MoveArrows extends GameObject {
	public int lineThickness = 3;
	public int hitboxThickness = 15;
	public int length = 70;
	
	private MoveDirection selectedDirection;
	private MoveDirection hoverDirection;
	
	
	public void render(Graphics2D g, Point3D origin, Projection projection) {
		renderXZ(g, origin, projection, null);
		renderXY(g, origin, projection, null);
		renderYZ(g, origin, projection, null);
		
		renderX(g, origin, projection, Color.BLACK);
		renderY(g, origin, projection, Color.BLACK);
		renderZ(g, origin, projection, Color.BLACK);
		
		renderCenter(g, origin, projection, null);
		
		
		//render current hover
		if (!hovering || hoverDirection == null) {
			return;
		}
		switch (hoverDirection) {
			case X:
				renderX(g, origin, projection, Color.YELLOW);
				break;
			case Y:
				renderY(g, origin, projection, Color.YELLOW);
				break;
			case Z:
				renderZ(g, origin, projection, Color.YELLOW);
				break;
			case XZ:
				renderXZ(g, origin, projection, Color.YELLOW);
				break;
			case XY:
				renderXY(g, origin, projection, Color.YELLOW);
				break;
			case YZ:
				renderYZ(g, origin, projection, Color.YELLOW);
				break;
			case ALL:
				renderCenter(g, origin, projection, Color.YELLOW);
				break;
		}
	}
	
	private void renderX(Graphics2D g, Point3D origin, Projection projection, Color background) {
		LineSegment line = projection.projectLineSegment(origin, origin.add(new Point3D(length, 0, 0)));
		if (line != null) {
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.RED, lineThickness, 0, 5, true);
		}
	}
	
	private void renderY(Graphics2D g, Point3D origin, Projection projection, Color background) {
		LineSegment line = projection.projectLineSegment(origin, origin.add(new Point3D(0, length, 0)));
		if (line != null) {
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.GREEN, lineThickness, 0, 5, true);
		}
	}
	
	private void renderZ(Graphics2D g, Point3D origin, Projection projection, Color background) {
		LineSegment line = projection.projectLineSegment(origin, origin.add(new Point3D(0, 0, length)));
		if (line != null) {
			line.hasBackground = true;
			line.backgroundColor = background;
			line.render(g, Color.BLUE, lineThickness, 0, 5, true);
		}
	}
	
	private void renderXZ(Graphics2D g, Point3D origin, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getXZFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable XZ = faces.get(0);
			XZ.render(g);
			if (XZ instanceof Face && background != null) {
				((Face) XZ).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderXY(Graphics2D g, Point3D origin, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getXYFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable XY = faces.get(0);
			XY.render(g);
			if (XY instanceof Face && background != null) {
				((Face) XY).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderYZ(Graphics2D g, Point3D origin, Projection projection, Color background) {
		List<Renderable> faces = projection.projectFaces(Arrays.asList(getYZFaceWorldSpace(origin)), null);
		if (!faces.isEmpty()) {
			Renderable YZ = faces.get(0);
			YZ.render(g);
			if (YZ instanceof Face && background != null) {
				((Face) YZ).renderLines(g, background, 3);
			}
		}
	}
	
	private void renderCenter(Graphics2D g, Point3D origin, Projection projection, Color color) {
		Point3D p = projection.project(origin);
		if (p != null) {
			double unit = length / 3.0 / 2.0;
			unit = projection.getProjectedSize(origin, unit, unit);
			
			if (color != null) {
				g.setColor(color);
				g.setStroke(new BasicStroke(3));
			} else {
				g.setColor(Color.WHITE);
				g.setStroke(new BasicStroke(1));
			}
			g.drawRect((int) (p.x - unit), (int) (p.y - unit), (int) (unit * 2), (int) (unit * 2));
		}
	}
	
	public BoundingBox getALLBoundingBox(Point3D origin) {
		double unit = length / 3.0;
		return BoundingBox.createBoundingBoxAroundPoint(origin, unit, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getXBoundingBox(Point3D origin) {
		LineSegment x = new LineSegment(origin, origin.add(new Point3D(length, 0, 0)));
		return BoundingBox.createBoundingBoxAroundLine(x, hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getYBoundingBox(Point3D origin) {
		LineSegment y = new LineSegment(origin, origin.add(new Point3D(0, length, 0)));
		return BoundingBox.createBoundingBoxAroundLine(y, hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
	}
	
	public BoundingBox getZBoundingBox(Point3D origin) {
		LineSegment z = new LineSegment(origin, origin.add(new Point3D(0, 0, length)));
		return BoundingBox.createBoundingBoxAroundLine(z, hitboxThickness, BoundingBoxType.AXIS_ALIGNED_BOX);
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
	public void setLocation(Point3D loc) {}
	
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
	public void renderHover(Graphics2D g, Projection projection) {}
}
