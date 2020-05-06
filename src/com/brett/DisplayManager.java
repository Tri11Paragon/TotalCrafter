package com.brett;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.ImageIOImageData;

public class DisplayManager {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	// note: this value is overwritten by the settings found in settings.txt
	public static int FPS_MAX = 0;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay(boolean isUsingFBOs) {
		
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			// withsamples = Antialiasing
			// this should be enabled if FBOs are not being used
			if (!isUsingFBOs)
				Display.create(new PixelFormat().withSamples(4), attribs);
			else
				Display.create(new PixelFormat(), attribs);
			Display.setTitle("RMS - V0.11A // Майнкрафт³ - V0.30.0A");
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {e.printStackTrace();}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		ByteBuffer[] list = new ByteBuffer[2];
		try {
			list[0] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("resources/textures/icon/icon16.png")), false, true, null);
			list[1] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("resources/textures/icon/icon32.png")), false, true, null);
		} catch (IOException e) {}
		Display.setIcon(list);
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_MAX);
		Display.update();
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
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
	
}
