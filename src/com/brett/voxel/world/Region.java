package com.brett.voxel.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.brett.renderer.Loader;

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
	
	private static boolean loadingRegion = false;
	
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
	public static Region loadRegion(Loader loader, VoxelWorld s, int x, int z, String worldLocation) {
		loadingRegion = true;
		Region r = new Region(x, z);
		DataInputStream is = null;
		try {
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(worldLocation + "DIM/" + x + "_" + z + ".region")));
		} catch (FileNotFoundException e) { return r;}
		try {
			short b = 0;
			while (b != -2) {
				int posX = is.readInt();
				int posZ = is.readInt();
				short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
				for (int i = 0; i < blks.length; i++) {
					for (int j = 0; j < blks[i].length; j++) {
						for (int k = 0; k < blks[i][j].length; k++) {
							short sd = is.readShort();
							// this prevents interchunk saving issues
							// this doesn't prevent interregion chunk issues
							// ie not putting chunk endings at the end
							// but it does prevent missing information inside a chunk.
							// though i guess you could call air missing information
							// but i don't care
							if (sd < 0) 
								blks[i][j][k] = 0;
							else
								blks[i][j][k] = sd;
						}
					}
				}
				b = is.readShort();
				r.setChunk(posX, posZ, new Chunk(loader, s, blks, posX, posZ));
			}
			
			is.close();
		} catch (IOException e) {}
		loadingRegion = false;
		// please don't remove marks
		// this causes some minor suttering
		// might remove it.
		System.gc();
		return r;
	}
	
	/**
	 * Saves this region inside the specified world location.
	 */
	public void saveRegion(String worldLocation) {
		// we don't want to save this region if its being loading.
		// yes i am aware of this being static.
		if (loadingRegion)
			return;
		MapIterator<MultiKey<? extends Integer>, Chunk> chunkIt = chunks.mapIterator();
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(worldLocation + "DIM/" + xpos + "_" + zpos + ".region")));
		} catch (FileNotFoundException e1) {return;}
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
				for (int i = 0; i < ch.length; i++) {
					for (int j = 0; j < ch[i].length; j++) {
						for (int k = 0; k < ch[i][j].length; k++) {
							os.writeShort(ch[i][j][k]);
						}
					}
				}
				chunkCount++;
				if (chunkCount == chunks.size()) {
					os.writeShort(-2);
				} else
					os.writeShort(-1);
				// nulls the chunk
				c.nul();
				// removes the reference of this chunk
				chunkIt.remove();
				// this should free up some memory but java is stupid.
			} catch (IOException e) {
				if (!new File(worldLocation).mkdirs())
					saveRegion(worldLocation);
			}
		}
		try {
			os.close();
		} catch (IOException e) {}
		// this currently causes stuttering kedwell, but since it goes from 1gb -> 0.1gb of memory usages
		// in somecases when this is called, i am keeping it.
		// sorry if it causes stuttering. currently as of 2019-3-27 this is the best i got.
		
		// quick update:
		// i just got it to the point where it goes from 1gb -> 0.06gb
		// this could be fixed if this is called after each region loading
		// might go do that now
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
