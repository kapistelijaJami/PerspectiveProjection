package perspectiveprojection;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.event.MouseInputListener;

public class KeyInput implements MouseInputListener, MouseWheelListener, KeyListener {
	private Game game;
	private Point clickLoc; //where was the click location of the mouse relative to the window
	private Point2D clickRot;
	//private Point3D clickPos; //what position the game was at when the click happened
	
	public KeyInput(Game game) {
		this.game = game;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Camera cam = game.getCamera();
		clickRot = new Point2D(cam.getYaw(), cam.getPitch());
		clickLoc = e.getPoint();
		//clickPos = cam.getLoc().copy();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		//game.mouseReleased(e);
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
		Camera cam = game.getCamera();
		
		if (checkMouseButtonMask(e, MouseEvent.BUTTON1_DOWN_MASK)) {
			double yaw = clickRot.x + diff.x / Math.max(4, 1.5 * 1); // *1 is zoom
			double pitch = clickRot.y - diff.y / Math.max(4, 1.5 * 1);
			
			cam.setYawAndPitch(yaw, pitch);
			
		} else if (checkMouseButtonMask(e, MouseEvent.BUTTON3_DOWN_MASK) || checkMouseButtonMask(e, MouseEvent.BUTTON2_DOWN_MASK)) {
			/*cam.getLoc().x = (clickPos.x + diff.x / 1); // /1 is zoom
			cam.getLoc().y = (clickPos.y + diff.y / 1);*/
		}
		mousePressed(e); //resets the click locations for this event, ready to receive the next drag event
	}
	
	private boolean checkMouseButtonMask(MouseEvent e, int mask) {
		return (e.getModifiersEx() & mask) == mask;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		//game.mouseMoved(e);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//game.zoom *= e.getPreciseWheelRotation() == -1 ? 1.5 : 0.75;
		Camera cam = game.getCamera();
		cam.moveForward(50 * -e.getPreciseWheelRotation());
		//cam.getLoc().z += 50 * e.getPreciseWheelRotation();
		//game.updateProjection();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
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
				game.lookAt(new Point3D(100, 100, 100));
				break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
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
