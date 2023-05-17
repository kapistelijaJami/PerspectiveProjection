package perspectiveprojection;

import java.awt.Color;
import java.awt.Graphics2D;

public class Light implements Renderable {
	public Point3D location;
	
	public Light(double x, double y, double z) {
		this(new Point3D(x, y, z));
	}
	
	public Light(Point3D location) {
		this.location = location;
	}
	
	@Override
	public void render(Graphics2D g) {
		int size = 20;
		g.setColor(Color.YELLOW);
		g.fillOval((int) location.x - (size / 2), (int) location.y - (size / 2), size, size);
	}

	@Override
	public double getDepth() {
		return location.z;
	}
}
