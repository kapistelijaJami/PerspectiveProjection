package perspectiveprojection;

import java.awt.Canvas;
import perspectiveprojection.projections.Projection;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import perspectiveprojection.projections.OldPerspectiveProjection;
import perspectiveprojection.projections.OrtographicProjection;
import perspectiveprojection.projections.PerspectiveProjection;
import uilibrary.GameLoop;
import uilibrary.Window;

public class Game extends GameLoop {
	private Window window;
	
	//perspective good location when translate first, then rotate: loc (586.0, 691.0, 1202.0) yaw -26.0 pitch -153.0
	private Camera cam = new Camera(new Point3D(586, 691, 1202), -26, 28); //(new Point3D(-400, 300, 500), -25, 28) works with ortographic (origo might be behind camera). Should be able to work with others with these settings as well
	private Cube cube = new Cube(100);
	private Cube cameraObject = new Cube(20);
	private Projection projection = new PerspectiveProjection(cam);
	
	public boolean up = false;
	public boolean down = false;
	public boolean left = false;
	public boolean right = false;
	
	public boolean w = false;
	public boolean a = false;
	public boolean s = false;
	public boolean d = false;
	public boolean shift = false;
	public boolean ctrl = false;
	
	public Game() {
		super(60);
		window = new Window(1280, 720, "Perspective projection");
	}
	
	@Override
	protected void init() {
		KeyInput input = new KeyInput(this);
		
		Canvas canvas = window.getCanvas();
		canvas.addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		canvas.addMouseWheelListener(input);
	}
	
	@Override
	protected void update() {
		double speed = 0.5;
		if (left) {
			cam.setYaw(cam.getYaw() - speed);
		}
		if (right) {
			cam.setYaw(cam.getYaw() + speed);
		}
		if (up) {
			cam.setPitch(cam.getPitch() + speed);
		}
		if (down) {
			cam.setPitch(cam.getPitch() - speed);
		}
		
		if (w) {
			cam.getLoc().z -= speed*4; //TODO: change that cam moves relative to camera orientation
		}
		if (s) {
			cam.getLoc().z += speed*4;
		}
		if (a) {
			cam.getLoc().x -= speed*4;
		}
		if (d) {
			cam.getLoc().x += speed*4;
		}
		
		if (shift) {
			cam.getLoc().y += speed*4;
		}
		if (ctrl) {
			cam.getLoc().y -= speed*4;
		}
		
		if (left || right || up || down || w || s || a || d || shift || ctrl) {
			updateProjection();
		}
	}
	
	@Override
	protected void render() {
		Graphics2D g = window.getGraphics2D();
		g.setColor(Color.red);
		
		Point3D cubeOffset = new Point3D(100, 100, 100);
		
		cube.render(g, cubeOffset, projection);
		
		Point3D cameraObjectOffset = new Point3D(500, 0, 0);
		cameraObject.render(g, cameraObjectOffset, projection);
		
		renderAxis(g, new Point3D());
		
		window.display(g);
	}
	
	//Right hand rule, X is red (thumb, to right), Y is green (index, to up), Z is blue (middle, towards cam)
	private void renderAxis(Graphics2D g, Point3D offset) {
		Point3D start = new Point3D(0, 0, 0).add(offset);
		Point s = projection.projectPointsInt(start)[0];
		
		Point3D xAxis = new Point3D(1000, 0, 0).add(offset);
		Point x = projection.projectPointsInt(xAxis)[0];
		g.setColor(Color.red);
		g.drawLine(s.x, s.y, x.x, x.y);
		
		Point3D yAxis = new Point3D(0, 1000, 0).add(offset);
		Point y = projection.projectPointsInt(yAxis)[0];
		g.setColor(Color.green);
		g.drawLine(s.x, s.y, y.x, y.y);
		
		Point3D zAxis = new Point3D(0, 0, 1000).add(offset);
		Point z = projection.projectPointsInt(zAxis)[0];
		g.setColor(Color.blue);
		g.drawLine(s.x, s.y, z.x, z.y);
	}
	
	public Camera getCamera()  {
		return cam;
	}
	
	public void updateProjection() {
		projection.updateViewMatrix();
	}
}
