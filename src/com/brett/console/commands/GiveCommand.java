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
		if (vars.length < 1)
			return "Please enter the item id!";
		int amount = 1;
		short id = Short.parseShort(vars[0]);
		System.out.println(id);
		if (vars.length > 1)
			amount = Integer.parseInt(vars[1]);
		if (!Item.items.containsKey(id))
			return "Please enter a valid item ID!";
		try {
			i.addItemToInventory(new ItemStack(Item.items.get(id), amount));
		} catch (Exception e) {
			System.out.println("ERROR");
		}
		return "";
	}
	
}
