package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemStoneShovel extends ItemTool {
	
	public ItemStoneShovel(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.43f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
