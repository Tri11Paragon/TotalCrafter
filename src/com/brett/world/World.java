package com.brett.world;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.brett.engine.data.collision.AxisAlignedBB;
import com.brett.engine.managers.ThreadPool;
import com.brett.networking.Client;
import com.brett.networking.ServerConnection;
import com.brett.utils.NdHashMap;
import com.brett.utils.Noise;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.Region;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.RenderMode;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
 * @author Brett
 * @date Jun. 28, 2020
 */

public class World {

	public static World world;

	public volatile NdHashMap<Integer, Region> regions = new NdHashMap<Integer, Region>();
	public volatile NdHashMap<Integer, Chunk> ungeneratedChunks = new NdHashMap<Integer, Chunk>();
	public volatile NdHashMap<Integer, List<Client>> playerRequestedChunks = new NdHashMap<Integer, List<Client>>();
	private volatile NdHashMap<Integer, Integer> lockedRegions = new NdHashMap<Integer, Integer>();
	public int threads = 1;
	public Noise noise1 = new Noise(694);
	public Noise noise2 = new Noise(733210811l + 11181013212l + 11111173287l + 105108108326051l);
	public String worldName;
	public String regionsLocation;
	public ExecutorService worldExecutor;
	public boolean isRemote = false;
	public boolean isServer = false;
	public ServerConnection serverConnection;
	
	public World(String worldName) {
		this.worldName = worldName;
		new File("worlds/" + this.worldName).mkdirs();
		regionsLocation = "worlds/" + this.worldName + "/regions/";
		new File(regionsLocation).mkdirs();
		
		threads = ThreadPool.reserveQuarterThreads() + ThreadPool.reserveQuarterThreads();
		worldExecutor = Executors.newFixedThreadPool(threads);
		World.world = this;
	}
	
	public World() {
		isRemote = false;
		isServer = true;
		this.worldName = "NULL";
		
		threads = ThreadPool.reserveQuarterThreads() + ThreadPool.reserveQuarterThreads();
		worldExecutor = Executors.newFixedThreadPool(threads);
		World.world = this;
	}
	
	public World(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
		this.worldName = "NULL";
		
		isRemote = true;
		isServer = false;
		
		threads = ThreadPool.reserveQuarterThreads() + ThreadPool.reserveQuarterThreads();
		worldExecutor = Executors.newFixedThreadPool(threads);
		World.world = this;
	}

