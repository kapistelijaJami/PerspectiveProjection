package perspectiveprojection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class LineSegment {
	private Point3D start;
	private Point3D end;
	
	public LineSegment(Point3D end) {
		start = new Point3D();
		this.end = end;
	}
	
	public LineSegment(Point3D start, Point3D end) {
		this.start = start;
		this.end = end;
	}
	
	public Point3D getStart() {
		return start;
	}
	
	public Point3D getEnd() {
		return end;
	}
	
	public void render(Graphics2D g) {
		render(g, Color.red);
	}
	
	public void render(Graphics2D g, double thickness) {
		render(g, Color.red, thickness);
	}
	
	public void render(Graphics2D g, Color color) {
		g.setColor(color);
		drawLine(g, start, end, 1);
	}
	
	public void render(Graphics2D g, Color color, double thickness) {
		g.setColor(color);
		drawLine(g, start, end, thickness);
	}
	
	private void drawLine(Graphics2D g, Point3D start, Point3D end, double thickness) {
		g.setStroke(new BasicStroke((float) thickness));
		g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
		
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval((int) start.x - 3, (int) start.y - 3, 6, 6);
		g.fillOval((int) end.x - 3, (int) end.y - 3, 6, 6);
	}
}
