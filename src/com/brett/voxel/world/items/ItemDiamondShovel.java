package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemDiamondShovel extends ItemTool {
	
	public ItemDiamondShovel(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.8f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
