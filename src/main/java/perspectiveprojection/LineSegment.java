package perspectiveprojection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

public class LineSegment {
	private final Point3D start;
	private final Point3D end;
	public boolean hasBackground = false;
	public boolean renderDots = true;
	public Color dotColor = Color.GREEN;
	public Color backgroundColor = Color.BLACK;
	
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
		drawLine(g, start, end, null, 1, startSize, endSize, false);
	}
	
	public void render(Graphics2D g, double thickness, double startSize, double endSize) {
		render(g, Color.red, thickness, startSize, endSize);
	}
	
	public void render(Graphics2D g, Color color, double startSize, double endSize) {
		g.setColor(color);
		drawLine(g, start, end, color, 1, startSize, endSize, false);
	}
	
	public void render(Graphics2D g, Color color, double thickness, double startSize, double endSize) {
		drawLine(g, start, end, color, thickness, startSize, endSize, false);
	}
	
	public void render(Graphics2D g, Color color, double thickness, double startSize, double endSize, boolean shiftDots) {
		drawLine(g, start, end, color, thickness, startSize, endSize, shiftDots);
	}
	
	private void drawLine(Graphics2D g, Point3D start, Point3D end, Color color, double thickness, double startRadius, double endRadius, boolean shiftDots) {
		Point3D dir = end.subtract(start).normalize();
		
		Point3D startDot = start.subtract(startRadius);
		Point3D endDot = end.subtract(endRadius);
		
		if (shiftDots) {
			startDot = startDot.add(dir.mult(startRadius / 2));
			endDot = endDot.add(dir.negated().mult(endRadius / 2));
		}
		
		if (hasBackground && color != null) {
			g.setColor(backgroundColor);
			g.setStroke(new BasicStroke((float) thickness + 4));
			g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
			
			if (renderDots) {
				g.fillOval((int) (startDot.x - 2), (int) (startDot.y - 2), (int) (startRadius * 2 + 4), (int) (startRadius * 2 + 4));
				g.fillOval((int) (endDot.x - 2), (int) (endDot.y - 2), (int) (endRadius * 2 + 4), (int) (endRadius * 2 + 4));
			}
		}
		
		if (color != null) {
			g.setColor(color);
		}
		g.setStroke(new BasicStroke((float) thickness));
		g.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
		
		if (renderDots) {
			g.setColor(dotColor);
			g.fillOval((int) (startDot.x), (int) (startDot.y), (int) (startRadius * 2), (int) (startRadius * 2));
			g.fillOval((int) (endDot.x), (int) (endDot.y), (int) (endRadius * 2), (int) (endRadius * 2));
		}
	}
	
	public Point getStartAs2DInt() {
		return start.getAs2DInt();
	}
	
	public Point getEndAs2DInt() {
		return end.getAs2DInt();
	}
	
	public double getLength() {
		return end.subtract(start).magnitude();
	}
	
	public Point3D getMiddle() {
		return start.add(end).divide(2);
	}

	public Point3D getDir() {
		return end.subtract(start).normalize();
	}
}
