package com.brett.world;

import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.NdHashMap;
import com.brett.world.chunks.data.RenderMode;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class World {
	
	public NdHashMap<Integer, Chunk> chunks = new NdHashMap<Integer, Chunk>();
	
	public World() {
		
	}
	
	public void setBlock(int x, int y, int z, short id) {
		getChunkWorld(x, y, z).blocks.setWorld(x, y, z, id);
	}
	
	public RenderMode getRenderMode(int x, int y, int z) {
		return GameRegistry.getBlock(getBlock(x, y, z)).getRenderMode();
	}
	
	public RenderMode getRenderModeNull(int x, int y, int z) {
		short block = getBlock(x, y, z);
		Block b = Block.blocks.get(block);
		if (b == null)
			return null;
		return b.getRenderMode();
	}
	
	public short getBlock(int x, int y, int z) {
		return getChunkWorld(x, y, z).blocks.getWorld(x, y, z);
	}
	
	/*
	 * gets the chunk in world pos
	 */
	public Chunk getChunkWorld(int wx, int wy, int wz) {
		return chunks.get(wx >> 4, wy >> 4, wz >> 4);
	}
	
	/**
	 * gets the chunk in chunk pos
	 */
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x, y, z);
	}
	
	public void setChunk(int x, int y, int z, Chunk c) {
		chunks.set(x, y, z, c);
	}
	
	public void setChunkWorld(int x, int y, int z, Chunk c) {
		chunks.set(x >> 4, y >> 4, z >> 4, c);
	}
	
}
