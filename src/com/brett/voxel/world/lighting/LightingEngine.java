package com.brett.voxel.world.lighting;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.lwjgl.Sys;

import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Mar. 31, 2020
*/

public class LightingEngine {
	
	public static final int secondsPerDay = 600 * 1000;
	public static final int secondsPerHalf = secondsPerDay/2;
	public static final int secondsPerEight = secondsPerDay/8;
	
	private static MultiKeyMap<Integer, Byte> lightSources = new MultiKeyMap<Integer, Byte>();
	private static MultiKeyMap<Integer, Byte> permalightSources = new MultiKeyMap<Integer, Byte>();
	//private static VoxelWorld world;
	
	public static byte sunLevel = 15;
	private static Camera cam;
	private static VoxelWorld world;
	
	private static long startSTime = 0;
	public static void init(VoxelWorld world, Camera cam) {
		LightingEngine.world = world;
		LightingEngine.cam = cam;
		new Thread(new Runnable() {
			public void run() {
				startSTime = System.currentTimeMillis();
				lightSources.putAll(permalightSources);
				long lastTime = 0;
				long startTime = 0;
				//NoiseFunction nf = world.chunk.getNf();
				while (VoxelScreenManager.isOpen) {
					// TODO: do this when the world is loaded!
					/*long timeOff = System.currentTimeMillis() - startSTime;
					timeOff %= secondsPerDay;
					if (timeOff < secondsPerEight) {
						byte w = (byte) (15 * ((float)timeOff / (float)secondsPerEight));
						if (w != sunLevel)
							changeSunLevel(w);
					} else {
						if (timeOff >= secondsPerEight && timeOff <= (secondsPerHalf)) {
							if (sunLevel != 15)
								changeSunLevel((byte)15);
						} else {
							if (timeOff > secondsPerHalf && timeOff <= (secondsPerHalf+secondsPerEight)) {
								byte w = (byte) (15 - (15 * ((float)timeOff / (float)(secondsPerHalf+secondsPerEight))));
								if (w != sunLevel)
									changeSunLevel(w);
							} else {
								if (sunLevel  != 0)
									changeSunLevel((byte)0);
							}
						}
					}*/
					// this is required for the lighting engine to work.
					// im not joking.
					try {Thread.sleep(1);} catch (InterruptedException e1) {}
					if (lightSources.size() == 0)
						continue;
					try {
						startTime = Sys.getTime();
						MapIterator<MultiKey<? extends Integer>, Byte> it = lightSources.mapIterator();
						while (it.hasNext()) {
							MultiKey<? extends Integer> key = it.next();
							//if (it.getValue() < 0)
								//recalculate();
							//applyLightPatern(world, key.getKey(0), key.getKey(1), key.getKey(2), it.getValue());
							//recalcualteChunks(key.getKey(0), key.getKey(2));
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
		recalculate();
	}
	
	public static void applyLightPatern(VoxelWorld world, int x, int y, int z, byte level) {
		boolean neg = false;
		if (level < 0) {
			neg = true;
			level = (byte) -level;
		}
		
		for (int i = -level; i < (level-1); i++) {
			for (int j = -level; j < (level-1); j++) {
				for (int k = -level; k < (level-1); k++) {
					int xp = x+i;
					int yp = y+j;
					int zp = z+k;
					byte l = level;
					l = (byte) (level - Math.abs(i) - Math.abs(j) - Math.abs(k));
					if (l < 0)
						l = 0;
					if (neg) {
						l = (byte) (world.chunk.getLightLevel(xp, yp, zp) - l);
						if (l < 0)
							l = 0;
						world.chunk.setLightLevel(xp, yp, zp, l);
					} else {
						l += world.chunk.getLightLevel(xp, yp, zp);
						if (l > 15)
							l = 15;
						world.chunk.setLightLevel(xp, yp, zp, l);
					}
				}
			}
		}
	}
	
	public static void recalculate() {
		lightSources.putAll(permalightSources);
	}
	
	public static void recalcalculateSun() {
		int renderDistance = ChunkStore.renderDistance;
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				Chunk c = world.chunk.getChunk(cx, cz);
				if (c == null)
					continue;
				/*for (int x = 0; x < Chunk.x; x++) {
					for (int z = 0; z < Chunk.z; z++) {
						int y = c.getHeightA(x, z);
						byte lel = (byte) ( sunLevel);
						if (lel > 15)
							lel = 15;
					}
				}*/
				c.remesh();
			}
		}
	}
	
	public static void changeSunLevel(byte level) {
		sunLevel = level;
		recalcalculateSun();
	}
	
	private static void recalcualteChunks(int x, int z) {
		ChunkStore cs = world.chunk;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cx = (int)(x/(float)Chunk.x) + xoff;
		int cz = (int)(z/(float)Chunk.z) + zoff;
		Chunk main = cs.getChunk(cx, cz);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx+1, cz+1);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx+1, cz);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx, cz+1);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx-1, cz);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx, cz-1);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx+1, cz-1);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx-1, cz+1);
		if (main != null)
			main.remeshNo();
		main = cs.getChunk(cx-1, cz-1);
		if (main != null)
			main.remeshNo();
	}
	
}