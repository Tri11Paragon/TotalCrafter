package com.brett.world.chunks;

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
	
	public void set(int x, int y, int z, short id) {
		blocks[x][y][z] = id;
	}
	
	public void set(int x, int y, int z, int id) {
		blocks[x][y][z] = (short) id;
	}
	
	public void setWorld(int x, int y, int z, short id) {
		blocks[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setWorld(int x, int y, int z, int id) {
		blocks[x & 0xF][y & 0xF][z & 0xF] = (short) id;
	}
	
}
