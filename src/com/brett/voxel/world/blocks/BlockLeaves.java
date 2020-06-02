package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.world.IWorldProvider;

/**
*
* @author brett
* @date May 26, 2020
*/

public class BlockLeaves extends Block {

	public BlockLeaves(ModelTexture model, int i) {
		super(model, i);
	}
	
	@Override
	public void onBlockTick(int x, int y, int z, IWorldProvider world) {
		super.onBlockTick(x, y, z, world);
	}

}
