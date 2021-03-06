package com.brett.voxel.networking.server;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.joml.Matrix4f;

import com.brett.datatypes.Tuple;
import com.brett.opencl.CLBuffer;
import com.brett.opencl.CLInit;
import com.brett.opencl.StaticCLKernel;
import com.brett.opencl.StaticCLProgram;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.Region;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.chunk.IChunkProvider;
import com.brett.voxel.world.chunk.NulChunk;
import com.brett.voxel.world.generators.WorldGenerator;

/**
 *
 * @author brett
 * @date Jun. 1, 2020
 * 
 *       IMPORTANT NOTE: im not re documenting the chunk stuff. if you want
 *       documentation goto the chunkstore class. its the same code.
 */

public class ServerWorld extends IWorldProvider implements IChunkProvider {

	private static final long serialVersionUID = -7269680346286023585L;

	public static int renderDistance = 12;
	public static String worldLocation = "worlds/server/";
	public static String dimLocation = worldLocation + "DIM";

	private volatile MultiKeyMap<Integer, Region> chunks = new MultiKeyMap<Integer, Region>();
	private volatile MultiKeyMap<Integer, NulChunk> ungenChunkData = new MultiKeyMap<Integer, NulChunk>();
	@SuppressWarnings("unused")
	private volatile MultiKeyMap<Integer, Region> chunksCopy = null;
	private volatile MultiKeyMap<Integer, ConnectedClient> ungeneratedChunks = new MultiKeyMap<Integer, ConnectedClient>();
	private volatile List<Tuple<Chunk, ConnectedClient>> unsentChunks = new ArrayList<Tuple<Chunk, ConnectedClient>>();
	private WorldGenerator gen;
	private Loader loader;
	
	public static StaticCLProgram program;
	public static StaticCLKernel kernel;
	public static CLBuffer ref;
	public static CLBuffer cpos;
	public static int[] dstArray;
	public static CLBuffer out;

