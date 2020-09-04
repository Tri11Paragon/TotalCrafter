package com.brett.engine;

import java.text.DecimalFormat;

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
	private static UIText ROTINFO;
	private static long lastTime = 0;
	private static long lastTimeUpdate = 0;
	private static long frames = 0;
	private static Camera camera;
	public static DecimalFormat formatterPOS = new DecimalFormat("0.000000000");
	public static DecimalFormat formatterROT = new DecimalFormat("0.000");
	
	public static void toggle() {
		menu.toggle();
	}
	
	public static UIMenu init(Camera camera) {
		DebugInfo.camera = camera;
		FPS = new UIText("FPS: 120", 250, "mono", 0, 0, 500, 5);
		POSITION = new UIText("X: " + camera.getPosition().x + "\nY: " + camera.getPosition().y + "\nZ: " + camera.getPosition().z, 250, "mono", 0, 30, 500, 5);
		MEMORYINFO = new UIText(new StringBuilder().append("Free Memory: ").append((Runtime.getRuntime().freeMemory()/1024)/1024).append(" MB").append("\n")
				.append("Total Memory: ").append((Runtime.getRuntime().totalMemory()/1024)/1024).append(" MB").toString(), 250, "mono", 0, 120, 500, 5);
		ROTINFO = new UIText("Yaw: 0", 250, "mono", 0, 180, 500, 5);
		UIText.updateTextMesh(FPS);
		UIText.updateTextMesh(MEMORYINFO);
		UIText.updateTextMesh(POSITION);
		UIText.updateTextMesh(ROTINFO);
		menu = new UIMenu();
		menu.addText(FPS);
		menu.addText(POSITION);
		menu.addText(MEMORYINFO);
		menu.addText(ROTINFO);
		return menu;
	}
	
	public static void update() {
		if (menu.isEnabled()) {
			frames++;
			if (System.currentTimeMillis() - lastTime > 1000) {
				FPS.changeText(new StringBuilder().append("FPS: ").append(frames).toString());
				MEMORYINFO.changeText(new StringBuilder().append("Free Memory: ").append((Runtime.getRuntime().freeMemory()/1024)/1024).append(" MB").append("\n")
					.append("Total Memory: ").append((Runtime.getRuntime().totalMemory()/1024)/1024).append(" MB").toString());
				lastTime = System.currentTimeMillis();
				frames = 0;
			}
			if (System.currentTimeMillis() - lastTimeUpdate > 50) {
				POSITION.changeText(new StringBuilder().append("X: ").append(formatterPOS.format(camera.getPosition().x)).append("\nY: ")
						.append(formatterPOS.format(camera.getPosition().y)).append("\nZ: ").append(formatterPOS.format(camera.getPosition().z)).toString());
				ROTINFO.changeText(new StringBuilder().append("Yaw: ")
						.append(formatterROT.format(camera.getYaw())).append(" Pitch: ").append(formatterROT.format(camera.getPitch())).toString());
				lastTimeUpdate = System.currentTimeMillis();
			}
		}
	}
	
	public static UIMenu destroy() {
		return menu;
	}
	
}
