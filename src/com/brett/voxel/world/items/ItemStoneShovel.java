package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemStoneShovel extends ItemTool {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6347121858366222312L;

	public ItemStoneShovel(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.43f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
