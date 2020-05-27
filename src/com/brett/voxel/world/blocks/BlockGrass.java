package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.renderer.RENDERMODE;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Mar. 11, 2020
*/

public class BlockGrass extends Block {

	
	public BlockGrass(ModelTexture model) {
		super(model, 20);
		super.textureTop = 0;
		super.textureBottom = 1;
		this.setBlockDropped(BLOCK_DIRT);
	}
	
	@Override
	public void onBlockTick(int x, int y, int z, VoxelWorld world) {
		if (Block.blocks.get(world.chunk.getBlockBIAS(x, y+1, z)).getRendermode() == RENDERMODE.SOLID) {
			world.chunk.setBlockBIAS(x, y, z, BLOCK_DIRT);
			return;
		}
		switch (world.random.nextInt(12)) {
			case 0:
				if (world.chunk.getBlockBIAS(x, y, z+1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y + 1, z+1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y, z+1, BLOCK_GRASS);
					return;
				}
				break;
			case 1:
				if (world.chunk.getBlockBIAS(x+1, y, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x+1, y + 1, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x+1, y, z, BLOCK_GRASS);
					return;
				}
				break;
			case 2:
				if (world.chunk.getBlockBIAS(x-1, y, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x-1, y + 1, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x-1, y, z, BLOCK_GRASS);
					return;
				}
				break;
			case 3:
				if (world.chunk.getBlockBIAS(x, y, z-1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y + 1, z-1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y, z-1, BLOCK_GRASS);
					return;
				}
				break;
			case 4:
				if (world.chunk.getBlockBIAS(x, y+1, z+1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y + 2, z+1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y+1, z+1, BLOCK_GRASS);
					return;
				}
				break;
			case 5:
				if (world.chunk.getBlockBIAS(x, y+1, z-1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y + 2, z-1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y+1, z-1, BLOCK_GRASS);
					return;
				}
				break;
			case 6:
				if (world.chunk.getBlockBIAS(x+1, y+1, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x+1, y + 2, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x+1, y+1, z, BLOCK_GRASS);
					return;
				}
				break;
			case 7:
				if (world.chunk.getBlockBIAS(x-1, y+1, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x-1, y + 2, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x-1, y+1, z, BLOCK_GRASS);
					return;
				}
				break;
			case 8:
				if (world.chunk.getBlockBIAS(x, y-1, z+1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y, z+1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y-1, z+1, BLOCK_GRASS);
					return;
				}
				break;
			case 9:
				if (world.chunk.getBlockBIAS(x, y-1, z-1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y, z-1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y-1, z-1, BLOCK_GRASS);
					return;
				}
				break;
			case 10:
				if (world.chunk.getBlockBIAS(x+1, y-1, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x+1, y, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x+1, y-1, z, BLOCK_GRASS);
					return;
				}
				break;
			case 11:
				if (world.chunk.getBlockBIAS(x-1, y-1, z) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x-1, y, z)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x-1, y-1, z, BLOCK_GRASS);
					return;
				}
				break;
			default:
				if (world.chunk.getBlockBIAS(x, y, z+1) == BLOCK_DIRT) {
					if (Block.blocks.get(world.chunk.getBlockBIAS(x, y + 1, z+1)).getRendermode() != RENDERMODE.SOLID)
						world.chunk.setBlockBIAS(x, y, z+1, BLOCK_GRASS);
					return;
				}
				break;
		}
	}
	
}
