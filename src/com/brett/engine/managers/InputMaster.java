package com.brett.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.brett.engine.data.IKeyState;
import com.brett.engine.data.IMouseState;

public class InputMaster {
	
	public static List<IKeyState> keyboard = new ArrayList<IKeyState>();
	public static List<IMouseState> mouse = new ArrayList<IMouseState>();
	public static boolean state;
	public static boolean mouseState;
	
	public static final boolean[] keyDown = new boolean[1023];
	
	public static void keyPressed(int key) {
		keyDown[key] = true;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyPressed(key);
	}
	
	public static void keyReleased(int key) {
		keyDown[key] = false;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyReleased(key);
	}
	
	public static void mousePressed(int button) {
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMousePressed(button);
	}
	
	public static void mouseReleased(int button) {
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMouseReleased(button);
	}
	
}
