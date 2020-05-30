package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemWoodenShovel extends ItemTool {
	
	public ItemWoodenShovel(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(1);
		super.setMiningSpeed(0.3f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
