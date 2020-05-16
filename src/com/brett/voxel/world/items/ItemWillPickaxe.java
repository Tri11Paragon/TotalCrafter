package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemWillPickaxe extends ItemTool {
	
	public ItemWillPickaxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(1000);
		super.setMiningSpeed(1.1f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}

}
