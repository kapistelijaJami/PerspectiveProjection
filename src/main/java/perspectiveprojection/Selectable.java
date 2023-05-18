package perspectiveprojection;

import java.awt.Graphics2D;
import perspectiveprojection.projections.Projection;

public interface Selectable {
	public void renderSelected(Graphics2D g, Projection projection);
}
