package com.brett.voxel.world.blocks;

import com.brett.datatypes.Texture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.tileentity.TileFurnace;

/**
*
* @author brett
* @date May 17, 2020
*/

public class BlockFurnace extends Block {

	public BlockFurnace(Texture model) {
		super(model);
		this.textureTop = 35;
		this.textureFront = 32;
		this.textureBack = 34;
		this.textureLeft = 34;
		this.textureRight = 34;
		this.textureBottom = 34;
	}
	
	@Override
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		// get the tile entitiy for this pos and opens its inventory.
		TileFurnace te = (TileFurnace) world.getTileEntity(x, y, z);
		if (te != null) {
			te.openInventory();
			i.toggleEnabledIOnly();
		}
		return true;
	}
	
	@Override
	public void onBlockPlaced(int x, int y, int z, IWorldProvider world) {
		super.onBlockPlaced(x, y, z, world);
		// places the tile entity and makes sure its placed in the correct direction
		TileFurnace ent = new TileFurnace();
		world.spawnTileEntity(ent, x, y, z);
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
	
	@Override
	public void onBlockBreaked(int x, int y, int z, IWorldProvider world) {
		super.onBlockBreaked(x, y, z, world);
		// remove the tile entity
		world.destoryTileEntity(world.getTileEntity(x, y, z));
	}
	
}
