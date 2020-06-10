package com.brett.voxel.world.chunk;

import com.brett.renderer.datatypes.BlockModelVAO;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;

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
	private BlockModelVAO[][][] blocksModels = new BlockModelVAO[Chunk.x][y][Chunk.z];
	@SuppressWarnings("unused")
	private float xoff, yoff, zoff;
	
	public ChunkSub(VoxelWorld world, float xoff, float yoff, float zoff) {
		this.world = world;
		this.xoff = xoff;
		this.yoff = yoff;
		this.zoff = zoff;
	}
	
	public void render() {
		// don't draw if there are no blocks in this chunk.
		if (size == 0)
			return;
		int xz = world.random.nextInt(Chunk.x);
		int yz = world.random.nextInt(Chunk.y);
		int zz = world.random.nextInt(Chunk.z);
		if (blocks[xz][yz][zz] != 0)
			Block.blocks.get(blocks[xz][yz][zz]).onBlockTick(xz, yz, zz, world);
		
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
	
	public void setBlockModel(int x, int y, int z, BlockModelVAO model) {
		blocksModels[x][y][z] = model;
	}
	
	public short getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public byte getBlockLight(int x, int y, int z) {
		return lightLevel[x][y][z];
	}
	
	public BlockModelVAO getBlockModel(int x, int y, int z) {
		return blocksModels[x][y][z];
	}
	
}
