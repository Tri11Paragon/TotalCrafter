package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemIronAxe extends ItemTool {

	/**
	 * 
	 */
	private static final long serialVersionUID = -690982119362340468L;

	public ItemIronAxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.53f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
