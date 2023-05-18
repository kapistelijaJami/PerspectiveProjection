package perspectiveprojection;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class KeyInput implements MouseInputListener, MouseWheelListener, KeyListener {
	private Game game;
	private Point clickLoc; //where was the click location of the mouse relative to the window
	
	private boolean dragging = false;
	
	public KeyInput(Game game) {
		this.game = game;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		clickLoc = e.getPoint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (!dragging) {
			boolean renderRay = e.getButton() == MouseEvent.BUTTON3;
			game.click(e.getX(), e.getY(), renderRay);
		}
		dragging = false;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		Point diff = new Point(p.x - clickLoc.x, p.y - clickLoc.y);
		if (!dragging && diff.distance(0, 0) < 5) { //How long does the distance have to be to be considered a drag instead of a click
			return;
		}
		dragging = true;
		
		if (SwingUtilities.isRightMouseButton(e) && !e.isAltDown()) {
			Point2D prevRot = new Point2D(game.getCurrentYaw(), game.getCurrentPitch());
			double speed = 0.75 / 4.0;
			double yaw = prevRot.x + diff.x * speed;
			double pitch = prevRot.y - diff.y * speed;
			
			game.newYawAndPitch(yaw, pitch);
			
		} else if (SwingUtilities.isLeftMouseButton(e)) {
			Point2D prevRot = new Point2D(game.getCurrentYaw(), game.getCurrentPitch());
			double speed = 0.5;
			double yaw = prevRot.x + diff.x * speed;
			double pitch = prevRot.y - diff.y * speed;
			
			game.orbit(yaw, pitch);
		}
		mousePressed(e); //resets the click locations for this event, ready to receive the next drag event
	}
	
	private boolean checkMouseButtonMask(MouseEvent e, int onMask) {
		return checkMouseButtonMask(e, onMask, 0);
	}
	
	/**
	 * Can be used to check if the buttons the onMask refers to are currently pressed,
	 * and the buttons the offMask refers to is off.
	 * If offMask is 0 it doesn't do anything, only onMask is checked.
	 * You can combine masks to the single int with | operator.
	 * @param e
	 * @param mask
	 * @return 
	 */
	private boolean checkMouseButtonMask(MouseEvent e, int onMask, int offMask) {
		return (e.getModifiersEx() & (onMask | offMask)) == onMask; //When modifiers are masked with & to both onMask and offMask the result has to be same as onMask.
		//If onMask has 1s where modifier doesn't, result won't equal onMask and it's false.
		//If offMask has 1s where onMask doesn't, but the modifier does, then result won't equal onMask, and it's false.
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		//game.mouseMoved(e);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Camera cam = game.getCamera();
		cam.moveForward(50 * -e.getPreciseWheelRotation());
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ALT) {
			e.consume(); //makes alt not do anything it would do normally (like unfocus the window, and change the cursor).
		}
		
		switch (key) {
			case KeyEvent.VK_ESCAPE:
				game.stop();
				break;
			case KeyEvent.VK_LEFT:
				game.left = true;
				break;
			case KeyEvent.VK_RIGHT:
				game.right = true;
				break;
			case KeyEvent.VK_UP:
				game.up = true;
				break;
			case KeyEvent.VK_DOWN:
				game.down = true;
				break;
			case KeyEvent.VK_W:
				game.w = true;
				break;
			case KeyEvent.VK_A:
				game.a = true;
				break;
			case KeyEvent.VK_S:
				game.s = true;
				break;
			case KeyEvent.VK_D:
				game.d = true;
				break;
			case KeyEvent.VK_SHIFT:
				game.shift = true;
				break;
			case KeyEvent.VK_SPACE:
				game.space = true;
				break;
			case KeyEvent.VK_CONTROL:
				game.ctrl = true;
				break;
			case KeyEvent.VK_L:
				game.lookAt(new Point3D(0, 0, 0));
				break;
			case KeyEvent.VK_F:
				game.focusSelected();
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ALT) {
			e.consume(); //makes alt not do anything it would do normally (like unfocus the window, and change the cursor).
		}
		
		switch (key) {
			case KeyEvent.VK_LEFT:
				game.left = false;
				break;
			case KeyEvent.VK_RIGHT:
				game.right = false;
				break;
			case KeyEvent.VK_UP:
				game.up = false;
				break;
			case KeyEvent.VK_DOWN:
				game.down = false;
				break;
			case KeyEvent.VK_W:
				game.w = false;
				break;
			case KeyEvent.VK_A:
				game.a = false;
				break;
			case KeyEvent.VK_S:
				game.s = false;
				break;
			case KeyEvent.VK_D:
				game.d = false;
				break;
			case KeyEvent.VK_SHIFT:
				game.shift = false;
				break;
			case KeyEvent.VK_SPACE:
				game.space = false;
				break;
			case KeyEvent.VK_CONTROL:
				game.ctrl = false;
				break;
		}
	}
}
