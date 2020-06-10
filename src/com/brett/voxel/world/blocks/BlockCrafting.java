package com.brett.voxel.world.blocks;

import com.brett.datatypes.Texture;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.inventory.recipe.TableCrafting;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date May 13, 2020
*/

public class BlockCrafting extends Block {
	
	public static TableCrafting craft;
	
	public BlockCrafting(Texture model) {
		super(model);
		super.textureTop = 24;
		super.textureBack = 25;
		super.textureLeft = 25;
		super.textureRight = 25;
		super.textureFront = 26;
		super.textureBottom = 27;
		
		craft = new TableCrafting();
		craft.loadInventory();
		if (VoxelScreenManager.ui != null)
			VoxelScreenManager.ui.addMenu(craft);
	}
	
	@Override
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		craft.toggleEnabled();
		i.toggleEnabledIOnly();
		return true;
	}
	
	@Override
	public void onBlockPlaced(int x, int y, int z, IWorldProvider world) {
		super.onBlockPlaced(x, y, z, world);
		if (world.ply == null)
			return;
		float yaw = world.ply.getYaw();
		if (yaw < 0)
			yaw = 360 - (-yaw);
		if (yaw > 45 && yaw <= 135) {
			world.chunk.setBlockStateBIAS(x, y, z, (byte)1);
		} else if (yaw > 135 && yaw <= 225) {
			world.chunk.setBlockStateBIAS(x, y, z, (byte)3);
		} else if (yaw > 225 && yaw <= 315) {
			world.chunk.setBlockStateBIAS(x, y, z, (byte)2);
		}
	}

}
