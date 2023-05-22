package perspectiveprojection;

import java.awt.Graphics2D;
import perspectiveprojection.projections.Projection;

public interface Hoverable {
	public void hover();
	public void unhover();
	public void renderHover(Graphics2D g, Projection projection);
}
