package com.brett.engine;

import java.text.DecimalFormat;

import org.joml.Vector3d;

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
	private static UIText REGINFO;
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
		REGINFO = new UIText("Region: 0 0 0", 250, "mono", 0, 210, 500, 5);
		UIText.updateTextMesh(FPS);
		UIText.updateTextMesh(MEMORYINFO);
		UIText.updateTextMesh(POSITION);
		UIText.updateTextMesh(ROTINFO);
		UIText.updateTextMesh(REGINFO);
		menu = new UIMenu();
		menu.addText(FPS);
		menu.addText(POSITION);
		menu.addText(MEMORYINFO);
		menu.addText(ROTINFO);
		menu.addText(REGINFO);
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
				Vector3d pos = camera.getPosition();
				POSITION.changeText(new StringBuilder().append("X: ").append(formatterPOS.format(pos.x)).append("\nY: ")
						.append(formatterPOS.format(pos.y)).append("\nZ: ").append(pos.z).toString());
				ROTINFO.changeText(new StringBuilder().append("Yaw: ")
						.append(formatterROT.format(camera.getYaw())).append(" Pitch: ").append(formatterROT.format(camera.getPitch())).toString());
				int rpx = ((int)(pos.x) >> 4) >> 3;
				int rpy = ((int)(pos.y) >> 4) >> 3;
				int rpz = ((int)(pos.z) >> 4) >> 3; 
				REGINFO.changeText(new StringBuilder().append("Region: ").append(rpx).append(" ").append(rpy).append(" ").append(rpz).toString());
				lastTimeUpdate = System.currentTimeMillis();
			}
		}
	}
	
	public static UIMenu destroy() {
		return menu;
	}
	
}
