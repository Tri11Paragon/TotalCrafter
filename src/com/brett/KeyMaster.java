package com.brett;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
*
* @author brett
* @date Mar. 14, 2020
* Handles key events
*/

public class KeyMaster {
	
	// lists of key and mouse amounts
	private static List<IKeyState> keys = new ArrayList<IKeyState>();
	private static List<IMouseState> mice = new ArrayList<IMouseState>();
	// states of the keyboard and mouse.
	public static boolean state;
	public static boolean mouseState;
	
	/**
	 * keeps the keymaster updated.
	 */
	public static void update() {
		state = Keyboard.next();
		// if key is pressed update all event listeners
		if(state) {
			// handles released and pressed events.
			if(Keyboard.getEventKeyState()) {
				for (int i = 0; i < keys.size(); i++)
					keys.get(i).onKeyPressed();
			} else {
				for (int i = 0; i < keys.size(); i++)
					keys.get(i).onKeyReleased();
			}
		}
		mouseState = Mouse.next();
		// same thing as ^ but for mice.
		if ((mouseState)) {
			// handles released and pressed events.
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
	
	/**
	 * Registers this key listener with the game's master listener
	 */
	public static void registerKeyRequester(IKeyState req) {
		if (req == null)
			return;
		keys.add(req);
	}
	
	/**
	 * Registers this mouse listener with the game's master listener
	 */
	public static void registerMouseRequester(IMouseState req) {
		if (req == null)
			return;
		mice.add(req);
	}
	
}
