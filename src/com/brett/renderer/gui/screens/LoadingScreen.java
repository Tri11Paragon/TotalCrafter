package com.brett.renderer.gui.screens;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Mar. 2, 2020
* This is unused and currently doesn't work.
*/

public class LoadingScreen {
	
	// TODO: use background loading?
	// does not currently work
	
	private Matrix4f translationMatrix;
	private Matrix4f logoMatrix;
	private GUIRenderer renderer;
	private int max;
	private int current;
	@SuppressWarnings("unused")
	private int grey;
	private int darkgrey;
	private int lightgrey;
	private int logoTexture;
	
	public LoadingScreen(Loader loader, GUIRenderer renderer, int maxIterations) {
		this.grey = loader.loadTexture("grey");
		this.darkgrey = loader.loadTexture("darkgrey");
		this.lightgrey = loader.loadTexture("lightgrey");
		this.logoTexture = loader.loadTexture("icon/logo");
		float w = Display.getWidth();
		float h = Display.getHeight();
		this.translationMatrix = Maths.createTransformationMatrix(new Vector2f(0,0), new Vector2f(1,1));
		this.logoMatrix = Maths.createTransformationMatrixCenteredSTATIC(w, h, 200, 200, 45);
		this.renderer = renderer;
		this.max = maxIterations;
	}
	
	public void render(int progress) {
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(1, 1, 1, 1);
		renderer.startrender();
		renderer.render(darkgrey, translationMatrix);
		renderer.render(logoTexture, logoMatrix);
		current+=progress;
		renderer.render(lightgrey, 0, 0, 200*(current/max), 50);
		renderer.stoprender();
		Display.update();
		Display.sync(DisplayManager.FPS_MAX);
	}
	
}
