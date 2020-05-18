package com.brett.voxel.inventory.container;

import com.brett.IInventoryDisable;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.Inventory;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.LevelLoader;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.tileentity.TileEntity;

/**
*
* @author brett
* @date Mar. 15, 2020
*/

public class Container extends TileEntity implements IInventoryDisable {
	
	protected Inventory i;
	
	@Override
	public void spawnTileEntity(int x, int y, int z, VoxelWorld world) {
		super.spawnTileEntity(x, y, z, world);
		i = new Inventory((int)LevelLoader.seed, "tile/inv_" + x + "_" + y + "_" + z + "_");
		PlayerInventory.registerDisableState(this);
		VoxelScreenManager.ui.addMenu(i);
	}
	
	public void openInventory() {
		i.enable();
	}
	
	public void closeInventory() {
		i.disable();
	}
	
	@Override
	public void destroy() {
		PlayerInventory.removeDisableState(this);
		VoxelScreenManager.ui.removeMenu(i);
		super.destroy();
	}
	
	@Override
	public void save() {
		super.save();
		i.saveInventory();
	}

	@Override
	public boolean disableInventory() {
		if (i.isEnabled()) {
			i.disable();
			return true;
		} else
			return false;
	}
	
}
