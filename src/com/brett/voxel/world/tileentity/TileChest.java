package com.brett.voxel.world.tileentity;

import org.lwjgl.opengl.Display;

import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.container.Container;
import com.brett.voxel.world.IWorldProvider;

/**
*
* @author brett
* @date May 25, 2020
*/

public class TileChest extends Container {
	
	private static final long serialVersionUID = -7199507670111413332L;

	@Override
	public void spawnTileEntity(int x, int y, int z, IWorldProvider world) {
		super.spawnTileEntity(x, y, z, world);
		// add all the slots to the inventory for the chest
		float sizeX = 48*12;
		float sizeY = 48*4;
		float xz = Display.getWidth()/2 - sizeX/2;
		float yz = Display.getHeight()/2 - sizeY/2 - 200;
		for (int j = 0; j < 12; j++) {
			for (int k = 0; k < 4; k++) {
				i.addSlot(new Slot(xz + (j*48),yz + (k*48), 48, 48));
			}
		}
		// load it
		super.i.loadInventory();
		hasChanged = true;
	}
	
}
