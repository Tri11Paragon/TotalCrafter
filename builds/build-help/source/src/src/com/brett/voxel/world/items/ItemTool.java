package com.brett.voxel.world.items;

import com.brett.cameras.Camera;
import com.brett.datatypes.Texture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;

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
	
	public ItemTool(short id, Texture texture) {
		super(id, texture);
	}

	public void onBlockMined(int x, int y, int z, Block b, VoxelWorld world, Camera c, PlayerInventory i) {
		
	}
	
	public void onRightClick(int x, int y, int z, VoxelWorld world, Camera c, PlayerInventory i) {
		
	}
	
	public void onLeftClick(int x, int y, int z, VoxelWorld world, Camera c, PlayerInventory i) {
		
	}
	
	public int getToolType() {
		return toolType;
	}

	public void setToolType(int toolType) {
		this.toolType = toolType;
	}
	
}
