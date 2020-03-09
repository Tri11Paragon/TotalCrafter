package com.brett.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.world.VoxelWorld;

/**
*
* @author brett
* @date Mar. 8, 2020
*/

public class BlockSand extends Block {

	// TODO: add in blockfalling class
	// TODO: improove this:
	private short id;
	
	public BlockSand(ModelTexture model, short id) {
		super(model);
		this.id = id;
	}
	
	@Override
	public void onBlockUpdated(int x, int y, int z, VoxelWorld world) {
		super.onBlockUpdated(x, y, z, world);
		if (world.chunk.getBlock(x, y - 1, z) == 0) {
			world.chunk.setBlock(x, y, z, 0);
			world.chunk.setBlock(x, y - 1, z, id);
		}
	}

}
