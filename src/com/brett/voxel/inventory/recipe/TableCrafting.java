package com.brett.voxel.inventory.recipe;

import com.brett.voxel.inventory.Inventory;
import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.SlotChange;
import com.brett.voxel.world.LevelLoader;

/**
*
* @author brett
* @date May 14, 2020
*/

public class TableCrafting extends Inventory implements SlotChange {

	public TableCrafting() {
		super((int)LevelLoader.seed);
	}

	@Override
	public void onChange(Slot s) {
		
	}
	
}
