package com.brett.voxel.world.chunk;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.lwjgl.util.vector.Matrix4f;

import com.brett.cameras.Camera;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.Region;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.generators.WorldGenerator;

/**
 *
 * @author brett
 * @date Feb. 19, 2020
 */

public class ChunkStore implements IChunkProvider {

	public static int renderDistance = 12;
	public static String worldLocation = "worlds/w1/";
	public static String dimLocation = worldLocation + "DIM";

	// actually the best way of storing chunk data.
	// however will need to add a way of moving between active and non active
	// chunks.
	// this actually might not be the best, espcially with integers truncating towards 0
	// however this is likely the best way without doing a bunch of crazy transformations or data structures.
	private volatile MultiKeyMap<Integer, Region> chunks = new MultiKeyMap<Integer, Region>();
	private volatile MultiKeyMap<Integer, NulChunk> ungenChunkData = new MultiKeyMap<Integer, NulChunk>();
	private volatile MultiKeyMap<Integer, Region> chunksCopy = null;
	private volatile MultiKeyMap<Integer, Long> ungeneratedChunks = new MultiKeyMap<Integer, Long>();
	private volatile List<int[]> unsentChunkRequests = new ArrayList<int[]>();
	
	protected Camera cam;
	private Loader loader;
	private WorldGenerator gen;
	private VoxelWorld world;

	public ChunkStore(Camera cam, Loader loader, VoxelWorld world) {
		this.cam = cam;
		this.loader = loader;
		this.world = world;
		init();
	}
	
	public void init() {
		dimLocation = worldLocation + "DIM";
		new File(dimLocation).mkdirs();
		new File(worldLocation + "tile").mkdirs();
		new File(worldLocation + "ents").mkdirs();
		LevelLoader.loadLevelData(worldLocation);
		this.gen = new WorldGenerator(world);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(VoxelScreenManager.isOpen && !VoxelWorld.isRemote) {
					long start = System.currentTimeMillis();
					MapIterator<MultiKey<? extends Integer>, Long> it = ungeneratedChunks.mapIterator();
					try {
						while (it.hasNext()) {
							MultiKey<? extends Integer> mk = it.next();
							Chunk c = generateChunk(mk.getKey(0), mk.getKey(1));
							if (c != null) {
								ungeneratedChunks.removeMultiKey(mk.getKey(0), mk.getKey(1));
							}
						}
					} catch (ConcurrentModificationException e) {}
					long end = System.currentTimeMillis();
					long delta = Maths.preventNegs(32 - (end - start));
					try {
						Thread.sleep(delta);
					} catch (InterruptedException e) {}
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
					
					if (ungenChunkData.size() > 0 && !VoxelWorld.isRemote) {
						MapIterator<MultiKey<? extends Integer>, NulChunk> itr = ungenChunkData.mapIterator();
						while (itr.hasNext()) {
							MultiKey<? extends Integer> keys = itr.next();
							NulChunk chnk = ungenChunkData.get(keys.getKey(0), keys.getKey(1));
							Chunk c = getChunk(keys.getKey(0), keys.getKey(1));
							if (c == null || chnk == null)
								return;
							chnk.integrate(c.getBlocks());
							c.setBlocks(chnk.getBlocks());
							ungenChunkData.removeMultiKey(keys.getKey(0), keys.getKey(1));
						}
					}
					
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
									try {
										Region r = chunks.get(rx, rz);
										if (r != null) {
											chunksCopy.put(rx, rz, r);
										}
									} catch (Exception e) {
										System.err.println("Broken (Region unloaded??)");
										System.err.println(e.getCause());
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
	
	public void remeshGlobal() {
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				Chunk c = getChunk(i, k);
				if (c != null)
					c.remesh();
			}
		}
	}

	public Chunk generateChunk(int x, int z) {
		if (VoxelWorld.isRemote)
			return null;
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			if (chunks.get(regionPosX, regionPosZ) == null)
				return genChunkUngen(x,z);
			else {
				try {
					Chunk c = chunks.get(regionPosX, regionPosZ).getChunk(x, z);
					if (c == null) {
						c = genChunkUngen(x,z);
						chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
						return c;
					} else {
						return c;
					}
				} catch (NullPointerException e) {
					System.out.println("Hey we got a broken pipe here!");
					return genChunkUngen(x,z);
				}
			}
		} else {
			Region r = Region.loadRegion(loader, world, regionPosX, regionPosZ, worldLocation);
			chunks.put(regionPosX, regionPosZ, r);
			Chunk c = r.getChunk(x, z);
			if (c == null) {
				c = genChunkUngen(x,z);
				r.setChunk(x, z, c);
				return c;
			}else
				return c;
		}
	}
	
