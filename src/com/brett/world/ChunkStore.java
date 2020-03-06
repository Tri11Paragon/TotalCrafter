package com.brett.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.Tuple;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.noisefunctions.ChunkNoiseFunction;
import com.brett.world.terrain.noisefunctions.NoiseFunction;
import com.tester.Main;

/**
 *
 * @author brett
 * @date Feb. 19, 2020
 */

public class ChunkStore {

	public static final int renderDistance = 3;
	public static final String worldLocation = "worlds/w1/";
	public static final String dimLocation = "worlds/w1/DIM";
	public static File wfolder = new File(worldLocation);
	public static File[] wfiles = wfolder.listFiles();

	// actually the best way of storing chunk data.
	// however will need to add a way of moving between active and non active
	// chunks.
	private MultiKeyMap<Integer, Region> chunks = new MultiKeyMap<Integer, Region>();
	private List<Tuple<Integer, Integer>> ungeneratedChunks = Collections.synchronizedList(new ArrayList<Tuple<Integer, Integer>>());

	@SuppressWarnings("unused")
	private List<Chunk> activeChunks = new ArrayList<Chunk>();

	protected Camera cam;
	private Loader loader;
	private NoiseFunction nf;
	private VoxelWorld world;

	public ChunkStore(Camera cam, Loader loader, VoxelWorld world) {
		new File(dimLocation).mkdirs();
		LevelLoader.loadLevelData(worldLocation);
		this.cam = cam;
		this.loader = loader;
		this.nf = new ChunkNoiseFunction(LevelLoader.seed);
		this.world = world;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(Main.isOpen) {
					for (int i = 0; i < ungeneratedChunks.size(); i++) {
						Tuple<Integer, Integer> g = ungeneratedChunks.get(i);
						setChunk(generateChunk(g.getX(), g.getY()), g.getX(), g.getY());
						ungeneratedChunks.remove(i);
					}
				}
			}
		}).start();
	}

	public Chunk generateChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			Chunk c = chunks.get(regionPosX, regionPosZ).getChunk(x, z);
			if (c == null) {
				c = new Chunk(loader,world, nf, x, z);
				chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
				return c;
			} else {
				return c;
			}
		} else {
			Region r = Region.loadRegion(loader, this, regionPosX, regionPosZ, worldLocation);
			chunks.put(regionPosX, regionPosZ, r);
			Chunk c = r.getChunk(x, z);
			if (c == null) {
				c = new Chunk(loader,world, nf, x, z);
				chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
				return c;
			}else
				return c;
		}
	}

	public void saveChunks() {
		System.out.println("Saving world");
		MapIterator<MultiKey<? extends Integer>, Region> regionIt = chunks.mapIterator();
		while (regionIt.hasNext()) {
			regionIt.next();
			Region val = regionIt.getValue();
			val.saveRegion(worldLocation);
		}
		LevelLoader.saveLevelData(worldLocation);
		System.out.println("World Saved");
	}

	public void renderChunks(VoxelShader shader) {
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				//if (!cam.planeIntersection(i, k, -16))
				//	continue;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				c.render(shader);
			}
		}
	}
	
	public void queChunk(int cx, int cz) {
		ungeneratedChunks.add(new Tuple<Integer, Integer>(cx, cz));
	}

	public void cleanup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				saveChunks();
			}
		}).start();
	}

	public void setChunk(Chunk c, int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
			return;
		}
		System.out.println(regionPosX + " " + regionPosZ);
		chunks.put(regionPosX, regionPosZ, new Region(regionPosX, regionPosZ));
		chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
	}

	public Chunk getChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ))
			return chunks.get(regionPosX, regionPosZ).getChunk(x, z);
		else
			return null;
	}
	
	public short getBlock(int x, int y, int z) {
		Chunk c = getChunk(x, z);
		if (c == null)
			return 0;
		x%=16;
		z%=16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getBlock(x, y, z);
	}
	
	public void setBlock(int x, int y, int z, short block) {
		Chunk c = getChunk(x, z);
		if (c == null)
			return;
		x%=16;
		z%=16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlock(x, y, z, block);
	}
	
	private int biasNegative(int f, int unitSize) {
		return unitSize - f;
	}

	/**
	 * Returns the chunk based on actual x position and not based on offset I would
	 * avoid this if possible.
	 */
	public Chunk getChunkUn(int x, int z) {
		x /= Chunk.x;
		z /= Chunk.z;
		return getChunk(x, z);
	}

}
