package com.brett.voxel.world.chunk;

import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Apr. 8, 2020
*/

public class ChunkSub {
	
	public static int y = Chunk.y / 8;
	
	private short size;
	private VoxelWorld world;
	private short[][][] blocks = new short[Chunk.x][y][Chunk.z];
	private byte[][][] lightLevel = new byte[Chunk.x][y][Chunk.z];
	private RawBlockModel[][][] blocksModels = new RawBlockModel[Chunk.x][y][Chunk.z];
	
	public ChunkSub(VoxelWorld world) {
		this.world = world;
	}
	
	public void render() {
		// don't draw if there are no blocks in this chunk.
		if (size == 0)
			return;
		
	}
	
	public void setBlock(int x, int y, int z, short block) {
		if (blocks[x][y][z] == block)
			return;
		if (block == (short)0) {
			size--;
			if (size < 0)
				size = 0;
		} else 
			size++;
		blocks[x][y][z] = block;
	}
	
	public void setLightLevel(int x, int y, int z, byte level) {
		lightLevel[x][y][z] = level;
	}
	
	public void setBlockModel(int x, int y, int z, RawBlockModel model) {
		blocksModels[x][y][z] = model;
	}
	
	public short getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public byte getBlockLight(int x, int y, int z) {
		return lightLevel[x][y][z];
	}
	
	public RawBlockModel getBlockModel(int x, int y, int z) {
		return blocksModels[x][y][z];
	}
	
}
