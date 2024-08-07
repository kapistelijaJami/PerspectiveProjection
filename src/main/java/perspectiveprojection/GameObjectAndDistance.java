package perspectiveprojection;

import perspectiveprojection.objects.GameObject;

public class GameObjectAndDistance implements Comparable<GameObjectAndDistance> {
	public GameObject gameObject;
	public double t;
	
	public GameObjectAndDistance(GameObject gameObject, double t) {
		this.gameObject = gameObject;
		this.t = t;
	}

	@Override
	public int compareTo(GameObjectAndDistance o) {
		return Double.compare(t, o.t);
	}
}
