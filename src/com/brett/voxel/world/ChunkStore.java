package com.brett.voxel.world;

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
import com.brett.voxel.VoxelScreenManager;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.noisefunctions.ChunkNoiseFunction;
import com.brett.world.terrain.noisefunctions.NoiseFunction;

/**
 *
 * @author brett
 * @date Feb. 19, 2020
 */

public class ChunkStore {

	public static int renderDistance = 3;
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
				while(VoxelScreenManager.isOpen) {
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
				//if (!cam.isPointInFrustum(i, cam.getPosition().y, k))
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
	
	/**
	 * Note this is in chunk pos and not world pos.
	 */
	public Chunk getChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			Region r = chunks.get(regionPosX, regionPosZ);
			if (r == null)
				return null;
			return r.getChunk(x, z);
		}else
			return null;
	}
	
	public short getBlock(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x/Chunk.x + xoff), (int) (z/Chunk.z + zoff));
		if (c == null)
			return -1;
		x%=16;
		z%=16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getBlock((int)x, (int)y, (int)z);
	}
	
	public void setBlock(float x, float y, float z, short block) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int)(x/(float)Chunk.x) + xoff, (int)(z/(float)Chunk.z) + zoff);
		if (c == null)
			return;
		x %= 16;
		z %= 16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlock((int)x,(int)y, (int)z, block);
	}
	
	public void setBlockBIAS(float x, float y, float z, short block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	public void setBlockBIAS(float x, float y, float z, int block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	public short getBlockBIAS(float x, float y, float z) {
		// fix for something that is not broken but this is needed
		// yes

		// this is actually one of those
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		return getBlock(x, y, z);
	}
	
	public void setBlock(float x, float y, float z, int block) {
		setBlock(x, y, z, (short)block);
	}
	
	private float biasNegative(float f, int unitSize) {
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
