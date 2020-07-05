package com.brett.engine;

import com.brett.engine.cameras.Camera;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jul. 4, 2020
*/

public class DebugInfo {
	
	public static UIMenu menu = new UIMenu();
	
	private static UIText FPS;
	private static UIText POSITION;
	private static UIText MEMORYINFO;
	private static long lastTime = 0;
	private static int frames;
	private static Camera camera;
	
	public static void toggle() {
		menu.toggle();
	}
	
	public static UIMenu init(Camera camera) {
		DebugInfo.camera = camera;
		FPS = new UIText("FPS: 120", 250, "mono", 0, 0, 500, 5);
		POSITION = new UIText("X: " + camera.getPosition().x + "\nY: " + camera.getPosition().y + "\nZ: " + camera.getPosition().z, 250, "mono", 0, 30, 500, 5);
		MEMORYINFO = new UIText(new StringBuilder().append("Free Memory: ").append((Runtime.getRuntime().freeMemory()/1024)/1024).append(" MB").append("\n")
				.append("Total Memory: ").append((Runtime.getRuntime().totalMemory()/1024)/1024).append(" MB").toString(), 250, "mono", 0, 120, 500, 5);
		UIText.updateTextMesh(FPS);
		UIText.updateTextMesh(MEMORYINFO);
		UIText.updateTextMesh(POSITION);
		menu = new UIMenu();
		menu.addText(FPS);
		menu.addText(POSITION);
		menu.addText(MEMORYINFO);
		return menu;
	}
	
	public static void update() {
		frames++;
		if (System.currentTimeMillis() - lastTime > 1000) {
			FPS.changeText(new StringBuilder().append("FPS: ").append(frames).toString());
			POSITION.changeText(new StringBuilder().append("X: ").append(camera.getPosition().x)
					.append("\nY: ").append(camera.getPosition().y).append("\nZ: ").append(camera.getPosition().z).toString());
			MEMORYINFO.changeText(new StringBuilder().append("Free Memory: ").append((Runtime.getRuntime().freeMemory()/1024)/1024).append(" MB").append("\n")
				.append("Total Memory: ").append((Runtime.getRuntime().totalMemory()/1024)/1024).append(" MB").toString());
			frames = 0;
			lastTime = System.currentTimeMillis();
		}
	}
	
	public static UIMenu destroy() {
		return menu;
	}
	
}
