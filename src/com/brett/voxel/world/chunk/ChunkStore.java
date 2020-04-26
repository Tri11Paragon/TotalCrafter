package com.brett.voxel.world.chunk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.Tuple;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.LevelLoader;
import com.brett.voxel.world.Region;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.noisefunctions.NoiseFunction;
import com.brett.world.terrain.noisefunctions.SuperNoise;

/**
 *
 * @author brett
 * @date Feb. 19, 2020
 */

public class ChunkStore {

	public static int renderDistance = 12;
	public static final String worldLocation = "worlds/w1/";
	public static final String dimLocation = "worlds/w1/DIM";
	public static File wfolder = new File(worldLocation);
	public static File[] wfiles = wfolder.listFiles();

	// actually the best way of storing chunk data.
	// however will need to add a way of moving between active and non active
	// chunks.
	private MultiKeyMap<Integer, Region> chunks = new MultiKeyMap<Integer, Region>();
	private MultiKeyMap<Integer, Region> chunksCopy = null;
	private List<Tuple<Integer, Integer>> ungeneratedChunks = Collections.synchronizedList(new ArrayList<Tuple<Integer, Integer>>());

	protected Camera cam;
	private Loader loader;
	private NoiseFunction nf;
	private VoxelWorld world;

	public ChunkStore(Camera cam, Loader loader, VoxelWorld world) {
		new File(dimLocation).mkdirs();
		LevelLoader.loadLevelData(worldLocation);
		this.cam = cam;
		this.loader = loader;
		//this.nf = new ChunkNoiseFunction(LevelLoader.seed);
		this.nf = new SuperNoise(LevelLoader.seed);
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(VoxelScreenManager.isOpen) {
					// what the region list will look like.
					// butters = collin?
					try {
						Thread.sleep(20*1000);
					} catch (InterruptedException e) {}
					
					if (chunksCopy == null) {
						try {
							chunksCopy = new MultiKeyMap<Integer, Region>();
							for (int i = -renderDistance; i < renderDistance; i++) {
								for (int k = -renderDistance; k < renderDistance; k++) {
									int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
									int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
									int xoff = 0, zoff = 0;
									if (cx < 0)
										xoff = -1;
									if (cz < 0)
										zoff = -1;
									int rx = cx / Region.x + xoff;
									int rz = cz / Region.z + zoff;
									if (chunksCopy == null)
										chunksCopy = new MultiKeyMap<Integer, Region>();
									Region c = chunks.get(rx, rz);
									if (c != null) {
										chunksCopy.put(rx, rz, c);
									}
								}
							}
						} catch (NullPointerException e) {System.err.println("Somehow something that isn't null is null. Possible race condition detected!");}
					}
					//System.gc();
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
			if (chunks.get(regionPosX, regionPosZ) == null)
				return new Chunk(loader,world, nf, x, z);
			else {
				try {
					Chunk c = chunks.get(regionPosX, regionPosZ).getChunk(x, z);
					if (c == null) {
						c = new Chunk(loader,world, nf, x, z);
						chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
						return c;
					} else {
						return c;
					}
				} catch (NullPointerException e) {
					System.out.println("Hey we got a broken pipe here!");
					return new Chunk(loader,world, nf, x, z);
				}
			}
		} else {
			Region r = Region.loadRegion(loader, world, regionPosX, regionPosZ, worldLocation);
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
		System.out.println("Saving World");
		MapIterator<MultiKey<? extends Integer>, Region> regionIt = chunks.mapIterator();
		while (regionIt.hasNext()) {
			try {
				regionIt.next();
			} catch (ConcurrentModificationException e) {System.err.println("Tried saving map while loading it. \nPlease wait for map to complete loading before exiting game.");}
			Region val = regionIt.getValue();
			if (val != null)
				val.saveRegion(worldLocation, false);
		}
		System.out.println("World Saved");
	}
	
	public void renderChunks(VoxelShader shader) {
		if (chunksCopy != null) {
			MapIterator<MultiKey<? extends Integer>, Region> rg = chunksCopy.mapIterator();
			while (rg.hasNext()) {
				MultiKey<? extends Integer> r = rg.next();
				chunks.remove(r);
			}
			MultiKeyMap<Integer, Region> rgs = new MultiKeyMap<Integer, Region>();
			rgs.putAll(chunks);
			queSave(rgs);
			chunks.clear();
			chunks.putAll(chunksCopy);
			System.out.println("Chunking size: " + chunks.size());
			chunksCopy = null;
		}
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				float fx = cam.getPosition().x + (i*Chunk.x);
				float fz = cam.getPosition().z + (k * Chunk.z);
				if (!cam.cubeInFrustum(fx, 0, fz, fx + Chunk.x, Chunk.y, fz + Chunk.z))
					continue;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				//if (!c.getNull())
				c.render(shader);
			}
		}
		
	}
	
	public void recalculateActiveLighting() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = -renderDistance; i < renderDistance; i++) {
					for (int k = -renderDistance; k < renderDistance; k++) {
						int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
						int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
						Chunk c = getChunk(cx, cz);
						if(c == null)
							continue;
						c.remeshNo(-1);
					}
				}
			}
		}).start();
	}
	
	public void queChunk(int cx, int cz) {
		ungeneratedChunks.add(new Tuple<Integer, Integer>(cx, cz));
	}

	public void cleanup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				GameRegistry.preSaveEvent();
				saveChunks();
				GameRegistry.postSaveEvent();
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
			Region r = chunks.get(regionPosX, regionPosZ);
			if (r != null) 
				r.setChunk(x, z, c);
			return;
		}
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
			return 0;
		x%=Chunk.x;
		z%=Chunk.z;
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
		int rx = (int)x;
		int rz = (int)z;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlock((int)x,(int)y, (int)z, rx, rz, block);
	}
	
	public void setLightLevel(float x, float y, float z, byte level) {
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
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setLightLevel((int)x,(int)y, (int)z, level);
	}
	
	public byte getLightLevel(float x, float y, float z) {
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
			return 0;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getLightLevel((int)x, (int)y, (int)z);
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
	
	public void setLightLevelBIAS(float x, float y, float z, byte level) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setLightLevel(x, y, z, level);
	}
	
	public byte getLightLevelBIAS(float x, float y, float z) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		return getLightLevel(x, y, z);
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

	private void queSave(MultiKeyMap<Integer, Region> rgs) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MapIterator<MultiKey<? extends Integer>, Region> rg = rgs.mapIterator();
				while (rg.hasNext()) {
					MultiKey<? extends Integer> r = rg.next();
					Region rgg = rgs.get(r.getKey(0), r.getKey(1));
					if (rgg != null) {
						StringBuilder b = new StringBuilder();
						b.append("Saving Region ");
						b.append(rgg);
						b.append(" at pos: {");
						b.append(r.getKey(0));
						b.append(",");
						b.append(r.getKey(1));
						b.append("}");
						System.out.println(b);
						rgg.saveRegion(worldLocation, true);
						rgg = null;
						rg.remove();
					}
				}
			}
		}).start();
	}

	public NoiseFunction getNf() {
		return nf;
	}

	public void setNf(NoiseFunction nf) {
		this.nf = nf;
	}
	
}
