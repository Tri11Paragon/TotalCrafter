package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Apr. 16, 2020
*/

public class GiveCommand extends Command {

	private PlayerInventory i;
	
	public GiveCommand(PlayerInventory i) {
		this.i = i;
	}
	
	@Override
	public String run(String data, String[] vars) {
		if (vars.length < 1)
			return "Please enter the item id!";
		int amount = 1;
		short id = Short.parseShort(vars[0]);
		if (vars.length > 1)
			amount = Integer.parseInt(vars[1]);
		if (!Item.items.containsKey(id))
			return "Please enter a valid item ID!";
		i.addItemToInventory(new ItemStack(Item.items.get(id), amount));
		return "";
	}
	
}
