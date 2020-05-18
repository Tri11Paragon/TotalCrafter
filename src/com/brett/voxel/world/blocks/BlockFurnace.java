package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.tileentity.TileFurnace;

/**
*
* @author brett
* @date May 17, 2020
*/

public class BlockFurnace extends Block {

	public BlockFurnace(ModelTexture model) {
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
		TileFurnace te = (TileFurnace) world.getTileEntity(x, y, z);
		if (te != null) {
			te.openInventory();
			i.toggleEnabledIOnly();
		}
		return true;
	}
	
	@Override
	public void onBlockPlaced(int x, int y, int z, VoxelWorld world) {
		super.onBlockPlaced(x, y, z, world);
		TileFurnace ent = new TileFurnace();
		world.spawnTileEntity(ent, x, y, z);
	}
	
	@Override
	public void onBlockBreaked(int x, int y, int z, VoxelWorld world) {
		super.onBlockBreaked(x, y, z, world);
		world.destoryTileEntity(world.getTileEntity(x, y, z));
	}
	
}
