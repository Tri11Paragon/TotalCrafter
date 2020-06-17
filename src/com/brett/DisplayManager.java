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

import com.brett.console.Console;
import com.brett.renderer.gui.UIMaster;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.inventory.PlayerInventory;

public class DisplayManager {

	// width and height of the screen
	// Don't change these as font is not 100% screen size safe.
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	// note: this value is overwritten by the settings found in settings.txt
	// max FPS
	public static int FPS_MAX = 120;
	
	// time of the last frame
	private static long lastFrameTime;
	// amount of frames between last frame.
	private static float delta;
	
	public static void createDisplay(boolean isUsingFBOs) {
		
		// creates a context for this OpenGL (3.3)
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);
		
		try {
			// creates the display with the width and height
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			// withsamples = AntiAliasing
			/**
			 * Why disable AA if you are rendering to FBOs and not the default display buffer?
			 * Well its because this kind of AA only applies to the default display buffer (the one drawn on screen)
			 * and since when you use FBOs you are actually only rendering one quad to the screen, OpenGL doesn't have any
			 * edges to AA. OpenGL doesn't see the edges that are rendered on the quad (edges in the FBO), 
			 * it only sees the edges of the quad which should be the edges of your screen, which results in nothing happening.
			 */
			// this should be enabled if FBOs are not being used
			if (!isUsingFBOs) // no reason to use more then 4 samples. 
				Display.create(new PixelFormat().withSamples(SettingsLoader.SAMPLES), attribs);
			else
				Display.create(new PixelFormat(), attribs);
			// set the window title
			Display.setTitle("RMS - V0.11A // Майнкрафт³ - V0.42.3A");
			// enable multisampling (AA)
			// this needs to be enabled even if you are using FBOs
			// (as there is a way of doing AA on FBOs)
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {e.printStackTrace();}
		
		// tells OpenGL that we will be drawing in these areas.
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		// this just assigns the icon textures.
		ByteBuffer[] list = new ByteBuffer[2];
		try {
			list[0] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("resources/textures/icon/icon16.png")), false, true, null);
			list[1] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("resources/textures/icon/icon32.png")), false, true, null);
		} catch (IOException e) {}
		// assign icons.
		Display.setIcon(list);
	}
	
	public static void updateDisplay() {
		// tell the display to sync at this FPS.
		Display.sync(FPS_MAX);
		// updates the current frame buffer with what we have drawn over the last frame.
		Display.update();
		// allows you to use the mouse if you want (not really needed TODO: replace with a menu)
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Keyboard.next() && !Console.getIsOpen() && !PlayerInventory.isOpen) {
			if(Keyboard.getEventKeyState()) {
				Mouse.setGrabbed(!Mouse.isGrabbed());
				UIMaster.menu.setEnabled(!Mouse.isGrabbed());
			}
		}
		// calculate the amount of time that frame took
		long currentFrameTime = getCurrentTime();
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
	}
	
	public static void closeDisplay() {
		
		// cleans up OpenGL
		Display.destroy();
		
	}
	
	// gets the current time
	private static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	/**
	 *  gets the frame time in ms
	 */
	public static float getFrameTimeMilis() {
		return delta;
	}
	
	/**
	 * gets the frame time in seconds
	 */
	public static float getFrameTimeSeconds() {
		return delta / 1000;
	}
	
}
