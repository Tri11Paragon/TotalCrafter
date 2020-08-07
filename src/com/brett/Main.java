package com.brett;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.brett.engine.DebugInfo;
import com.brett.engine.SyncSave;
import com.brett.engine.data.IKeyState;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.tools.Settings;
import com.brett.engine.ui.console.Console;
import com.brett.engine.ui.screen.SinglePlayer;

public class Main {

	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public static boolean isOpen = true;
	public static MemoryMXBean mx;
	public static OperatingSystemMXBean osx;
	public static RuntimeMXBean rnx;
	public static ThreadMXBean thx;
	public static String os;
	public static String os_version;
	public static String os_arch;
	public static String file_separator;
	public static String path_separator;
	public static String line_separator;
	public static String user_name;
	public static String user_home;
	public static String user_workingdir;
	public static int processors;
	
	
	/**
	 * Josiah doesn't get credit
	 * 
	 * ^.*$
	 * 
	 * ThinMatrix gets credit? (loader)
	 */
	
	public static void main(String[] args) {
		/**
		 * Assign system variables
		 */
		mx = ManagementFactory.getMemoryMXBean();
		osx = ManagementFactory.getOperatingSystemMXBean();
		rnx = ManagementFactory.getRuntimeMXBean();
		thx = ManagementFactory.getThreadMXBean();
		os = System.getProperty("os.name");
		os_version = System.getProperty("os.version");
		os_arch = System.getProperty("os.arch");
		file_separator = System.getProperty("file.separator");
		path_separator = System.getProperty("path.separator");
		line_separator = System.getProperty("line.separator");
		user_name = System.getProperty("user.name");
		user_home = System.getProperty("user.home");
		user_workingdir = System.getProperty("user.dir");
		processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4)
			processors = 4;
		/**
		 * Display important info about OS
		 */
		System.out.println("---------------------------------------");
		System.out.println("OS: " + os + " " + os_version + " " + os_arch);
		System.out.println("User: " + user_name + "@" + user_home);
		System.out.println("Working Directory: " + user_workingdir);
		System.out.println("Number of cores: " + processors);
		System.out.println();
		/**
		 * Inits
		 */
		ScreenManager.pre_init();
		ScreenManager.init();
		ScreenManager.post_init();
		System.out.println();
		printMemoryInfo();
		
		InputMaster.keyboard.add(new IKeyState() {
			
			@Override
			public void onKeyReleased(int keys) {}
			
			@Override
			public void onKeyPressed(int keys) {
				if (keys == GLFW.GLFW_KEY_F3)
					DebugInfo.toggle();
			}
		});
		
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
			
			DebugInfo.update();
			Console.update();
			DisplayManager.updateDisplay();
			SyncSave.sync(Settings.FPS);
		}
		isOpen = false;
		
		ScreenManager.close();
	}
	
	public static void printMemoryInfo() {
		System.out.println("---------{ Memory Info }---------");
		System.out.println(new StringBuilder().append("Free Memory: ").append((Runtime.getRuntime().freeMemory()/1024)/1024).append(" MB"));
		System.out.println(new StringBuilder().append("Total Memory: ").append((Runtime.getRuntime().totalMemory()/1024)/1024).append(" MB"));
		System.out.println(new StringBuilder().append("Max Memory: ").append((Runtime.getRuntime().maxMemory()/1024)/1024).append(" MB"));
		System.out.println();
	}
	
	public static void printHeapInfo() {
		System.out.println("Used Heap Space: " + mx.getHeapMemoryUsage());
		System.out.println("Non Heap Memory Usage: " + mx.getNonHeapMemoryUsage());
	}
	
	public static void printAverageLoad() {
		System.out.println("System Average Load: " + osx.getSystemLoadAverage());
	}
	
}
