package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Mar. 11, 2020
*/

public class BlockGrass extends Block {

	
	public BlockGrass(ModelTexture model) {
		super(model);
	}
	
	@Override
	public void onBlockTick(int x, int y, int z, VoxelWorld world) {
		if (world.chunk.getBlockBIAS(x, y, z+1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y, z+1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x+1, y, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x+1, y, z, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x-1, y, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x-1, y, z, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x, y, z-1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y, z-1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x, y+1, z+1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y+1, z+1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x, y+1, z-1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y+1, z-1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x+1, y+1, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x+1, y+1, z, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x-1, y+1, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x-1, y+1, z, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x, y-1, z+1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y-1, z+1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x, y-1, z-1) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x, y-1, z-1, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x+1, y-1, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x+1, y-1, z, Block.BLOCK_GRASS);
			return;
		}
		if (world.chunk.getBlockBIAS(x-1, y-1, z) == Block.BLOCK_DIRT) {
			world.chunk.setBlockBIAS(x-1, y-1, z, Block.BLOCK_GRASS);
			return;
		}
	}

}
