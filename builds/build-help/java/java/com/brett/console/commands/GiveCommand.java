package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Apr. 16, 2020
* Gives the player and item.
*/

public class GiveCommand extends Command {

	private PlayerInventory i;
	
	public GiveCommand(PlayerInventory i) {
		this.i = i;
	}
	
	@Override
	public String run(String data, String[] vars) {
		if (i == null)
			i = VoxelScreenManager.world.ply.getInventory();
		// make sure they enter an item id
		if (vars.length < 1)
			return "Please enter the item id!";
		// default amount is 1
		int amount = 1;
		short id = Short.parseShort(vars[0]);
		// parse the amount if the user entered one.
		if (vars.length > 1)
			amount = Integer.parseInt(vars[1]);
		// make sure the item exists
		if (!Item.items.containsKey(id))
			return "Please enter a valid item ID!";
		try {
			// if the item exists add it to the inventory.
			i.addItemToInventory(new ItemStack(Item.items.get(id), amount));
		} catch (Exception e) {
			System.err.println("ERROR GIVING ITEM");
		}
		return "";
	}
	
}
