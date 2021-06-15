package com.brett.world.chunks.data;

import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.biome.BiomePalette;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class LowFloatBiomeStorage {
	
	public BiomePalette palette;
	public byte[][][] biomeType = new byte[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
	public float[][][] biomeBlend = new float[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
	
	public LowFloatBiomeStorage() {
		
	}
	
	public LowFloatBiomeStorage(BiomePalette palette) {
		this.palette = palette;
	}
	
	public LowFloatBiomeStorage setPalette(BiomePalette palette) {
		this.palette = palette;
		return this;
	}
	
	/**
	 * returns the block at a position in the array
	 */
	public byte get(int x, int y, int z) {
		return biomeType[x][y][z];
	}
	
	public byte getWorld(int x, int y, int z) {
		return biomeType[x & 0xF][y & 0xF][z & 0xF];
	}
	
	public void setAirWorld(int x, int y, int z, byte id) {
		if (biomeType[x & 0xF][y & 0xF][z & 0xF] == Block.AIR)
			biomeType[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setAirWorld(int x, int y, int z, int id) {
		if (biomeType[x & 0xF][y & 0xF][z & 0xF] == Block.AIR)
			biomeType[x & 0xF][y & 0xF][z & 0xF] = (byte)id;
	}
	
	public void setWorld(int x, int y, int z, byte id) {
		biomeType[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setWorld(int x, int y, int z, int id) {
		biomeType[x & 0xF][y & 0xF][z & 0xF] = (byte) id;
	}
	
}
