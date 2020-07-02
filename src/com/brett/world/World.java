package com.brett.world;

import java.util.ArrayList;
import java.util.List;

import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.NdHashMap;
import com.brett.world.chunks.data.RenderMode;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class World {
	
	public static World world;
	
	public NdHashMap<Integer, Chunk> chunks = new NdHashMap<Integer, Chunk>();
	public List<Chunk> ungeneratedChunks = new ArrayList<Chunk>();
	
	public World() {
		World.world = this;
	}
	
	public void setBlock(int x, int y, int z, short id) {
		Chunk c	= getChunkWorld(x, y, z);
		if (c == null) {
			c = new Chunk(this, new ShortBlockStorage(), new ByteBlockStorage(), new ByteBlockStorage(), x >> 4, y >> 4, z >> 4);
			ungeneratedChunks.add(c);
		}
		if (c != null)
			c.blocks.setWorld(x, y, z, id);
	}
	
	public RenderMode getRenderMode(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return GameRegistry.getBlock((short)0).getRenderMode();
		short block = c.blocks.getWorld(x, y, z);
		return GameRegistry.getBlock(block).getRenderMode();
	}
	
	public RenderMode getRenderModeNull(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return null;
		short block = c.blocks.getWorld(x, y, z);
		Block b = Block.blocks.get(block);
		if (b == null)
			return null;
		return b.getRenderMode();
	}
	
	public short getBlock(int x, int y, int z) {
		Chunk c = getChunkWorld(x, y, z);
		if (c == null)
			return 0;
		return c.blocks.getWorld(x, y, z);
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
	
	public void setChunk(Chunk c) {
		chunks.set(c.x_pos, c.y_pos, c.y_pos, c);
	}
	
	public void setChunkWorld(int x, int y, int z, Chunk c) {
		chunks.set(x >> 4, y >> 4, z >> 4, c);
	}
	
	public void save() {
		
	}
	
}
