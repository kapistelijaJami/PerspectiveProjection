package perspectiveprojection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

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
	
	public void render(Graphics2D g, Paint paint) {
		g.setPaint(paint);
		drawLine(g, start, end, 1);
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
		
		
		int pointSize = 8;
		g.setColor(Color.LIGHT_GRAY);
		g.fillOval((int) start.x - pointSize / 2, (int) start.y - pointSize / 2, pointSize, pointSize);
		g.fillOval((int) end.x - pointSize / 2, (int) end.y - pointSize / 2, pointSize, pointSize);
	}
	
	public Point getStartAs2DInt() {
		return start.getAs2DInt();
	}
	
	public Point getEndAs2DInt() {
		return end.getAs2DInt();
	}
}
