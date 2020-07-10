package com.brett.world;

import java.util.Stack;

/**
* @author Brett
* @date Jul. 9, 2020
*/

public class Lighting {
	
	public static World world;
	public static Stack<Integer> lights = new Stack<Integer>();
	
	public static void init(World world) {
		Lighting.world = world;
	}
	
	public static void updateLighting(int x, int y, int z, int level) {
		world.setLightLevel(x, y, z, (byte)level);
		
	}
	
}
