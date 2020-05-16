package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.inventory.recipe.TableCrafting;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date May 13, 2020
*/

public class BlockCrafting extends Block {
	
	public static TableCrafting craft;
	
	public BlockCrafting(ModelTexture model) {
		super(model);
		super.textureTop = 24;
		super.textureBack = 25;
		super.textureLeft = 25;
		super.textureRight = 25;
		super.textureFront = 26;
		super.textureBottom = 27;
		
		craft = new TableCrafting();
		craft.loadInventory();
		VoxelScreenManager.ui.addMenu(craft);
	}
	
	@Override
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		craft.toggleEnabled();
		i.toggleEnabledIOnly();
		return true;
	}

}
