package perspectiveprojection.input;

import perspectiveprojection.camera.Camera;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import perspectiveprojection.Game;
import perspectiveprojection.util.HelperFunctions;
import perspectiveprojection.enums.MoveDirection;
import perspectiveprojection.linear_algebra.Point2D;
import perspectiveprojection.linear_algebra.Point3D;

public class KeyInput implements MouseInputListener, MouseWheelListener, KeyListener, ComponentListener {
	private Game game;
	private Point clickLoc; //where was the click location of the mouse relative to the screen
	
	private boolean dragging = false;
	private boolean movingObject = false;
	private MoveDirection movingDirection;
	private Point3D currentMoveLocation;
	private Robot robot;
	
	public ButtonsDown buttons = new ButtonsDown();
	
	public KeyInput(Game game) {
		this.game = game;
		
		try {
			robot = new Robot();
		} catch (AWTException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		//clickLoc = e.getPoint();
		clickLoc = e.getLocationOnScreen();
		
		if (SwingUtilities.isLeftMouseButton(e) && game.clickMoveSelected(e.getX(), e.getY(), this)) {
			currentMoveLocation = game.projectToMoveDirection(e.getX(), e.getY(), movingDirection, null);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (!dragging) {
			boolean renderRay = e.getButton() == MouseEvent.BUTTON3;
			game.click(e.getX(), e.getY(), renderRay);
		}
		dragging = false;
		movingObject = false;
		game.getCanvas().setCursor(Cursor.getDefaultCursor());
		
		mouseMoved(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		//Point p = MouseInfo.getPointerInfo().getLocation(); //if we were to reset mouse location somewhere other than where it was clicked, this is needed, since e could have old information.
		
		if (movingObject) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point3D newMoveLocation = game.projectToMoveDirection(e.getX(), e.getY(), movingDirection, currentMoveLocation);
				Point3D diff = new Point3D(newMoveLocation.x - currentMoveLocation.x, newMoveLocation.y - currentMoveLocation.y, newMoveLocation.z - currentMoveLocation.z);

				handleMoveObject(diff);

				currentMoveLocation = newMoveLocation;
			}
		} else {
			Point p = e.getLocationOnScreen();
			Point2D diff = new Point2D(p.x - clickLoc.x, p.y - clickLoc.y);
			
			if (!dragging && diff.magnitude() < 5) { //How long does the distance have to be to be considered a drag instead of a click
				return;
			}
			if (!dragging && !SwingUtilities.isMiddleMouseButton(e)) {
				HelperFunctions.setCursorBlank(game.getCanvas());
			}
			handleMoveCamera(e, diff);
		}
		
		dragging = true;
	}
	
	private void handleMoveCamera(MouseEvent e, Point2D diff) {
		if (SwingUtilities.isRightMouseButton(e)) {
			Camera cam = game.getCamera();
			cam.moveForward(-diff.y);
		} else if (SwingUtilities.isLeftMouseButton(e) && e.isAltDown()) {
			Point2D prevRot = new Point2D(game.getCurrentYaw(), game.getCurrentPitch());
			double speed = 0.5 / 4.0;
			double yaw = prevRot.x + diff.x * speed;
			double pitch = prevRot.y - diff.y * speed;
			
			game.newYawAndPitch(yaw, pitch);
		} else if (SwingUtilities.isLeftMouseButton(e) && !e.isAltDown()) {
			Point2D prevRot = new Point2D(game.getCurrentYaw(), game.getCurrentPitch());
			double speed = 0.5;
			double yaw = prevRot.x + diff.x * speed;
			double pitch = prevRot.y - diff.y * speed;
			
			game.orbit(yaw, pitch);
		} else if (SwingUtilities.isMiddleMouseButton(e)) {
			Camera cam = game.getCamera();
			cam.moveLeft(diff.x);
			cam.moveUp(diff.y);
			
			mousePressed(e); //resets the click locations for this event, ready to receive the next drag event (not needed now that the mouse is still and clickLoc is not being updated)
			return; //so that it doesn't go to robot.mouseMove()
		}
		
		robot.mouseMove(clickLoc.x, clickLoc.y);
	}
	
	private void handleMoveObject(Point3D diff) {
		double speed = 1;
		game.moveSelected(movingDirection, diff.mult(speed));
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
		//If onMask has 1's where modifier doesn't, result won't equal onMask and it's false.
		//If offMask has 1's where onMask doesn't, but the modifier does, then result won't equal onMask, and it's false.
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if (!dragging) {
			game.hover(e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Camera cam = game.getCamera();
		double speed = 100;
		cam.moveForward(speed * -e.getPreciseWheelRotation());
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
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_W:
			case KeyEvent.VK_A:
			case KeyEvent.VK_S:
			case KeyEvent.VK_D:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_CONTROL:
				buttons.set(key, true);
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
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_W:
			case KeyEvent.VK_A:
			case KeyEvent.VK_S:
			case KeyEvent.VK_D:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_CONTROL:
				buttons.set(key, false);
				break;
		}
	}
	
	public boolean isButtonDown(int button) {
		return buttons.isOn(button);
	}
	
	public void startToMoveObject(MoveDirection movingDirection) {
		movingObject = true;
		this.movingDirection = movingDirection;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		game.windowResized(e.getComponent().getSize());
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {}
	
	@Override
	public void componentShown(ComponentEvent e) {}
	
	@Override
	public void componentHidden(ComponentEvent e) {}
}
