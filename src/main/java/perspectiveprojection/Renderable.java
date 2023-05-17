package perspectiveprojection;

import java.awt.Graphics2D;

public interface Renderable extends Comparable<Renderable> {
	public double getDepth();
	
	public void render(Graphics2D g);
	
	@Override
	public default int compareTo(Renderable o) {
		return -Double.compare(this.getDepth(), o.getDepth());
	}
}
