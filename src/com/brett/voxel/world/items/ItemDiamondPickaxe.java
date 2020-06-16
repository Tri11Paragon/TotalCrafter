package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
 *
 * @author brett
 * @date Apr. 16, 2020
 */

public class ItemDiamondPickaxe extends ItemTool {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2464936233742990964L;

	public ItemDiamondPickaxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(4);
		super.setMiningSpeed(0.8f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}

}
