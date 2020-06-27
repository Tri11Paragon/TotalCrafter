package com.brett;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.screen.SinglePlayer;

public class Main {

	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public static boolean isOpen = true;
	
	/**
	 * Josiah doesn't get credit
	 * 
	 * ThinMatrix gets credit? (loader)
	 */
	
	public static void main(String[] args) {
		ScreenManager.pre_init();
		ScreenManager.init();
		ScreenManager.post_init();
		
		ScreenManager.switchScreen(new SinglePlayer());
		
		while (isOpen) {
			isOpen = !GLFW.glfwWindowShouldClose(DisplayManager.window);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(RED, GREEN, BLUE, 1);
			
			try {
				ScreenManager.update();
			} catch (Exception e) {
				GLFW.glfwSetInputMode(DisplayManager.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				ScreenManager.close();
				isOpen = false;
				e.printStackTrace();
				break;
			}
			
			DisplayManager.updateDisplay();
		}
		
		ScreenManager.close();
	}

}
