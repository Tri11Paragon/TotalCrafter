package com.brett.world.chunks.data;

import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class ByteBlockStorage {
	
	public byte[][][] blocks = new byte[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
	
	/**
	 * returns the block at a position in the array
	 */
	public byte get(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public byte getWorld(int x, int y, int z) {
		return blocks[x & 0xF][y & 0xF][z & 0xF];
	}
	
	public void setAirWorld(int x, int y, int z, byte id) {
		if (blocks[x & 0xF][y & 0xF][z & 0xF] == Block.AIR)
			blocks[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setAirWorld(int x, int y, int z, int id) {
		if (blocks[x & 0xF][y & 0xF][z & 0xF] == Block.AIR)
			blocks[x & 0xF][y & 0xF][z & 0xF] = (byte)id;
	}
	
	public void setWorld(int x, int y, int z, byte id) {
		blocks[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setWorld(int x, int y, int z, int id) {
		blocks[x & 0xF][y & 0xF][z & 0xF] = (byte) id;
	}
	
}
