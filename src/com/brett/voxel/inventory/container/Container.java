package com.brett.voxel.inventory.container;

import java.io.Serializable;

import com.brett.IInventoryDisable;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.tileentity.TileEntity;

/**
*
* @author brett
* @date Mar. 15, 2020
*/

public class Container extends TileEntity implements Serializable, IInventoryDisable {
	
	private static final long serialVersionUID = 7893074616663499692L;
	
	@Override
	public void spawnTileEntity(int x, int y, int z, IWorldProvider world) {
		super.spawnTileEntity(x, y, z, world);
		// register this entity.
		PlayerInventory.registerDisableState(this);
		if (VoxelScreenManager.ui != null)
			VoxelScreenManager.ui.addMenu(i);
		hasChanged = true;
	}
	
	public void openInventory() {
		i.enable();
	}
	
	public void closeInventory() {
		i.disable();
	}
	
	@Override
	public void destroy() {
		// remove this entity
		PlayerInventory.removeDisableState(this);
		if (VoxelScreenManager.ui != null)
			VoxelScreenManager.ui.removeMenu(i);
		super.destroy();
	}
	
	@Override
	public void renderUpdate() {
		
	}
	
	@Override
	public boolean getTileChanged() {
		return i.hasChanged() | super.getTileChanged();
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
