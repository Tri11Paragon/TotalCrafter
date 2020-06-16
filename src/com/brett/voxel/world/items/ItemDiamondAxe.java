package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 27, 2020
*/

public class ItemDiamondAxe extends ItemTool {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8527942462931876878L;

	public ItemDiamondAxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.8f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_AXE);
	}

}
