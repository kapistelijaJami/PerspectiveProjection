package perspectiveprojection.input;

import java.util.HashMap;
import java.util.Map;

public class ButtonsDown {
	private final Map<Integer, Boolean> buttons = new HashMap<>();
	
	public boolean isOn(int key) {
		return buttons.getOrDefault(key, false);
	}
	
	public void set(int key, boolean b) {
		buttons.put(key, b);
	}
}
