package com.brett.voxel.world.items;

import com.brett.datatypes.Texture;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class ItemIronPickaxe extends ItemTool {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 920142220327257305L;

	public ItemIronPickaxe(short id, Texture texture) {
		super(id, texture);
		super.setMiningLevel(3);
		super.setMiningSpeed(0.53f);
		super.setMaxStackSize(1);
		super.setToolType(ItemTool.TOOL_PICKAXE);
	}
	
}
