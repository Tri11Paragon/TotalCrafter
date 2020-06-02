package com.brett.voxel.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.brett.renderer.Loader;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.lighting.LightingEngine;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Factory;

/**
*
* @author brett
* @date Mar. 4, 2020
* 
* Region class for saving and loading chunks
* TODO: use GZIP or make my own compression to compress world files.
* Each chunk has a size of about 64kb.
* Regions should have about 64mb.
* 
* Help may be on my github.
* if it is not and want help submit an issue.
* 
*/

public class Region {
	
	public static final int x = 32;
	public static final int z = 32;
	
	private int xpos;
	private int zpos;
	
	private MultiKeyMap<Integer, Chunk> chunks = new MultiKeyMap<Integer, Chunk>();
	
	public Region(int xpos, int zpos) {
		this.xpos = xpos;
		this.zpos = zpos;
	}

	/**
	 * loads region from hard drive at the given point.
	 * NOTE: X and Z are in region standardized coordinates.
	 * If you don't know what this means and have a world position,
	 * then take the world x and z, divide by your chunk size and then finally divide by
	 * the region size. (Defined as x and z statically in this class.)
	 */
	public static Region loadRegion(Loader loader, IWorldProvider s, int x, int z, String worldLocation) {
		Region r = new Region(x, z);
		LZ4Factory factory = LZ4Factory.fastestInstance();
		// Threading increases load / save speed by A LOT
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {*/
				DataInputStream is = null;
				try {
					// data in stream for the region
					is = new DataInputStream(new LZ4BlockInputStream(new FileInputStream(worldLocation + "DIM/" + x + "_" + z + ".region"), factory.fastDecompressor()));
				} catch (IOException e) {return r;}
				try {
					// byte flag
					byte b = 0;
					while (b != -2) {
						int posX = is.readInt();
						int posZ = is.readInt();
						// add in all the blocks in this chunk
						short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
						for (int i = 0; i < blks.length; i++) {
							for (int j = 0; j < blks[i].length; j++) {
								for (int k = 0; k < blks[i][j].length; k++) {
									try {
									blks[i][j][k] = is.readShort();
									int xk = 0;
									int zk = 0;
									if (posX < 0)
										xk = 1;
									if (posZ < 0)
										zk = 1;
									Block.blocks.get((short)(blks[i][j][k] & 0xFFF)).onBlockPlaced(i + (posX * Chunk.x) + xk, j, k + (posZ * Chunk.z) + zk, s);
									} catch (Exception e) {if (j==0) blks[i][j][k]=Block.WILL; else blks[i][j][k] = 0;}
								}
							}
						}
						// add the chunk to the region
						b = is.readByte();
						r.setChunk(posX, posZ, new Chunk(loader, s, blks, posX, posZ));
					}
					
					is.close();
				} catch (IOException e) {}
				LightingEngine.recalculate();
			//}
		//}).start();
		// return the loaded region
		//System.gc();
		return r;
	}
	
	/**
	 * Saves this region inside the specified world location. if game is true then it will treat it as if the game is running (slower but less stuttering)
	 */
	public void saveRegion(String worldLocation, boolean game) {
		// Threading increases save speed by A LOT
		new Thread(new Runnable() {
			@Override
			public void run() {
				LZ4Factory factory = LZ4Factory.fastestInstance();
				// NOTE: i am aware of:
				//bytes[0] = (byte)(x & 0xff);
				//bytes[1] = (byte)((x >> 8) & 0xff);
				// for short -> byte conversion but this is more clean code.
				// easier to read.
				MapIterator<MultiKey<? extends Integer>, Chunk> chunkIt = chunks.mapIterator();
				DataOutputStream os = null;
				try {
					os = new DataOutputStream(new LZ4BlockOutputStream(
							new FileOutputStream(worldLocation + "DIM/" + xpos + "_" + zpos + ".region"), 1 << 16, factory.fastCompressor()));
				} catch (IOException e1) {return;}
				int chunkCount = 0;
				while (chunkIt.hasNext()) {
					MultiKey<? extends Integer> ke = chunkIt.next();
					try {
						Chunk c = chunks.get(ke);
						if (c == null)
							continue;
						os.writeInt(ke.getKey(0));
						os.writeInt(ke.getKey(1));
						short[][][] ch = c.getBlocks();
						if (ch == null)
							continue;
						for (int i = 0; i < ch.length; i++) {
							for (int j = 0; j < ch[i].length; j++) {
								for (int k = 0; k < ch[i][j].length; k++) {
									os.writeShort(ch[i][j][k]);
								}
							}
						}
						chunkCount++;
						if (chunkCount == chunks.size()) {
							os.writeByte(-2);
						} else
							os.writeByte(-1);
						c.nul();
					} catch (IOException e) {
						if (!new File(worldLocation).mkdirs())
							saveRegion(worldLocation, game);
					}
					if (game) {
						try {
							Thread.sleep(16);
						} catch (InterruptedException e) {}
					}
				}
				try {
					os.close();
				} catch (IOException e) {}
				System.gc();
			}
		}).start(); 
		System.gc();
	}
	
	/**
	 * Nulls all the times inside so that way garbage collection can remove them.
	 */
	public void nul() {
		MapIterator<MultiKey<? extends Integer>, Chunk> rgr = chunks.mapIterator();
		while (rgr.hasNext()) {
			MultiKey<? extends Integer> r = rgr.next();
			chunks.get(r).nul();
		}
		chunks.clear();
	}
	
	/**
	 * Gets the x of the region standardized coordinate for this region.
	 */
	public int getXpos() {
		return xpos;
	}
	
	/**
	 * Gets the z of the region standardized coordinate for this region.
	 */
	public int getZpos() {
		return zpos;
	}
	
	/**
	 * Gets chunk by chunk standardized coordinate.
	 * (Divide world pos by chunk size)
	 */
	public Chunk getChunk(int x, int z) {
		if (chunks.containsKey(x, z))
			return chunks.get(x, z);
		else
			return null;
	}
	
	/**
	 * same as {@code getChunk(int x, int z)}
	 * but with a key from the iterator.
	 */
	public Chunk getChunkByKey(MultiKey<? extends Integer> e) {
		if (chunks.containsKey(e.getKey(0), e.getKey(1)))
				return chunks.get(e.getKey(0), e.getKey(1));
		else
			return null;
	}
	
	/**
	 * Sets the chunk at the chunk standardized coordinate.
	 */
	public void setChunk(int x, int z, Chunk c) {
		chunks.put(x, z, c);
	}
	
	/**
	 * Returns the chunk map iterator.
	 */
	public MapIterator<MultiKey<? extends Integer>, Chunk> getChunkIterator(){
		return chunks.mapIterator();
	}
	
}
