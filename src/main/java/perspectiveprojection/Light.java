package perspectiveprojection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.projections.Projection;

public class Light extends GameObject implements Renderable {
	public Point3D location;
	private double intensity = 100;
	public double size = 50;
	
	public Light(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Light(Point3D location) {
		this.location = location;
	}
	
	public Light(Point3D location, double size) {
		this.location = location;
		this.size = size;
	}
	
	public double getIntensity() {
		return intensity;
	}
	
	@Override
	public Point3D getLocation() {
		return location;
	}
	
	@Override
	public void setLocation(Point3D loc) {
		location = loc;
	}
	
	@Override
	public void render(Graphics2D g) {
		double radius = size / 2;
		g.setColor(Color.YELLOW);
		g.fillOval((int) (location.x - radius), (int) (location.y - radius), (int) size, (int) size);
	}
	
	@Override
	public double getDepth() {
		return location.z;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBoxAroundPoint(location, size, BoundingBoxType.SPHERE);
	}
	
	@Override
	public List<SimpleMatrix> getListOfPoints() {
		return List.of(location.asMatrix());
	}
	
	@Override
	public void renderSelected(Graphics2D g, Projection projection) {
		Point3D projected = projection.project(location);
		
		Double s = projection.getProjectedSize(location, size);
		if (s == null) {
			return;
		}
		double radius = s / 2;
		
		g.setColor(Color.RED);
		g.drawOval((int) (projected.x - radius), (int) (projected.y - radius), (int) (radius * 2), (int) (radius * 2));
	}

	@Override
	public void renderHover(Graphics2D g, Projection projection) {
		Point3D projected = projection.project(location);
		
		Double s = projection.getProjectedSize(location, size);
		if (s == null) {
			return;
		}
		double radius = s / 2;
		
		g.setColor(Color.YELLOW);
		g.drawOval((int) (projected.x - radius), (int) (projected.y - radius), (int) (radius * 2), (int) (radius * 2));
	}
}
