package perspectiveprojection.interfaces;

import java.awt.Graphics2D;
import perspectiveprojection.transformations.projections.Projection;

public interface Selectable {
	public void renderSelected(Graphics2D g, Projection projection);
}
