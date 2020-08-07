package com.brett.world;

import com.brett.world.chunks.data.RenderMode;

/**
* @author Brett
* @date Jul. 9, 2020
*/

public class Lighting {
	
	public static World world;
	
	public static void init(World world) {
		Lighting.world = world;
	}
	
	public static void updateLighting(int x, int y, int z, int level) {
		if (level < 0)
			return;
		world.setBlockLevel(x, y, z, (byte)level);
		if (world.getRenderMode(x+1, y, z) != RenderMode.SOLID && world.getBlockLevel(x+1, y, z) + 2 <= level)
			updateLighting(x+1, y, z, level-1);
		if (world.getRenderMode(x-1, y, z) != RenderMode.SOLID && world.getBlockLevel(x-1, y, z) + 2 <= level)
			updateLighting(x-1, y, z, level-1);
		if (world.getRenderMode(x, y, z+1) != RenderMode.SOLID && world.getBlockLevel(x, y, z+1) + 2 <= level)
			updateLighting(x, y, z+1, level-1);
		if (world.getRenderMode(x, y, z-1) != RenderMode.SOLID && world.getBlockLevel(x, y, z-1) + 2 <= level)
			updateLighting(x, y, z-1, level-1);
		if (world.getRenderMode(x, y+1, z) != RenderMode.SOLID && world.getBlockLevel(x, y+1, z) + 2 <= level)
			updateLighting(x, y+1, z, level-1);
		if (world.getRenderMode(x, y-1, z) != RenderMode.SOLID && world.getBlockLevel(x, y-1, z) + 2 <= level)
			updateLighting(x, y-1, z, level-1);
	}
	
	public static void updateMinedLight(int x, int y, int z) {
		byte ll = (byte) Math.min(world.getBlockLevel(x-1, y, z) + world.getSunLevel(x-1, y, z), 15);
		byte lr = (byte) Math.min(world.getBlockLevel(x+1, y, z) + world.getSunLevel(x+1, y, z), 15);
		byte lf = (byte) Math.min(world.getBlockLevel(x, y, z+1) + world.getSunLevel(x, y, z+1), 15);
		byte lb = (byte) Math.min(world.getBlockLevel(x, y, z-1) + world.getSunLevel(x, y, z-1), 15);
		byte lt = (byte) Math.min(world.getBlockLevel(x, y+1, z) + world.getSunLevel(x, y+1, z), 15);
		byte ld = (byte) Math.min(world.getBlockLevel(x, y-1, z) + world.getSunLevel(x, y-1, z), 15);
		if (ll == 0 && lr == 0 && lf == 0 && lb == 0 && lt == 0 && ld == 0)
			return;
		byte[] listb = new byte[] {ll, lr, lf, lb, lt, ld};
		for (int i = 0; i < listb.length; i++) {
			for (int j = i; j < listb.length; j++) {
				if (listb[i] < listb[j]) {
					byte tmp = listb[i];
					listb[i] = listb[j];
					listb[j] = tmp;
				}
			}
		}
		updateLighting(x, y, z, (byte)(listb[0]-1));
	}
	
	public static void updatePlacedLight(int x, int y, int z) {
		world.setBlockLevel(x, y, z, (byte) 0);
	}
	
	public static void updateLightingRemove(int x, int y, int z, int val) {
		if (val < 0)
			return;
		//updateLighting(x, y, z, 0);
		world.setBlockLevel(x, y, z, (byte)0);
		byte ngl = world.getBlockLevel(x+1, y, z);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x+1, y, z, val);
		else if (ngl >= val)
			updateLighting(x+1, y, z, val);
		
		ngl = world.getBlockLevel(x-1, y, z);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x-1, y, z, val);
		else if (ngl >= val)
			updateLighting(x-1, y, z, val);
		
		ngl = world.getBlockLevel(x, y+1, z);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x, y+1, z, val);
		else if (ngl >= val)
			updateLighting(x, y+1, z, val);
		
		ngl = world.getBlockLevel(x, y-1, z);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x, y-1, z, val);
		else if (ngl >= val)
			updateLighting(x, y-1, z, val);
		
		ngl = world.getBlockLevel(x, y, z-1);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x, y, z-1, val);
		else if (ngl >= val)
			updateLighting(x, y, z-1, val);
		
		ngl = world.getBlockLevel(x, y, z+1);
		if (ngl != 0 && ngl < val)
			updateLightingRemove(x, y, z+1, val);
		else if (ngl >= val)
			updateLighting(x, y, z+1, val);
	}
	
}
