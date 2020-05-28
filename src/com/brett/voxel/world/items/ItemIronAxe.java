package com.brett.voxel.world.items;

import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemIronAxe extends ItemTool {

	public ItemIronAxe(short id, ModelTexture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.53f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
