package com.brett.engine.managers;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.tools.ImageToBuffer;

public class DisplayManager {
	    
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static int FPS_MAX = 120;

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(boolean isUsingFBOs) {
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setResizable(true);

			if (!isUsingFBOs)
				Display.create(new PixelFormat().withSamples(4), attribs);
			else
				Display.create(new PixelFormat(), attribs);
			Display.setTitle("RMS - V0.1A");

			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();

		ByteBuffer[] list = new ByteBuffer[2];
		try {
			list[0] = ImageToBuffer.convet(ImageIO.read(new File("resources/textures/icon/icon16.png")));
			list[1] = ImageToBuffer.convet(ImageIO.read(new File("resources/textures/icon/icon32.png")));
		} catch (IOException e) {e.printStackTrace();}
		Display.setIcon(list);
		ProjectionMatrix.updateProjectionMatrix();
	}

	public static void updateDisplay() {
		if (Display.wasResized()) { GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight()); ProjectionMatrix.updateProjectionMatrix(); }
		
		Display.sync(FPS_MAX);
		Display.update();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				Mouse.setGrabbed(!Mouse.isGrabbed());
			}
		}
		
		long currentFrameTime = getCurrentTime();
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
	}

	public static void closeDisplay() {
		Display.destroy();
	}

	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static float getFrameTimeMilis() {
		return delta;
	}

	public static float getFrameTimeSeconds() {
		return delta / 1000;
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void enableTransparentcy() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableTransparentcy() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}
