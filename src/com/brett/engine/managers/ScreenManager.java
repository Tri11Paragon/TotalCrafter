package com.brett.engine.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.brett.engine.Loader;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.tools.ScreenShot;
import com.brett.engine.tools.Settings;
import com.brett.engine.ui.GUIRenderer;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.font.FontType;
import com.brett.engine.ui.font.fontRendering.FontRenderer;
import com.brett.engine.ui.screen.Screen;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ScreenManager {
	
	public static GUIRenderer uiRenderer;
	public static Loader loader;
	public static FontRenderer fontrenderer;
	public static FontType monospaced;
	public static HashMap<String, FontType> fonts = new HashMap<String, FontType>();
	
	private static List<Screen> screens = new ArrayList<Screen>();
	private static Screen activeScreen;
	
	public static void pre_init() {
		Settings.load();
		DisplayManager.createDisplay(false);
		loader = new Loader();
		uiRenderer = new GUIRenderer(loader);
		fontrenderer = new FontRenderer();
		monospaced = new FontType(loader.loadTexture("fonts/monospaced-72", 0), new File("resources/textures/fonts/monospaced-72.fnt"));
		fonts.put("mono", monospaced);
	}
	
	public static void init() {
		InputMaster.keyboard.add(new ScreenShot());
		
	}
	
	public static void post_init() {
		ProjectionMatrix.updateProjectionMatrix();
	}
	
	public static Screen switchScreen(Screen s) {
		Screen old = activeScreen;
		if (activeScreen != null)
			activeScreen.onLeave();
		activeScreen = s;
		if (activeScreen != null)
			activeScreen.onSwitch();
		return old;
	}
	
	public static void update() {
		if (activeScreen != null) {
			List<UIElement> elements = activeScreen.render();
			if (elements != null)
				uiRenderer.render(elements);
			ScreenManager.fontrenderer.render(activeScreen.renderText());
			activeScreen.update();
		}
	}
	
	public static void close() {
		for (int i = 0; i < screens.size(); i++) {
			screens.get(i).close();
			screens.get(i).onLeave();
		}
		DisplayManager.closeDisplay();
		Settings.save();
	}
	
}
