package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemWoodenAxe extends ItemTool {

	public ItemWoodenAxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(1);
		super.setMiningSpeed(0.3f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
