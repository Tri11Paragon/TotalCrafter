package com.brett.world.chunks;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class ShortBlockStorage {
	
	public static final int SIZE = 16;
	
	public short[] blocks = new short[SIZE*SIZE*SIZE];
	
	/**
	 * returns the block at a position in the array
	 */
	public short get(int x, int y, int z) {
		return blocks[y + z * SIZE + x * SIZE * SIZE];
	}
	
	public short getWorld(int x, int y, int z) {
		return blocks[y % SIZE + (z % SIZE) * SIZE + (x % SIZE) * SIZE * SIZE];
	}
	
	public void set(int x, int y, int z, short id) {
		(blocks[y + z * SIZE + x * SIZE * SIZE]) = id;
	}
	
	public void set(int x, int y, int z, int id) {
		(blocks[y + z * SIZE + x * SIZE * SIZE]) = (short) id;
	}
	
	public void setWorld(int x, int y, int z, short id) {
		blocks[y % SIZE + (z % SIZE) * SIZE + (x % SIZE) * SIZE * SIZE] = id;
	}
	
	public void setWorld(int x, int y, int z, int id) {
		blocks[y % SIZE + (z % SIZE) * SIZE + (x % SIZE) * SIZE * SIZE] = (short) id;
	}
	
}