	/**
	 * Generates a chunk while including all the blocks that have been placed while it was null.
	 */
	private Chunk genChunkUngen(int x, int z) {
		Chunk c = new Chunk(loader,world, gen.getChunkBlocks(x, z), x, z);
		NulChunk nc = ungenChunkData.get(x, z);
		if (nc != null) {
			c.setBlocks(nc.integrate(c.getBlocks()));
			ungenChunkData.removeMultiKey(x, z);
		}
		return c;
	}

	public void saveChunks() {
		if (VoxelWorld.isRemote)
			return;
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
	
	Matrix4f view = new Matrix4f();
	long last = 0;
	private boolean lastTransparent = true;
	public void renderChunks(VoxelShader shader, Matrix4f project) {
		if (chunksCopy != null) {
			MapIterator<MultiKey<? extends Integer>, Region> rg = chunksCopy.mapIterator();
			while (rg.hasNext()) {
				try {
					MultiKey<? extends Integer> r = rg.next();
					chunks.remove(r);
				} catch (Exception e) {}
			}
			MultiKeyMap<Integer, Region> rgs = new MultiKeyMap<Integer, Region>();
			rgs.putAll(chunks);
			queSave(rgs);
			chunks.clear();
			chunks.putAll(chunksCopy);
			System.out.println("Chunking size: " + chunks.size());
			chunksCopy = null;
		}
		if (VoxelWorld.isRemote && unsentChunkRequests.size() > 0) {
			long current = System.currentTimeMillis();
			if (current - last > 20) {
				int[] cc = unsentChunkRequests.get(0);
				VoxelWorld.localClient.sendChunkRequest(cc[0], cc[1]);
				unsentChunkRequests.remove(0);
				last = current;
			}
		}
		view.setIdentity();
		Matrix4f.mul(project, Maths.createViewMatrixROT(cam), view);
		//shader.loadViewMatrix(Maths.createViewMatrixROT(cam));
		for (int i = -renderDistance; i <= renderDistance; i++) {
			for (int k = -renderDistance; k <= renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				//float fx = cam.getPosition().x + (i*Chunk.x);
				//float fz = cam.getPosition().z + (k * Chunk.z);
				//if (!cam.cubeInFrustum(fx, 0, fz, fx + Chunk.x, Chunk.y, fz + Chunk.z))
					//continue;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				c.render(shader, view, i, k);
			}
		}
		for (int i = -renderDistance; i <= renderDistance; i++) {
			for (int k = -renderDistance; k <= renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				//float fx = cam.getPosition().x + (i*Chunk.x);
				//float fz = cam.getPosition().z + (k * Chunk.z);
				//if (!cam.cubeInFrustum(fx, 0, fz, fx + Chunk.x, Chunk.y, fz + Chunk.z))
					//continue;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				if (distance(cx, cz) > 32) {
					if (lastTransparent) {
						MasterRenderer.disableTransparentcy();
						lastTransparent = false;
					}
				} else {
					if (!lastTransparent) {
						MasterRenderer.enableTransparentcy();
						lastTransparent = true;
					}
				}
				c.renderSpecial(shader, view, i, k);
			}
		}
	}
	