	/**
	 * queue a chunk for generation in chunk space.
	 */
	public synchronized void queueChunk(int x, int y, int z) {
		if (ungeneratedChunks.containsKey(x, y, z)) {
			return;
		}
		Chunk c = new Chunk(this, new ShortBlockStorage(), new ByteBlockStorage(), x, y, z);
		ungeneratedChunks.set(x, y, z, c);
		if (world.isRemote) {
			serverConnection.sendChunkReq(x, y, z);
			return;
		}
		worldExecutor.submit(() -> {
			int rx = c.x_pos >> 3;
			int ry = c.y_pos >> 3;
			int rz = c.z_pos >> 3;
			Region r = regions.get(rx, ry, rz);
			if (r == null) {
				if (lockedRegions.containsKey(rx, ry, rz)) {
					while (lockedRegions.containsKey(rx, ry, rz)) {try{ Thread.sleep(1); } catch (Exception e) {}}
					r = regions.get(rx, ry, rz);
					if (r == null)
						System.err.println("we got an issue with a null region after loading :(");
				} else {
					lockedRegions.set(rx, ry, rz, 1);
					r = Region.load(regionsLocation, this, rx, ry, rz);
					regions.set(rx, ry, rz, r);
					lockedRegions.remove(rx, ry, rz);
				}
			}
			Chunk cr = r.getChunk(x, y, z);
			if (cr != null) {
				ungeneratedChunks.remove(x, y, z);
				return;
			}
			ShortBlockStorage blks = c.blocks;
			int cxw = x * 16;
			int cyw = y * 16;
			int czw = z * 16;
			for (int i = 0; i < 16; i++) {
				for (int k = 0; k < 16; k++) {
					int wx = cxw + i;
					int wz = czw + k;
					double nfxz = 0;
					if (cyw > -120)
						nfxz = noise1.noise(wx / 128d + noise2.nNoise(wx / 32d, 4 / 3492d, wz / 32d + noise1.noise(wx, wz), 8, 4d), wz / 128d) * 64 + 64;
					for (int j = 0; j < 16; j++) {
						int wy = cyw + j;

						if (wy > nfxz) {
							
						} else {

							double nf = noise1.noise(wx / 32d, wy / 32d, wz / 32d);
							if (nf > -Noise.RANGE_3D / 2) {
								if (wy < nfxz && wy > nfxz - 1)
									blks.setWorld(wx, wy, wz, Block.GRASS);
								else if (wy < nfxz - 1 && wy > (nfxz - 4))
									blks.setWorld(wx, wy, wz, Block.DIRT);
								else if (wy > -120)
									blks.setWorld(wx, wy, wz, Block.STONE);
								else
									blks.setWorld(wx, wy, wz, Block.BASALT);
							}
						}
					}
				}
			}
			r.setChunk(x, y, z, c);
			if (playerRequestedChunks.containsKey(x, y, z) && isServer) {
				List<Client> clients = playerRequestedChunks.get(x, y, z);
				for (int i = 0; i < clients.size(); i++) {
					clients.get(i).sendChunk(c);
				}
				playerRequestedChunks.remove(x, y, z);
			}
			ungeneratedChunks.remove(x, y, z);
		});
	}

	/**
	 * sets a block in block space.
	 */
	public void setBlock(int x, int y, int z, short id) {
		int cx = x >> 4;
		int cy = y >> 4;
		int cz = z >> 4;
		Chunk c = getChunk(cx, cy, cz);
		if (c == null) {
			if (ungeneratedChunks.containsKey(cx, cy, cz)) {
				c = ungeneratedChunks.get(cx, cy, cz);
			} else {
				c = new Chunk(this, new ShortBlockStorage(), new ByteBlockStorage(), cx, cy, cz);
				ungeneratedChunks.set(cx, cy, cz, c);
			}
		} else
			c.blocks.setWorld(x, y, z, id);
	}

	public Chunk meshAt(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return null;
		c.meshChunk();
		return c;
	}

