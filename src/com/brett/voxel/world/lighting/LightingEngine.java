package com.brett.voxel.world.lighting;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.lwjgl.Sys;

import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.ChunkStore;

/**
*
* @author brett
* @date Mar. 31, 2020
*/

public class LightingEngine {
	
	private static MultiKeyMap<Integer, Byte> lightSources = new MultiKeyMap<Integer, Byte>();
	private static MultiKeyMap<Integer, Byte> permalightSources = new MultiKeyMap<Integer, Byte>();
	//private static VoxelWorld world;
	
	private static byte sunLevel = 0;
	private static VoxelWorld world;
	
	public static void init(VoxelWorld world) {
		LightingEngine.world = world;
		new Thread(new Runnable() {
			public void run() {
				lightSources.putAll(permalightSources);
				long lastTime = 0;
				long startTime = 0;
				//NoiseFunction nf = world.chunk.getNf();
				while (VoxelScreenManager.isOpen) {
					// this is required for the lighting engine to work.
					// im not joking.
					try {Thread.sleep(1);} catch (InterruptedException e1) {}
					// ^thats a race condition without a reason
					if (lightSources.size() == 0)
						continue;
					try {
						startTime = Sys.getTime();
						MapIterator<MultiKey<? extends Integer>, Byte> it = lightSources.mapIterator();
						while (it.hasNext()) {
							MultiKey<? extends Integer> key = it.next();
							applyLightPatern(world, key.getKey(0), key.getKey(1), key.getKey(2), it.getValue());
						}
	
						lightSources.clear();
	
						lastTime = Sys.getTime();
						Thread.sleep(32 - (lastTime - startTime) > 0 ? 32 - (lastTime - startTime) : 0);
					} catch (Exception e) {}
				}
			}
		}).start();
	}
	
	public static void addLightSource(int x, int y, int z, byte level) {
		lightSources.put(x, y, z, level);
		permalightSources.put(x, y, z, level);
	}
	
	public static void removeLightSource(int x, int y, int z, byte level) {
		lightSources.put(x,y,z, (byte) -level);
		permalightSources.removeAll(x,y,z);
		//recalculate();
		//recalcalculateSun();
	}
	
	public static void applyLightPatern(VoxelWorld world, int x, int y, int z, byte level) {
		boolean neg = false;
		if (level < 0) {
			neg = true;
			level = (byte) -level;
		}
		byte[][][] levels = lightup(level);
		byte length = (byte) (level + 1);
		byte center = (byte) (length/2);
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				for (int k = 0; k < length; k++) {
					int xpos = (i - center);
					int ypos = (j - center);
					int zpos = (k - center);
					if (neg)
						world.chunk.setLightLevel(x + xpos, y + ypos, z + zpos, (byte)0);
					else
						world.chunk.setLightLevel(x + xpos, y + ypos, z + zpos, levels[i][j][k]);
				}
			}
		}
	}
	
	public static byte[][][] lightup(byte level){
		byte[][][] ba = new byte[level+1][level+1][level+1];
		byte center = (byte) ((level+1)/2);
		for (byte i = 0; i < level+1; i++) {
			for (byte j = 0; j < level+1; j++) {
				for (byte k = 0; k < level+1; k++) {
					ba[i][j][k] = 0;
					if (i == center && j == center && j == center) {
						ba[i][j][k] = (byte) level;
						continue;
					}
					byte distancex = distance(center, i);
					byte distancey = distance(center, j);
					byte distancez = distance(center, k);
					ba[i][j][k] = (byte) ((center*3) - distancex - distancey - distancez);
				}
			}
		}
		return ba;
	}
	
	private static byte distance(byte f, byte d) {
		return (byte) (f - d);
	}
	
	public static void recalculate() {
		lightSources.putAll(permalightSources);
	}
	
	public static void recalcalculateSun() {
		int renderDistance = ChunkStore.renderDistance;
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				
			}
		}
	}
	
	public static void changeSunLevel(byte level) {
		sunLevel = level;
		recalculate();
	}
	
}
