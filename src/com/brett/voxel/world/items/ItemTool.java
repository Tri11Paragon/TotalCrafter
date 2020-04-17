package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemTool extends Item {
	
	public static final int TOOL_HAND = 0;
	public static final int TOOL_PICKAXE = 1;
	public static final int TOOL_SHOVEL = 2;
	public static final int TOOL_AXE = 3;
	public static final int TOOL_SWORD = 4;
	
	private int toolType = 0;
	
	public ItemTool(short id, ModelTexture texture) {
		super(id, texture);
	}

	public void onBlockMined(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		
	}
	
	public int getToolType() {
		return toolType;
	}

	public void setToolType(int toolType) {
		this.toolType = toolType;
	}
	
}
