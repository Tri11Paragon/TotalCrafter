package com.brett.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.brett.engine.data.IKeyState;
import com.brett.engine.data.IMouseState;
import com.brett.engine.data.IScrollState;

public class InputMaster {
	
	public static List<IKeyState> keyboard = new ArrayList<IKeyState>();
	public static List<IMouseState> mouse = new ArrayList<IMouseState>();
	public static List<IScrollState> scroll = new ArrayList<IScrollState>();
	public static boolean state;
	public static boolean mouseState;
	
	public static final boolean[] keyDown = new boolean[1023];
	public static final boolean[] mouseDown = new boolean[32];
	public static int lastScrollState = 0;
	public static volatile boolean scrolledLastFrame = false;
	
	public static void keyPressed(int key) {
		if (key < 0)
			return;
		keyDown[key] = true;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyPressed(key);
	}
	
	public static void keyReleased(int key) {
		if (key < 0)
			return;
		keyDown[key] = false;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyReleased(key);
	}
	
	public static void mousePressed(int button) {
		mouseDown[button] = true;
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMousePressed(button);
	}
	
	public static void mouseReleased(int button) {
		mouseDown[button] = false;
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMouseReleased(button);
	}
	
	public static void scrollMoved(int dir) {
		lastScrollState = dir;
		for (int i = 0; i < scroll.size(); i++)
			scroll.get(i).scroll(dir);
		scrolledLastFrame = true;
	}
	
	public static void update() {
		scrolledLastFrame = false;
	}
	
}
