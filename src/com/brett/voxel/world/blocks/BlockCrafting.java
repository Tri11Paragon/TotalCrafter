package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date May 13, 2020
*/

public class BlockCrafting extends Block {

	public BlockCrafting(ModelTexture model) {
		super(model);
		super.textureTop = 24;
		super.textureBack = 25;
		super.textureLeft = 25;
		super.textureRight = 25;
		super.textureFront = 26;
		super.textureBottom = 27;
	}
	
	@Override
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		
		return true;
	}

}
