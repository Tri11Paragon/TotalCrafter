package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemCopperPickaxe extends ItemTool {

	public ItemCopperPickaxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.45f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}

}
