package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemWoodenShovel extends ItemTool {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2970948253836861385L;

	public ItemWoodenShovel(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(1);
		super.setMiningSpeed(0.3f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
