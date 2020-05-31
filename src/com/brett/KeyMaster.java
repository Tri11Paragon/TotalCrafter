package com.brett;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
*
* @author brett
* @date Mar. 14, 2020
*/

public class KeyMaster {
	
	private static List<IKeyState> keys = new ArrayList<IKeyState>();
	private static List<IMouseState> mice = new ArrayList<IMouseState>();
	public static boolean state;
	public static boolean mouseState;
	
	public static void init() {
		
	}
	
	public static void update() {
		state = Keyboard.next();
		if(state) {
			if(Keyboard.getEventKeyState()) {
				for (int i = 0; i < keys.size(); i++)
					keys.get(i).onKeyPressed();
			} else {
				for (int i = 0; i < keys.size(); i++)
					keys.get(i).onKeyReleased();
			}
		}
		mouseState = Mouse.next();
		if ((mouseState)) {
			if (Mouse.getEventButtonState()) {
				for (int i = 0; i < mice.size(); i++) {
					mice.get(i).onMousePressed();
				}
			} else {
				for (int i = 0; i < mice.size(); i++)
					mice.get(i).onMouseReleased();
			}
		}
	}
	
	public static void registerKeyRequester(IKeyState req) {
		if (req == null)
			return;
		keys.add(req);
	}
	
	public static void registerMouseRequester(IMouseState req) {
		if (req == null)
			return;
		mice.add(req);
	}
	
}
