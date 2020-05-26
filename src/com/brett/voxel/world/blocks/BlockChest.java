package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.tileentity.TileChest;

/**
*
* @author brett
* @date May 25, 2020
*/

public class BlockChest extends Block {

	public BlockChest(ModelTexture model) {
		super(model);
		this.textureTop = 36;
		this.textureFront = 38;
		this.textureBack = 37;
		this.textureLeft = 37;
		this.textureRight = 37;
		this.textureBottom = 27;
	}
	
	@Override
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		TileChest te = (TileChest) world.getTileEntity(x, y, z);
		if (te != null) {
			te.openInventory();
			i.toggleEnabledIOnly();
		}
		return true;
	}
	
	@Override
	public void onBlockPlaced(int x, int y, int z, VoxelWorld world) {
		super.onBlockPlaced(x, y, z, world);
		TileChest ent = new TileChest();
		world.spawnTileEntity(ent, x, y, z);
	}
	
	@Override
	public void onBlockBreaked(int x, int y, int z, VoxelWorld world) {
		super.onBlockBreaked(x, y, z, world);
		world.destoryTileEntity(world.getTileEntity(x, y, z));
	}

}
