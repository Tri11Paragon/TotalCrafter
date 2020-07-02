package com.brett.world.chunks.data;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class ByteBlockStorage {
	
public static final int SIZE = 16;
	
	public byte[] blocks = new byte[SIZE*SIZE*SIZE];
	
	/**
	 * returns the block at a position in the array
	 */
	public short get(int x, int y, int z) {
		return blocks[y + z * SIZE + x * SIZE * SIZE];
	}
	
	/**
	 * set a index in the array from a world position
	 */
	public short getWorld(int x, int y, int z) {
		return blocks[y & 0xF + (z & 0xF) * SIZE + (x & 0xF) * SIZE * SIZE];
	}
	
	/**
	 * set a index in the array from a chunk position
	 */
	public void set(int x, int y, int z, byte id) {
		(blocks[y + z * SIZE + x * SIZE * SIZE]) = id;
	}
	
	/**
	 * set a index in the array from a chunk position
	 */
	public void set(int x, int y, int z, int id) {
		(blocks[y + z * SIZE + x * SIZE * SIZE]) = (byte) id;
	}
	
	/**
	 * set a index in the array from a world position
	 */
	public void setWorld(int x, int y, int z, byte id) {
		blocks[y & 0xF + (z & 0xF) * SIZE + (x & 0xF) * SIZE * SIZE] = id;
	}
	
	/**
	 * set a index in the array from a world position
	 */
	public void setWorld(int x, int y, int z, int id) {
		blocks[y & 0xF + (z & 0xF) * SIZE + (x & 0xF) * SIZE * SIZE] = (byte) id;
	}
	
}
