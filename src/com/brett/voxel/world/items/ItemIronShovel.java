package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemIronShovel extends ItemTool {
	
	public ItemIronShovel(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.53f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
