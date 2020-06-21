package com.brett;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.ScreenManager;

public class Main {

	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public static boolean isOpen = true;
	
	public static void main(String[] args) {
		ScreenManager.pre_init();
		ScreenManager.init();
		
		while (isOpen) {
			isOpen = !Display.isCloseRequested();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(RED, GREEN, BLUE, 1);
			
			try {
				ScreenManager.update();
			} catch (Exception e) {
				Mouse.setGrabbed(false);
				ScreenManager.close();
				isOpen = false;
				break;
			}
			
			DisplayManager.updateDisplay();
		}
		
		ScreenManager.close();
	}

}
