package perspectiveprojection.objects;

import perspectiveprojection.enums.BoundingBoxType;
import perspectiveprojection.primitives.BoundingBox;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.interfaces.Renderable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import perspectiveprojection.transformations.projections.Projection;

public class Light extends GameObject implements Renderable {
	private double intensity = 100;
	
	public Light(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Light(Point3D location) {
		super(50);
		this.setLocation(location);
	}
	
	public Light(Point3D location, double size) {
		super(size);
		this.setLocation(location);
	}
	
	public double getIntensity() {
		return intensity;
	}
	
	@Override
	public void render(Graphics2D g) {
		double radius = size / 2;
		g.setColor(Color.YELLOW);
		Point3D location = getLocation();
		g.fillOval((int) (location.x - radius), (int) (location.y - radius), (int) size, (int) size);
	}
	
	@Override
	public double getDepth() {
		Point3D location = getLocation();
		return location.z;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return BoundingBox.createBoundingBoxAroundPoint(getLocation(), size, BoundingBoxType.SPHERE);
	}
	
	@Override
	public List<SimpleMatrix> getListOfPoints() {
		return List.of(getLocation().asMatrix());
	}
	
	@Override
	public void renderSelected(Graphics2D g, Projection projection) {
		Point3D projected = projection.project(getLocation(), true);
		if (projected == null) {
			return;
		}
		
		double s = projection.getProjectedSize(getLocation(), size);
		double radius = s / 2;
		
		g.setColor(Color.RED);
		g.drawOval((int) (projected.x - radius), (int) (projected.y - radius), (int) (radius * 2), (int) (radius * 2));
	}
	
	@Override
	public void renderHover(Graphics2D g, Projection projection) {
		Point3D projected = projection.project(getLocation(), true);
		if (projected == null) {
			return;
		}
		
		double s = projection.getProjectedSize(getLocation(), size);
		double radius = s / 2;
		
		g.setColor(Color.YELLOW);
		g.drawOval((int) (projected.x - radius), (int) (projected.y - radius), (int) (radius * 2), (int) (radius * 2));
	}

	@Override
	public BoundingBoxType getBoundingBoxType() {
		return BoundingBoxType.SPHERE;
	}
	
	public double getSize() {
		return size;
	}
}
