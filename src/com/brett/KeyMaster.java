package com.brett;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

/**
*
* @author brett
* @date Mar. 14, 2020
*/

public class KeyMaster {
	
	private static List<IKeyState> keys = new ArrayList<IKeyState>();
	public static boolean state;
	
	public static void init() {
		
	}
	
	public static void update() {
		state = Keyboard.next();
		if(state) {
			if(Keyboard.getEventKeyState()) {
				for (IKeyState k : keys)
					k.onKeyPressed();
			} else {
				for (IKeyState k : keys)
					k.onKeyReleased();
			}
		}
	}
	
	public static void registerKeyRequester(IKeyState req) {
		keys.add(req);
	}
	
}
