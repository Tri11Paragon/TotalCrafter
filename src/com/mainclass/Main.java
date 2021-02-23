package com.mainclass;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import com.brett.voxel.VoxelScreenManager;

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
	public static String username = null;
	public static String password = null;
	public static String token = null;
	public static int processors = 8;
	
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			String[] vars = args[i].split("=");
			if (vars == null)
				continue;
			if (vars.length < 2)
				continue;
			if(vars[0].contentEquals("username"))
				username = vars[1];
			if (vars[0].contentEquals("password"))
				password = vars[1];
		}
		//token = ClientAuth.setToken(username, password);
		
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
		System.out.println("Current Thread: " + Thread.currentThread());
		System.out.println();
		VoxelScreenManager.init();
	}

}

/* TODO List
 * 694 // 7332108111118101321211111173287105108108326051
 * 
 * make lights when they are inside objects work.
 * Background resource loader
 * make FBOs have more then 2 color attachments?
 * add multi-terrain support (check)
 * add settings saver / loader
 * add world loader / saver
 * add world builder
 * merge FBO classes
 * add entity picking
 * add shadows to entities
 * finish water renderer / clean up water renderer
 * add some uniforms for font rendering
 * add bash
 * add console (checkish)
 * ^.*$
 */
