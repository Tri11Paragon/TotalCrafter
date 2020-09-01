package com.brett.engine.managers;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.tools.GLIcon;
import com.brett.engine.tools.Settings;
import com.brett.engine.ui.RescaleEvent;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DisplayManager {
	
	public static final String version = "0.2A";
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	
	public static boolean isMouseGrabbed = false;

	public static long window;
	
	private static long lastFrameTime;
	private static double delta;
	
	public static double mouseX,mouseY;
	
	public static List<RescaleEvent> rescales = new ArrayList<RescaleEvent>();

	public static void createDisplay(boolean isUsingFBOs) {
		System.out.println("LWJGL Version: " + Version.getVersion() + "!");
		System.out.println("Game Version: " + version);
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		
		
		window = glfwCreateWindow(WIDTH, HEIGHT, "TotalCrafter - V" + version, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				glfwSetInputMode(window, GLFW_CURSOR, glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
				isMouseGrabbed = glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED ? true : false;
			}
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
		});
		
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
		});
		
		glfwSetWindowSizeCallback(window, (window, x, y) -> {
			DisplayManager.WIDTH = x;
			DisplayManager.HEIGHT = y;
			GL11.glViewport(0, 0, x, y); ProjectionMatrix.updateProjectionMatrix();
			for (int i = 0; i < rescales.size(); i++)
				rescales.get(i).rescale();
			//ScreenManager.monospaced = new FontType(ScreenManager.loader.loadTexture("fonts/monospaced-72", 0), new File("resources/textures/fonts/monospaced-72.fnt"));
			//ScreenManager.fonts.remove("mono");
			//ScreenManager.fonts.put("mono", ScreenManager.monospaced);
		});
		
		glfwSetScrollCallback(window, (window, x, y) -> {
			InputMaster.scrollMoved((int)y);
		});
		
		glfwSetCursorPosCallback(window, (window, x, y) -> {
			DisplayManager.mouseX = x;
			DisplayManager.mouseY = y;
		});
		
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(window, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(Settings.VSYNC);

		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		glfwWindowHint(GLFW_SAMPLES, 4);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		GLIcon gli = new GLIcon("resources/textures/icon/icon16.png", "resources/textures/icon/icon32.png");
		glfwSetWindowIcon(window, gli.getBuffer());
		
	}

	public static double getDX() {
		return mouseX - lx;
	}
	
	public static double getDY() {
		return mouseY - ly;
	}
	
	private static double lx, ly;
	
	public static void updateDisplay() {
		
		lx = mouseX;
		ly = mouseY;
		
		glfwSwapBuffers(window);
		glfwPollEvents();
		
		long currentFrameTime = getCurrentTime();
		delta = currentFrameTime - lastFrameTime;
		lastFrameTime = currentFrameTime;
	}

	public static void closeDisplay() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null);
	}

	private static long getCurrentTime() {
		return System.nanoTime();
	}

	public static double getFrameTimeMilis() {
		return delta / 1000000d;
	}

	public static double getFrameTimeSeconds() {
		return delta / 1000000000d;
	}
	
	public static double getFPS() {
		return 1000000000d / delta;
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
