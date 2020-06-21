package com.brett.engine.managers;

import java.util.ArrayList;
import java.util.List;

import com.brett.engine.Loader;
import com.brett.engine.ui.GUIRenderer;
import com.brett.engine.ui.Screen;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ScreenManager {
	
	public static GUIRenderer uiRenderer;
	public static Loader loader;
	
	private static List<Screen> screens = new ArrayList<Screen>();
	private static Screen activeScreen;
	
	public static void pre_init() {
		DisplayManager.createDisplay(false);
		loader = new Loader();
		uiRenderer = new GUIRenderer(loader);
	}
	
	public static void init() {
		
	}
	
	public static void update() {
		if (activeScreen != null)
			activeScreen.update();
	}
	
	public static void close() {
		for (int i = 0; i < screens.size(); i++) {
			screens.get(i).close();
			screens.get(i).onLeave();
		}
		DisplayManager.closeDisplay();
	}
	
}
