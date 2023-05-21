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
	
	public void render(Graphics2D g, double startSize, double endSize) {
		render(g, Color.red, startSize, endSize);
	}
	
	public void render(Graphics2D g, Paint paint, double startSize, double endSize) {
		g.setPaint(paint);
		drawLine(g, start, end, 1, startSize, endSize);
	}
	
	public void render(Graphics2D g, double thickness, double startSize, double endSize) {
		render(g, Color.red, thickness, startSize, endSize);
	}
	
	public void render(Graphics2D g, Color color, double startSize, double endSize) {
		g.setColor(color);
		drawLine(g, start, end, 1, startSize, endSize);
	}
	
	public void render(Graphics2D g, Color color, double thickness, double startSize, double endSize) {
		g.setColor(color);
		drawLine(g, start, end, thickness, startSize, endSize);
	}
	
	private void drawLine(Graphics2D g, Point3D start, Point3D end, double thickness, double startRadius, double endRadius) {
		g.setStroke(new BasicStroke((float) thickness));
		g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
		
		g.setColor(Color.GREEN);
		g.fillOval((int) (start.x - startRadius), (int) (start.y - startRadius), (int) (startRadius * 2), (int) (startRadius * 2));
		g.fillOval((int) (end.x - endRadius), (int) (end.y - endRadius), (int) (endRadius * 2), (int) (endRadius * 2));
	}
	
	public Point getStartAs2DInt() {
		return start.getAs2DInt();
	}
	
	public Point getEndAs2DInt() {
		return end.getAs2DInt();
	}
}
