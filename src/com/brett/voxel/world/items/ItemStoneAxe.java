package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemStoneAxe extends ItemTool {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6389610055835463737L;

	public ItemStoneAxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(2);
		super.setMiningSpeed(0.43f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
