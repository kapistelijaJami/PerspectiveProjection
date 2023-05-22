package perspectiveprojection.interfaces;

import java.awt.Graphics2D;
import perspectiveprojection.transformations.projections.Projection;

public interface Hoverable {
	public void renderHover(Graphics2D g, Projection projection);
}