	public Chunk meshAt(int x, int y, int z, Chunk co) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return null;
		if (co == c)
			return c;
		c.meshChunk();
		return c;
	}

	public void meshAround(int x, int y, int z) {
		int cx = x >> 4;
		int cy = y >> 4;
		int cz = z >> 4;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					int wx = cx + i;
					int wy = cy + j;
					int wz = cz + k;
					Region r = getRegion(wx, wy, wz);
					if (r != null) {
						Chunk c = r.getChunk(wx, wy, wz);
						if (c != null)
							c.meshChunk();
					}
				}
			}
		}
	}
	
	/**
	 * returns region based on chunk coords
	 */
	public Region getRegion(int x, int y, int z) {
		// convert chunk space into region space
		int rx = x >> 3;
		int ry = y >> 3;
		int rz = z >> 3;
		return regions.get(rx, ry, rz);
	}

	public RenderMode getRenderMode(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return GameRegistry.getBlock((short) 0).getRenderMode();
		short block = c.blocks.getWorld(x, y, z);
		return GameRegistry.getBlock(block).getRenderMode();
	}

	public RenderMode getRenderModeNull(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return null;
		short block = c.blocks.getWorld(x, y, z);
		Block b = Block.blocks.get(block);
		if (b == null)
			return null;
		return b.getRenderMode();
	}

	public short getBlock(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return c.blocks.getWorld(x, y, z);
	}

	public byte getLightLevel(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return c.lightLevel.getWorld(x, y, z);
	}

	public byte getSunLevel(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return (byte) (c.lightLevel.getWorld(x, y, z) >> 4);
	}

	public byte getBlockLevel(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return (byte) (c.lightLevel.getWorld(x, y, z) & 0xF);
	}

	public void setLightLevel(int x, int y, int z, byte level) {
		Chunk c = getChunkWorld(x, y, z);
		if (c != null)
			c.lightLevel.setWorld(x, y, z, level);
	}

	public void setSunLevel(int x, int y, int z, byte level) {
		Chunk c = getChunkWorld(x, y, z);
		if (c != null)
			c.lightLevel.setWorld(x, y, z, c.lightLevel.getWorld(x, y, z) & ((level << 4) | 0x0F));
	}

	public void setBlockLevel(int x, int y, int z, byte level) {
		Chunk c = getChunkWorld(x, y, z);
		if (c != null) {
			c.lightLevel.setWorld(x, y, z, (c.lightLevel.getWorld(x, y, z) & 0xF0) | (level & 0xF));
		}
	}

	public Block getBlockB(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return GameRegistry.getBlock(Block.AIR);
		return GameRegistry.getBlock(c.blocks.getWorld(x, y, z));
	}

	public List<AxisAlignedBB> getBoundsInRange(int nx, int ny, int nz, int px, int py, int pz) {
		ArrayList<AxisAlignedBB> lis = new ArrayList<AxisAlignedBB>();
		return getBoundsInRange(lis, nx, ny, nz, px, py, pz);
	}

	public List<AxisAlignedBB> getBoundsInRange(ArrayList<AxisAlignedBB> lis, int nx, int ny, int nz, int px, int py,
			int pz) {

		for (int i = nx; i <= px; i++) {
			for (int j = ny; j <= py; j++) {
				for (int k = nz; k <= pz; k++) {
					AxisAlignedBB bb = getBlockB(i, j, k).bbox;
					if (bb != null)
						lis.add(bb.translate(i, j, k));
				}
			}
		}

		return lis;
	}

	/*
	 * gets the chunk in world pos
	 */
	public Chunk getChunkWorld(int wx, int wy, int wz) {
		wx >>= 4;
		wy >>= 4;
		wz >>= 4;
		Chunk c = null;
		Region r = getRegion(wx, wy, wz);
		if (r != null)
			c = r.getChunk(wx, wy, wz);
		return c;
	}

	/**
	 * gets the chunk in chunk pos
	 */
	public Chunk getChunk(int x, int y, int z) {
		Chunk c = null;
		Region r = getRegion(x, y, z);
		if (r != null)
			c = r.getChunk(x, y, z);
		return c;
	}

	/**
	 * sets a chunk, do not call from main thread.
	 */
	public void setChunk(Chunk c) {
		Region r = getRegion(c.x_pos, c.y_pos, c.z_pos);
		if (r != null)
			r.setChunk(c.x_pos, c.y_pos, c.z_pos, c);
		else {
			int rx = c.x_pos >> 3;
			int ry = c.y_pos >> 3;
			int rz = c.z_pos >> 3;
			r = Region.load(regionsLocation, this, rx, ry, rz);
			Chunk rc = r.getChunk(c.x_pos, c.y_pos, c.z_pos);
			if (rc != null) {
				rc.blocks.integrate(c.blocks);
				rc.meshChunk();
			} else
				r.setChunk(c.x_pos, c.y_pos, c.z_pos, c);
			
			regions.set(rx, ry, rz, r);
		}
	}

	public void save() {
		System.out.println("Saving World");
		
		regions.iterate((NdHashMap<Integer, Region> rg, Integer x, Integer y, Integer z, Region r) -> {
			worldExecutor.execute(() -> {
				r.save();
			});
		});
		
		worldExecutor.shutdown();
	}

	public boolean isWorldGeneratorTerminated() {
		return worldExecutor.isTerminated();
	}
	
}