	public ServerWorld() {
		super.chunk = this;
		this.gen = new WorldGenerator(this);
		loader = new Loader();
		new File(worldLocation).mkdirs();
		new File(dimLocation).mkdirs();
		new File(worldLocation + "tile").mkdirs();
		new File(worldLocation + "ents").mkdirs();
		new File(worldLocation + "players").mkdirs();
		GameRegistry.init(loader);

		new Thread(new Runnable() {
			@Override
			public void run() {
				CLInit.init();
				dstArray = new int[16 * 16 * 128];
				// setup arrays
				CLBuffer p = StaticCLKernel.createBufferInt(gen.lf.p);
				ref = StaticCLKernel.createBufferInt(new int[16 * 16]);
				cpos = StaticCLKernel.createBufferInt(new int[] {1, 0});
				out = StaticCLKernel.createBufferInt(dstArray);
				program = new StaticCLProgram("worldgen.cl");
				kernel = new StaticCLKernel(program, "worldgen", 16 * 16 * 128, p, ref, cpos, out);
				kernel.writeIntBuffer(p, gen.lf.p.length);
				//kernel.execute(out);
				//System.out.println("Out: " + Arrays.toString(dstArray));
				
				while (VoxelScreenManager.isOpen) {
					long start = System.currentTimeMillis();
					MapIterator<MultiKey<? extends Integer>, ConnectedClient> it = ungeneratedChunks.mapIterator();
					try {
						//if (ungeneratedChunks.size() > 0)
							//System.out.println("Processing data; " + ungeneratedChunks.size());
						while (it.hasNext()) {
							MultiKey<? extends Integer> mk = it.next();
							Chunk c = generateChunk(mk.getKey(0), mk.getKey(1));
							if (c != null) {
								ConnectedClient cl = it.getValue();

								unsentChunks.add(new Tuple<Chunk, ConnectedClient>(c, cl));
								// Server.server.sendCompressedChunk(c, cl);
								ungeneratedChunks.removeMultiKey(mk.getKey(0), mk.getKey(1));
							}
						}
					} catch (ConcurrentModificationException e) {
					}
					long end = System.currentTimeMillis();
					long delta = Maths.preventNegs(32 - (end - start));
					long d = 0;
					long ls = System.nanoTime();
					while (ungenChunkData.size() < 10 && d < 2000000) {
						d += (System.nanoTime() - ls);
						Thread.yield();
					}
					//if (ungeneratedChunks.size() > 0)
						//System.out.println(d + " " + delta);
					// try {
					// Thread.sleep(delta);
					// } catch (InterruptedException e) {}
				}
				// two (2.172939) milliseconds on average to generate a chunk
				long time = 0;
				for (int i =0; i < WorldGenerator.timeToComplete.size(); i++) {
					time += WorldGenerator.timeToComplete.get(i);
				}
				long size = WorldGenerator.timeToComplete.size();
				if (size == 0)
					size = 1;
				long sized = (time / size);
				System.out.println("Time: " + sized);
				time = 0;
				for (int i =0; i < WorldGenerator.timeToComplete2.size(); i++) {
					time += WorldGenerator.timeToComplete2.get(i);
				}
				size = WorldGenerator.timeToComplete2.size();
				if (size == 0)
					size = 1;
				sized = (time / size);
				System.out.println("Time2: " + sized);
				
				kernel.cleanup();
				program.cleanup();
				CLInit.cleanup();
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				long last = 0;
				while (VoxelScreenManager.isOpen) {
					long end = System.currentTimeMillis();
					if (end - last > 10) {
						try {
							if (unsentChunks.size() > 0) {
								Tuple<Chunk, ConnectedClient> c = unsentChunks.get(0);
								Server.server.sendCompressedChunk(c.getX(), c.getY());
								unsentChunks.remove(0);
								last = end;
							}
						} catch (Exception e) {
						}
					} else {
						Thread.yield();
					}
				}
			}
		}).start();
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
				return genChunkUngen(x, z);
			else {
				try {
					Chunk c = chunks.get(regionPosX, regionPosZ).getChunk(x, z);
					if (c == null) {
						c = genChunkUngen(x, z);
						chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
						return c;
					} else {
						return c;
					}
				} catch (NullPointerException e) {
					System.out.println("Hey we got a broken pipe here!");
					return genChunkUngen(x, z);
				}
			}
		} else {
			Region r = Region.loadRegion(MasterRenderer.global_loader, this, regionPosX, regionPosZ, worldLocation);
			chunks.put(regionPosX, regionPosZ, r);
			Chunk c = r.getChunk(x, z);
			if (c == null) {
				c = genChunkUngen(x, z);
				r.setChunk(x, z, c);
				return c;
			} else
				return c;
		}
	}

	private Chunk genChunkUngen(int x, int z) {
		Chunk c = new Chunk(MasterRenderer.global_loader, this, gen.getChunkBlocks(x, z), x, z);
		NulChunk nc = ungenChunkData.get(x, z);
		if (nc != null) {
			c.setBlocks(nc.integrate(c.getBlocks()));
			ungenChunkData.removeMultiKey(x, z);
		}
		return c;
	}

	public void saveChunks() {
		PlayerSaver.savePlayers(Server.server.clients);
		System.out.println("Saving World");
		MapIterator<MultiKey<? extends Integer>, Region> regionIt = chunks.mapIterator();
		while (regionIt.hasNext()) {
			try {
				regionIt.next();
			} catch (ConcurrentModificationException e) {
				System.err.println(
						"Tried saving map while loading it. \nPlease wait for map to complete loading before exiting game.");
			}
			Region val = regionIt.getValue();
			if (val != null)
				val.saveRegion(worldLocation, false);
		}
		System.out.println("World Saved");
	}

	public void queChunk(int cx, int cz, ConnectedClient cl) {
		try {
			Chunk c = getChunk(cx, cz);
			if (c != null) {
				unsentChunks.add(new Tuple<Chunk, ConnectedClient>(c, cl));
				return;
			}
			if (!ungeneratedChunks.containsKey(cx, cz))
				ungeneratedChunks.put(cx, cz, cl);
		} catch (ConcurrentModificationException e) {
			queChunk(cx, cz, cl);
		}
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
			} else
				return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public short getBlock(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return 0;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getBlock((int) x, (int) y, (int) z);
	}

	public byte getBlockState(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return 0;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getBlockState((int) x, (int) y, (int) z);
	}

	public COLLISIONTYPE getBlockCollision(float x, float y, float z) {
		if (y < 0)
			return COLLISIONTYPE.NOT;
		if (y > Chunk.y)
			return COLLISIONTYPE.NOT;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return COLLISIONTYPE.SOLID;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return Block.blocks.get(c.getBlock((int) x, (int) y, (int) z)).getCollisiontype();
	}

	public void setBlock(float x, float y, float z, short block) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cxpos = ((int) (x / (float) Chunk.x) + xoff), czpos = ((int) (z / (float) Chunk.z) + zoff);
		Chunk c = getChunk(cxpos, czpos);
		if (c == null) {
			NulChunk nc = ungenChunkData.get(cxpos, czpos);
			if (nc == null)
				nc = new NulChunk(this);
			int rx = (int) x;
			int rz = (int) z;
			x %= Chunk.x;
			z %= Chunk.z;
			if (x < 0)
				x = biasNegative(x, -Chunk.x);
			if (z < 0)
				z = biasNegative(z, -Chunk.z);
			nc.setBlock((int) x, (int) y, (int) z, rx, rz, block);
			return;
		}
		int rx = (int) x;
		int rz = (int) z;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlock((int) x, (int) y, (int) z, rx, rz, block);
	}

	public void setBlockState(float x, float y, float z, byte state) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		int cxpos = ((int) (x / (float) Chunk.x) + xoff), czpos = ((int) (z / (float) Chunk.z) + zoff);
		Chunk c = getChunk(cxpos, czpos);
		if (c == null) {
			return;
		}
		int rx = (int) x;
		int rz = (int) z;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlockState((int) x, (int) y, (int) z, rx, rz, state);
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
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / (float) Chunk.x) + xoff, (int) (z / (float) Chunk.z) + zoff);
		if (c == null)
			return;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setLightLevel((int) x, (int) y, (int) z, level);
	}

	public void setLightLevelTorch(float x, float y, float z, int level) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / (float) Chunk.x) + xoff, (int) (z / (float) Chunk.z) + zoff);
		if (c == null)
			return;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setLightLevelTorch((int) x, (int) y, (int) z, level);
	}

	public void setLightLevelSun(float x, float y, float z, int level) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / (float) Chunk.x) + xoff, (int) (z / (float) Chunk.z) + zoff);
		if (c == null)
			return;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setLightLevelSun((int) x, (int) y, (int) z, level);
	}

	public byte getLightLevel(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return 0;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getLightLevel((int) x, (int) y, (int) z);
	}

	public byte getLightLevelSun(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return 0;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getLightLevelSun((int) x, (int) y, (int) z);
	}

	public byte getLightLevelTorch(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0, zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x / Chunk.x + xoff), (int) (z / Chunk.z + zoff));
		if (c == null)
			return 0;
		x %= Chunk.x;
		z %= Chunk.z;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getLightLevelTorch((int) x, (int) y, (int) z);
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
		setBlock(x, y, z, (short) block);
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

	@SuppressWarnings("unused")
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

	@Override
	public void renderChunks(VoxelShader shader, Matrix4f project) {
	}

	@Override
	public void updateChunks() {
	}

	@Override
	public void insertChunk(Chunk c) {
	}

	@Override
	public void setBlockServer(float x, float y, float z, short block) {
	}

}
