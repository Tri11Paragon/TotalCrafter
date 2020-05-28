package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemStoneAxe extends ItemTool {

	public ItemStoneAxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.43f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
