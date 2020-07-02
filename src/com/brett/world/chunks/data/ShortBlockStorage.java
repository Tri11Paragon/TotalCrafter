package com.brett.world.chunks.data;

import com.brett.world.GameRegistry;
import com.brett.world.World;
import com.brett.world.block.Block;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class ShortBlockStorage {
	
	public static final int SIZE = 16;
	
	public short[][][] blocks = new short[SIZE][SIZE][SIZE];
	
	/**
	 * returns the block at a position in the array
	 */
	public short get(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public short getWorld(int x, int y, int z) {
		return blocks[x & 0xF][y & 0xF][z & 0xF];
	}
	
	public void setAirWorld(int x, int y, int z, short id) {
		if (blocks[x & 0xF][y & 0xF][z & 0xF] == Block.AIR) {
			blocks[x & 0xF][y & 0xF][z & 0xF] = id;
			GameRegistry.getBlock(id).onBlockPlaced(World.world, id, x, y, z);
		}
	}
	
	public void setAirWorld(int x, int y, int z, int id) {
		if (blocks[x & 0xF][y & 0xF][z & 0xF] == Block.AIR) {
			blocks[x & 0xF][y & 0xF][z & 0xF] = (short)id;
			GameRegistry.getBlock((short)id).onBlockPlaced(World.world, (short)id, x, y, z);
		}
	}
	
	public void setWorld(int x, int y, int z, short id) {
		GameRegistry.getBlock(blocks[x & 0xF][y & 0xF][z & 0xF]).onBlockDestroyed(World.world, id, x, y, z);
		blocks[x & 0xF][y & 0xF][z & 0xF] = id;
		GameRegistry.getBlock(id).onBlockPlaced(World.world, (short)id, x, y, z);
	}
	
	public void setWorld(int x, int y, int z, int id) {
		GameRegistry.getBlock(blocks[x & 0xF][y & 0xF][z & 0xF]).onBlockDestroyed(World.world, (short)id, x, y, z);
		blocks[x & 0xF][y & 0xF][z & 0xF] = (short) id;
		GameRegistry.getBlock((short)id).onBlockPlaced(World.world, (short)id, x, y, z);
	}
	
}
