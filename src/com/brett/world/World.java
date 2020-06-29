package com.brett.world;

import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.NdHashMap;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class World {
	
	public NdHashMap<Integer, Chunk> chunks = new NdHashMap<Integer, Chunk>();
	
	public World() {
		
	}
	
	public short getBlock(int x, int y, int z) {
		return getChunkWorld(x, y, z).blocks.getWorld(x, y, z);
	}
	
	public Chunk getChunkWorld(int wx, int wy, int wz) {
		return chunks.get(wx >> 4, wy >> 4, wz >> 4);
	}
	
	public Chunk getChunk(int x, int y, int z) {
		return chunks.get(x, y, z);
	}
	
}
