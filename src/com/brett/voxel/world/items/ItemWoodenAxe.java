package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemWoodenAxe extends ItemTool {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5437624229375977557L;

	public ItemWoodenAxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(1);
		super.setMiningSpeed(0.3f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
