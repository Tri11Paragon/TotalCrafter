package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date May 29, 2020
*/

public class ItemDiamondShovel extends ItemTool {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5678461857929659699L;

	public ItemDiamondShovel(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.8f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_SHOVEL);
	}
	
}
