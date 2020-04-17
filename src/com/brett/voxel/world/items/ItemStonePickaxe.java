package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemStonePickaxe extends ItemTool {
	
	public ItemStonePickaxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.4f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}
	
}
