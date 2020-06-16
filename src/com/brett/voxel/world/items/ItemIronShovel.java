package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemIronShovel extends ItemTool {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 96692581258274395L;

	public ItemIronShovel(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.53f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
