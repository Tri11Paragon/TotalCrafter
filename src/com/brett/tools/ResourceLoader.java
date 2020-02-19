package com.brett.tools;

/**
*
* @author brett
*
*/

public class ResourceLoader {

	
	private static Thread load1;
	
	public static void init() {
		load1 = new Thread(new Runnable() {
			@Override
			public void run() {
				
			}
		});
	}
	
	public static void load() {
		
	}
	
	public static void startLoaders() {
		load1.start();
	}
	
}
