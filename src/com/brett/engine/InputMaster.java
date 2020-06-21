package com.brett.engine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.brett.engine.data.IKeyState;
import com.brett.engine.data.IMouseState;

public class InputMaster {
	
	public static List<IKeyState> keyboard = new ArrayList<IKeyState>();
	public static List<IMouseState> mouse = new ArrayList<IMouseState>();
	public static boolean state;
	public static boolean mouseState;

	public static void update() {
		state = Keyboard.next();
		if (state) {
			if (Keyboard.getEventKeyState()) {
				for (int i = 0; i < keyboard.size(); i++)
					keyboard.get(i).onKeyPressed();
			} else {
				for (int i = 0; i < keyboard.size(); i++)
					keyboard.get(i).onKeyReleased();
			}
		}
		mouseState = Mouse.next();
		if ((mouseState)) {
			if (Mouse.getEventButtonState()) {
				for (int i = 0; i < mouse.size(); i++) {
					mouse.get(i).onMousePressed();
				}
			} else {
				for (int i = 0; i < mouse.size(); i++)
					mouse.get(i).onMouseReleased();
			}
		}
	}
	
}
