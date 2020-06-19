package com.brett.voxel.world.tileentity;

import java.util.List;

import org.lwjgl.opengl.Display;

import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.container.Container;
import com.brett.voxel.inventory.recipe.CraftingManager;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date May 17, 2020
*/

public class TileFurnace extends Container {
	
	private static final long serialVersionUID = 399355403172335183L;
	
	private int fuel;
	private int progress;
	private boolean spawned = false;
	
	@Override
	public void spawnTileEntity(int x, int y, int z, IWorldProvider world) {
		super.spawnTileEntity(x, y, z, world);
		// create the tile inventory.
		float sizeX = 48*1;
		float sizeY = 48*1;
		float xz = Display.getWidth()/2 - sizeX/2;
		float yz = Display.getHeight()/2 - sizeY/2 + 80;
		super.i.addSlot(new Slot(xz - 100,yz - 375, 48, 48));
		super.i.addSlot(new Slot(xz - 100,yz - 250, 48, 48));
		super.i.addSlot(new Slot(xz + 100,yz - 300, 48, 48));
		super.i.loadInventory();
		spawned = true;
		hasChanged = true;
	}
	
	@Override
	public void load() {
		super.load();
		// load fuel and progress information
		String fue = super.getNBT().loadNBT("fuel");
		String prog = super.getNBT().loadNBT("progress");
		if (fue != null)
			fuel = Integer.parseInt(fue);
		if (prog != null)
			progress = Integer.parseInt(prog);
	}
	
	@Override
	public void save() {
		// save the fuel and progress information
		super.getNBT().saveNBT("fuel", fuel);
		super.getNBT().saveNBT("progress", progress);
		super.save();
	}
	
	private int la1 = 0,la2 = 0,la3 = 0;
	
	@Override
	public void renderUpdate() {
		super.renderUpdate();
		// don't do any updates if it hasn't spawned.
		if (!spawned)
			return;
		// update the slots.
		if (i != null) {
			List<Slot> slots = i.getSlots();
			if (slots.get(0).getItemsAmount() != la1) {
				la1 = slots.get(0).getItemsAmount();
				slots.get(0).updateText();
			}
			if (slots.get(1).getItemsAmount() != la2) {
				la2 = slots.get(1).getItemsAmount();
				slots.get(1).updateText();
			}
			if (slots.get(2).getItemsAmount() != la3) {
				la3 = slots.get(2).getItemsAmount();
				slots.get(2).updateText();
			}
		}
	}
	
	@Override
	public void tick(long skip) {
		super.tick(skip);
		if (!spawned)
			return;
		if (i != null) {
			List<Slot> slots = i.getSlots();
			// make sure there is an item in the input
			if (slots.get(0).getItem() != null) {
				// get the recipe for this item (if there is one)
				long val = CraftingManager.getFurnaceRecipe(slots.get(0).getItemID());
				if (val == 0)
					return;
				// get fuel item
				if (slots.get(1).getItem() != null) {
					// add the fuel if it is fuel and if there is needed fuel
					int fuelTime = GameRegistry.getItemFuel(slots.get(1).getItemID());
					if (fuel <= 0 && fuelTime > 0) {
						// update the slot
						slots.get(1).getItemStack().decreaseStack(1);
						if (slots.get(1).getItemStack().getAmountInStack() <= 0)
							slots.get(1).changeItem(null);
						fuel = fuelTime;
					}
				}
				if (fuel > 0) {
					// decode information from the recipe.
					long temp = val << 32;
					// result id
					int id = (int) (temp >> 32);
					int time = (int)(val >> 32);
					// adjust for any lag
					progress+=skip;
					// decrease the fuel amount while we are smelting.
					if (progress < time)
						fuel--;
					// once we have finished we need to add item to the slot
					if (progress >= time) {
						// make sure we can output items
						if (slots.get(2).getItem() == null) {
							// set the stack
							slots.get(2).setItemStack(new ItemStack(Item.items.get((short)id), 1));
						} else {
							// or increase stack if its id.
							if (slots.get(2).getItemID() == id) {
								slots.get(2).getItemStack().increaseStack(1);
							} else
								return;
						}
						// reset progress
						progress = 0;
						// decrease the stacks
						slots.get(0).getItemStack().decreaseStack(1);
						if (slots.get(0).getItemsAmount() <= 0)
							slots.get(0).changeItem(null);
					}
				} else {
				}
			}
		}
	}
	
}
