package com.brett.voxel.world.tileentity;

import org.lwjgl.opengl.Display;

import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.container.Container;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date May 25, 2020
*/

public class TileChest extends Container {
	
	@Override
	public void spawnTileEntity(int x, int y, int z, VoxelWorld world) {
		super.spawnTileEntity(x, y, z, world);
		float sizeX = 48*12;
		float sizeY = 48*4;
		float xz = Display.getWidth()/2 - sizeX/2;
		float yz = Display.getHeight()/2 - sizeY/2 - 200;
		for (int j = 0; j < 12; j++) {
			for (int k = 0; k < 4; k++) {
				i.addSlot(new Slot(xz + (j*48),yz + (k*48), 48, 48));
			}
		}
		super.i.loadInventory();
	}
	
}
