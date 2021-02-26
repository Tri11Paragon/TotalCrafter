package com.brett.voxel.world.chunk;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.joml.Matrix4f;

import com.brett.cameras.Camera;
import com.brett.renderer.Loader;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.LevelLoader;
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
	// this actually might not be the best, especially with integers truncating towards 0
	// however this is likely the best way without doing a bunch of crazy transformations or data structures.
	private volatile MultiKeyMap<Integer, Region> regions = new MultiKeyMap<Integer, Region>();
	// ungenerated block data (stored in chunks)
	private volatile MultiKeyMap<Integer, NulChunk> ungenChunkData = new MultiKeyMap<Integer, NulChunk>();
	private volatile MultiKeyMap<Integer, Region> chunksCopy = null;
	// ungenerated chunks
	private volatile MultiKeyMap<Integer, Long> ungeneratedChunks = new MultiKeyMap<Integer, Long>();
	private volatile List<int[]> unsentChunkRequests = new ArrayList<int[]>();
	
	protected Camera cam;
	private Loader loader;
	private WorldGenerator gen;
	private VoxelWorld world;
	// prevents unloading while we are still trying to determin what needs to be unloaded.
	private volatile boolean unloading = false;

	public ChunkStore(Camera cam, Loader loader, VoxelWorld world) {
		this.cam = cam;
		this.loader = loader;
		this.world = world;
		init();
	}
	
	public void init() {
		// make sure the files and folders exist
		dimLocation = worldLocation + "DIM";
		new File(dimLocation).mkdirs();
		new File(worldLocation + "tile").mkdirs();
		new File(worldLocation + "ents").mkdirs();
		// load level data
		LevelLoader.loadLevelData(worldLocation);
		this.gen = new WorldGenerator(world);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// this generates chunks on the local client
				while(VoxelScreenManager.isOpen && !VoxelWorld.isRemote) {
					long start = System.currentTimeMillis();
					MapIterator<MultiKey<? extends Integer>, Long> it = ungeneratedChunks.mapIterator();
					try {
						while (it.hasNext()) {
							// get the chunk that needs to be generated
							MultiKey<? extends Integer> mk = it.next();
							// generate it
							Chunk c = generateChunk(mk.getKey(0), mk.getKey(1));
							// remove it from the ungen chunks map
							if (c != null) {
								ungeneratedChunks.removeMultiKey(mk.getKey(0), mk.getKey(1));
							}
						}
					} catch (ConcurrentModificationException e) {}
					// prevent running too quick
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
						// only run once per 20 seconds
						Thread.sleep(20*1000);
					} catch (InterruptedException e) {}
					
					// put into the world any unlocked chunks into loaded chunks
					if (ungenChunkData.size() > 0 && !VoxelWorld.isRemote) {
						MapIterator<MultiKey<? extends Integer>, NulChunk> itr = ungenChunkData.mapIterator();
						while (itr.hasNext()) {
							MultiKey<? extends Integer> keys = itr.next();
							// get unloaded chunk data
							NulChunk chnk = ungenChunkData.get(keys.getKey(0), keys.getKey(1));
							// get the chunk
							Chunk c = getChunk(keys.getKey(0), keys.getKey(1));
							if (c == null || chnk == null)
								return;
							// integrate them
							chnk.integrate(c.getBlocks());
							// set their blocks
							c.setBlocks(chnk.getBlocks());
							// remove it from the map
							ungenChunkData.removeMultiKey(keys.getKey(0), keys.getKey(1));
						}
					}
					
					if (chunksCopy == null) {
						try {
							unloading = true;
							chunksCopy = new MultiKeyMap<Integer, Region>();
							// make the unload size double the size of the render distance for good padding
							// yes I know i should use some kind of static variable instead of *2
							// but its only ran once per 20 seconds!
							/*
							 * What the chunk-copy system does is every 20 or so seconds it loops through all the visible chunks and puts them into a copy map
							 * the original map gets purged and then this map gets copied into the original map.
							 * 
							 * That removes all unloaded chunks and keep the loaded ones!
							 * Im sure there is a better way? I thought this was a neat idea so
							 * I haven't really bothered trying to innovate it.
							 */
							for (int i = -(renderDistance*2); i < (renderDistance*2); i++) {
								for (int k = -(renderDistance*2); k < (renderDistance*2); k++) {
									int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
									int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
									int xoff = 0, zoff = 0;
									// adjust for negatives
									if (cx < 0)
										xoff = -1;
									if (cz < 0)
										zoff = -1;
									int rx = cx / Region.x + xoff;
									int rz = cz / Region.z + zoff;
									try {
										// any chunks in the players view should be copied and kept loaded.
										Region r = regions.get(rx, rz);
										if (r != null) {
											chunksCopy.put(rx, rz, r);
										}
									} catch (Exception e) {
										System.err.println("Broken Game! (Region unloaded??)");
										System.err.println(e.getCause());
									}
								}
							}
							unloading = false;
						} catch (NullPointerException e) {System.err.println("Somehow something that isn't null is null. Possible race condition detected!");}
					}
					//System.gc();
				}
			}
		}).start();
	}
	
	/**
	 * remesh all active chunks
	 */
	public void remeshGlobal() {
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				Chunk c = getChunk(i, k);
				if (c != null)
					c.remesh();
			}
		}
	}

	/**
	 * generates a chunk and returns it.
	 */
	public Chunk generateChunk(int x, int z) {
		if (VoxelWorld.isRemote)
			return null;
		// get the region pos
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		// adjust for negatives
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		// see if the region exists its called chunk because it was created before regions.
		if (regions.containsKey(regionPosX, regionPosZ)) {
			// if the region is null generated ungenerated chunks
			if (regions.get(regionPosX, regionPosZ) == null)
				return genChunkUngen(x,z);
			else {
				// try to get the chunk at this pos
				try {
					Chunk c = regions.get(regionPosX, regionPosZ).getChunk(x, z);
					// if its null generate it
					if (c == null) {
						c = genChunkUngen(x,z);
						// add it to map and return.
						regions.get(regionPosX, regionPosZ).setChunk(x, z, c);
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
			// load the region
			Region r = Region.loadRegion(loader, world, regionPosX, regionPosZ, worldLocation);
			// put it into the map
			regions.put(regionPosX, regionPosZ, r);
			// get the chunk
			Chunk c = r.getChunk(x, z);
			if (c == null) {
				// if its null generate and set.
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
		// create a new chunk while generated its blocks
		Chunk c = new Chunk(loader,world, gen.getChunkBlocks(x, z), x, z);
		// get the ungenerated blocks
		NulChunk nc = ungenChunkData.get(x, z);
		if (nc != null) {
			// integrate them
			c.setBlocks(nc.integrate(c.getBlocks()));
			ungenChunkData.removeMultiKey(x, z);
		}
		// return the newly generated chunk.
		return c;
	}

	/**
	 * saves all the regions in the world.
	 */
	public void saveChunks() {
		if (VoxelWorld.isRemote)
			return;
		System.out.println("Saving World");
		MapIterator<MultiKey<? extends Integer>, Region> regionIt = regions.mapIterator();
		// loop all the regions and save them.
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
	public void renderChunks(VoxelShader shader, Matrix4f project) {
		if (chunksCopy != null && !unloading) {
			// this is the other half of the chunk-copy system
			MapIterator<MultiKey<? extends Integer>, Region> rg = chunksCopy.mapIterator();
			// remove all the regions that are still loaded
			while (rg.hasNext()) {
				try {
					MultiKey<? extends Integer> r = rg.next();
					regions.remove(r);
				} catch (Exception e) {}
			}
			// copy map
			MultiKeyMap<Integer, Region> rgs = new MultiKeyMap<Integer, Region>();
			// put all the not loaded regions into the copy map
			rgs.putAll(regions);
			// save them
			queueSave(rgs);
			// clear the original map
			regions.clear();
			// put the loaded regions into the map
			regions.putAll(chunksCopy);
			// debug that will never be removed.
			System.out.println("Chunking size: " + regions.size());
			// null the memory taken by chunks copy as we don't need it.
			chunksCopy = null;
			// normally now would be a good time to call the GC
			// but with the settings I have it on right now it should be fine
			// and calling the GC now would cause a lag spike.
		}
		// send chunk requests
		if (VoxelWorld.isRemote && unsentChunkRequests.size() > 0) {
			long current = System.currentTimeMillis();
			// make sure we only send once per 10 milliseconds
			if (current - last > 10) {
				// get the unsent chunk
				int[] cc = unsentChunkRequests.get(0);
				// send it
				VoxelWorld.localClient.sendChunkRequest(cc[0], cc[1]);
				// remove
				unsentChunkRequests.remove(0);
				last = current;
			}
		}
		//view.identity();
		shader.loadProjectionMatrix(project);
		shader.loadViewMatrix(Maths.createViewMatrixROT(cam));
		//project.mul(Maths.createViewMatrixROT(cam), view);
		//shader.loadViewMatrix(Maths.createViewMatrixROT(cam));
		// render all the chunks in the players view distance
		for (int i = -renderDistance; i <= renderDistance; i++) {
			for (int k = -renderDistance; k <= renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				//float fx = cam.getPosition().x + (i*Chunk.x);
				//float fz = cam.getPosition().z + (k * Chunk.z);
				//if (!cam.cubeInFrustum(fx, 0, fz, fx + Chunk.x, Chunk.y, fz + Chunk.z))
					//continue;
				// if the chunk is null then que it for generation.
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queueChunk(cx, cz);
					continue;
				}
				c.render(shader, view, i, k);
			}
		}
		// render all the transparent texutres in a chunk.
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
					// que it if its still null
					queueChunk(cx, cz);
					continue;
				}
				c.renderSpecial(shader, view, i, k);
			}
		}
	}
	
	/**
	 * update all the chunks in the players view distance
	 */
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
				// do a fixed update.
				c.fixedUpdate();
			}
		}
	}
	
	/**
	 * currently unused. also pointless call.
	 */
	@Deprecated
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
	
	/**
	 * queues a chunk for generation
	 */
	public void queueChunk(int cx, int cz) {
		// if we are connected to the server
		if (VoxelWorld.isRemote) {
			// then try and put it in the unsent chunks map
			if (!ungeneratedChunks.containsKey(cx, cz)) {
				ungeneratedChunks.put(cx, cz, System.currentTimeMillis());
				unsentChunkRequests.add(new int[] {cx, cz});
				//VoxelWorld.localClient.sendChunkRequest(cx, cz);
			} else {
				// if the chunk exists then return
				if (getChunk(cx, cz) != null)
					return;
				try {
					// was going to do something with sending the chunk request again
					if (System.currentTimeMillis() - ungeneratedChunks.get(cx, cz) > 50000) {
						VoxelWorld.localClient.sendChunkRequest(cx, cz);
					}
				} catch (Exception e) {}
			}
			return;
		}
		try {
			// add the chunk pos to generate pos if it hasn't already been qued
			if (!ungeneratedChunks.containsKey(cx, cz))
				ungeneratedChunks.put(cx, cz, 0l);
		} catch (ConcurrentModificationException e) {
			queueChunk(cx, cz);
		}
	}
	
	/**
	 * inserts a chunk from the server
	 */
	public void insertChunk(Chunk c) {
		int x = c.getX();
		int z = c.getZ();
		
		// remove it form the ungen chunks list
		if (ungeneratedChunks.containsKey(x, z))
			ungeneratedChunks.removeMultiKey(x, z);
		
		// get the region pos
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		// add it to the region if it exists
		if (regions.containsKey(regionPosX, regionPosZ)) {
			Region r = regions.get(regionPosX, regionPosZ);
			if (r != null) 
				r.setChunk(x, z, c);
			return;
		}
		// create a new region
		regions.put(regionPosX, regionPosZ, new Region(regionPosX, regionPosZ));
		// set a chunk in it
		regions.get(regionPosX, regionPosZ).setChunk(x, z, c);
	}

	/**
	 * save all the chunks
	 */
	public void cleanup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				saveChunks();
			}
		}).start();
	}

	/**
	 * sets a chunk at a pos
	 */
	public void setChunk(Chunk c, int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		// adjust for negatives
		// happens due to integer truncation.
		// eg you can't have negative 0
		// so like -14 / 32 = −0.4375 which when converted to integer would be -0 which is 0
		// so thats what this avoids by converting negative 0 into -1
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (regions.containsKey(regionPosX, regionPosZ)) {
			Region r = regions.get(regionPosX, regionPosZ);
			if (r != null) 
				r.setChunk(x, z, c);
			return;
		}
		// create the region and set the chunk
		regions.put(regionPosX, regionPosZ, new Region(regionPosX, regionPosZ));
		regions.get(regionPosX, regionPosZ).setChunk(x, z, c);
	}
	
	/**
	 * Note this is in chunk pos and not world pos.
	 * returns the chunk or null
	 */
	public Chunk getChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		// adjust for negatives
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		try {
			// get the region
			if (regions.containsKey(regionPosX, regionPosZ)) {
				Region r = regions.get(regionPosX, regionPosZ);
				// return the chunk or null
				if (r == null)
					return null;
				return r.getChunk(x, z);
			}else
				return null;
		} catch (NullPointerException e) {return null;}
	}
	
	/**
	 * gets a block from a pos
	 */
	public short getBlock(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0,zoff = 0;
		// adjust for negatives
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		// get the chunk
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
	
	/*
	 * many of the functions below are very similar to the ones above
	 */	
	
	/**
	 * get the block state for a pos
	 */
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
	
	/**
	 * return the block collision type for a pos
	 */
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
	
	/**
	 * set the block without updating it on the server
	 */
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
			// add to a null chunk
			NulChunk nc = ungenChunkData.get(cxpos, czpos);
			if (nc == null)
				nc = new NulChunk(world);
			// get position in the chunk
			int rx = (int)x;
			int rz = (int)z;
			x%=Chunk.x;
			z%=Chunk.z;
			if (x < 0)
				x = biasNegative(x, -Chunk.x);
			if (z < 0)
				z = biasNegative(z, -Chunk.z);
			// set the block in the null chunk (ungenerated chunk)
			nc.setBlock((int)x,(int)y, (int)z, rx, rz, block);
			return;
		}
		// get the position in the chunk
		int rx = (int)x;
		int rz = (int)z;
		x%=Chunk.x;
		z%=Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		// set it an remesh
		c.setBlockServer((int)x,(int)y, (int)z, rx, rz, block);
		c.remesh();
	}
	
	/**
	 * set a block for a position.
	 */
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
	
	/**
	 * set block state at a pos
	 */
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
	
	/**
	 * bias the set block state for negatives, this is only used for block function calls
	 */
	public void setBlockStateBIAS(float x, float y, float z, byte state) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlockState(x, y, z, state);
	}
	
	/**
	 * sets the raw light level for a position
	 */
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
	
	/**
	 * set the torch light level for a pos
	 */
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
	
	/**
	 * set the sun light level for a pos
	 */
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
	
	/**
	 * returns the light level for a pos
	 */
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
	
	/**
	 * returns the sun level for a pos
	 */
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
	
	/**
	 * returns the torch light level for a pos
	 */
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
	
	/**
	 * sets the block with a bias on the block pos
	 */
	public void setBlockBIAS(float x, float y, float z, short block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	/**
	 * sets the block with a bias on the block pos
	 */
	public void setBlockBIAS(float x, float y, float z, int block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	/**
	 * gets the block with a bias on the negative
	 */
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
	
	/**
	 * sets the light level with a bias
	 */
	public void setLightLevelBIAS(float x, float y, float z, byte level) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setLightLevel(x, y, z, level);
	}
	
	/**
	 * gets the light level with a bias on the negative
	 */
	public byte getLightLevelBIAS(float x, float y, float z) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		return getLightLevel(x, y, z);
	}
	
	/**
	 * sets a block at a pos
	 */
	public void setBlock(float x, float y, float z, int block) {
		setBlock(x, y, z, (short)block);
	}
	
	/**
	 * does some math magic with stuff because yes
	 */
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

	/**
	 * Queue the list of regions for saving.
	 */
	private void queueSave(MultiKeyMap<Integer, Region> rgs) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MapIterator<MultiKey<? extends Integer>, Region> rg = rgs.mapIterator();
				// loop and save the region
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
						// tell the user
						System.out.println(b);
						// save the region
						rgg.saveRegion(worldLocation, true);
						rgg = null;
						rg.remove();
					}
				}
			}
		}).start();
	}
	
}