	private double distance(int x, int z) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));
	}
	
	public void updateChunks() {
		if (VoxelWorld.isRemote)
			return;
		for (int i = -renderDistance; i <= renderDistance; i++) {
			for (int k = -renderDistance; k <= renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					continue;
				}
				c.fixedUpdate();
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
						c.remeshNo();
					}
				}
			}
		}).start();
	}
	
	public void queChunk(int cx, int cz) {
		if (VoxelWorld.isRemote) {
			if (!ungeneratedChunks.containsKey(cx, cz)) {
				ungeneratedChunks.put(cx, cz, System.currentTimeMillis());
				unsentChunkRequests.add(new int[] {cx, cz});
				//VoxelWorld.localClient.sendChunkRequest(cx, cz);
			} else {
				if (getChunk(cx, cz) != null)
					return;
				try {
				if (System.currentTimeMillis() - ungeneratedChunks.get(cx, cz) > 5000) {
					//VoxelWorld.localClient.sendChunkRequest(cx, cz);
				}
				} catch (Exception e) {}
			}
			return;
		}
		try {
			if (!ungeneratedChunks.containsKey(cx, cz))
				ungeneratedChunks.put(cx, cz, 0l);
		} catch (ConcurrentModificationException e) {
			queChunk(cx, cz);
		}
	}
	
	public void insertChunk(Chunk c) {
		int x = c.getX();
		int z = c.getZ();
		
		if (ungeneratedChunks.containsKey(x, z))
			ungeneratedChunks.removeMultiKey(x, z);
		
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
		try {
			if (chunks.containsKey(regionPosX, regionPosZ)) {
				Region r = chunks.get(regionPosX, regionPosZ);
				if (r == null)
					return null;
				return r.getChunk(x, z);
			}else
				return null;
		} catch (NullPointerException e) {return null;}
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
	
	public byte getBlockState(float x, float y, float z) {
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
		return c.getBlockState((int)x, (int)y, (int)z);
	}
	
	public COLLISIONTYPE getBlockCollision(float x, float y, float z) {
		if (y < 0)
			return COLLISIONTYPE.NOT;
		if (y > Chunk.y)
			return COLLISIONTYPE.NOT;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x/Chunk.x + xoff), (int) (z/Chunk.z + zoff));
		if (c == null)
			return COLLISIONTYPE.SOLID;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return Block.blocks.get(c.getBlock((int)x, (int)y, (int)z)).getCollisiontype();
	}
	
	public void setBlockServer(float x, float y, float z, short block) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cxpos = ((int)(x/(float)Chunk.x) + xoff), czpos = ((int)(z/(float)Chunk.z) + zoff);
		Chunk c = getChunk(cxpos, czpos);
		if (c == null) {
			NulChunk nc = ungenChunkData.get(cxpos, czpos);
			if (nc == null)
				nc = new NulChunk(world);
			int rx = (int)x;
			int rz = (int)z;
			x%=Chunk.x;
			z%=Chunk.z;
			if (x < 0)
				x = biasNegative(x, -Chunk.x);
			if (z < 0)
				z = biasNegative(z, -Chunk.z);
			nc.setBlock((int)x,(int)y, (int)z, rx, rz, block);
			return;
		}
		int rx = (int)x;
		int rz = (int)z;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlockServer((int)x,(int)y, (int)z, rx, rz, block);
		c.remesh();
	}
	
	public void setBlock(float x, float y, float z, short block) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		//if (VoxelWorld.isRemote)
		//	VoxelWorld.localClient.updateBlock((int)x, (int)y, (int)z, block);
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cxpos = ((int)(x/(float)Chunk.x) + xoff), czpos = ((int)(z/(float)Chunk.z) + zoff);
		Chunk c = getChunk(cxpos, czpos);
		if (c == null) {
			NulChunk nc = ungenChunkData.get(cxpos, czpos);
			if (nc == null)
				nc = new NulChunk(world);
			int rx = (int)x;
			int rz = (int)z;
			x%=Chunk.x;
			z%=Chunk.z;
			if (x < 0)
				x = biasNegative(x, -Chunk.x);
			if (z < 0)
				z = biasNegative(z, -Chunk.z);
			nc.setBlock((int)x,(int)y, (int)z, rx, rz, block);
			return;
		}
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
	
	public void setBlockState(float x, float y, float z, byte state) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cxpos = ((int)(x/(float)Chunk.x) + xoff), czpos = ((int)(z/(float)Chunk.z) + zoff);
		Chunk c = getChunk(cxpos, czpos);
		if (c == null) {
			return;
		}
		int rx = (int)x;
		int rz = (int)z;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlockState((int)x,(int)y, (int)z, rx, rz, state);
	}
	
	public void setBlockStateBIAS(float x, float y, float z, byte state) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlockState(x, y, z, state);
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
	
	public void setLightLevelTorch(float x, float y, float z, int level) {
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
		c.setLightLevelTorch((int)x,(int)y, (int)z, level);
	}
	
	public void setLightLevelSun(float x, float y, float z, int level) {
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
		c.setLightLevelSun((int)x,(int)y, (int)z, level);
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
	
	public byte getLightLevelSun(float x, float y, float z) {
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
		return c.getLightLevelSun((int)x, (int)y, (int)z);
	}
	
	public byte getLightLevelTorch(float x, float y, float z) {
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
		return c.getLightLevelTorch((int)x, (int)y, (int)z);
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
	
}
