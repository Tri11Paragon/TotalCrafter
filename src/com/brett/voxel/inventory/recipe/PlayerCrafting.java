package com.brett.voxel.inventory.recipe;

import org.lwjgl.opengl.Display;

import com.brett.voxel.inventory.Inventory;
import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.SlotChange;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date May 14, 2020
*/

public class PlayerCrafting extends Inventory implements SlotChange {
	
	private static final long serialVersionUID = 3523526757745624172L;
	
	// 4 slots for player crafting plus one for output items.
	private Slot s1,s2,s3,s4,so;
	
	public PlayerCrafting() {
		super((int)LevelLoader.seed, "crafting");
		float sizeX = 48*2;
		float sizeY = 48*2;
		float x = Display.getWidth()/2 - sizeX/2 + 120;
		float y = Display.getHeight()/2 - sizeY/2 - 180;
		s1 = new Slot(x-48/2, y, 48, 48).setSc(this);
		s2 = new Slot(x+48/2, y, 48, 48).setSc(this);
		s3 = new Slot(x-48/2, y+48, 48, 48).setSc(this);
		s4 = new Slot(x+48/2, y+48, 48, 48).setSc(this);
		// mark the output for outputting. (wont allow items to be placed in it)
		so = new Slot("o", x+48*3, y+48/2, 48, 48).setSc(this);
		// adds slots to inventory
		this.addSlot(s1);
		this.addSlot(s2);
		this.addSlot(s3);
		this.addSlot(s4);
		this.addSlot(so);
	}

	@Override
	public void onChange(Slot s) {
		Item i1 = s1.getItem();
		Item i2 = s2.getItem();
		Item i3 = s3.getItem();
		Item i4 = s4.getItem();
		int id1 = 0;
		int id2 = 0;
		int id3 = 0;
		int id4 = 0;
		// get the itemids if they exist.
		if (i1 != null)
			id1 = i1.getId();
		if (i2 != null)
			id2 = i2.getId();
		if (i3 != null)
			id3 = i3.getId();
		if (i4 != null)
			id4 = i4.getId();
		StringBuilder bild = new StringBuilder();
		/**
		 * I'm not sure I can really explain all this (especially the 3x3 crafting)
		 * The idea behind the builder below is that if an item is placed inside the crafting table that it should be added to the string
		 * if not then don't add it. This works well for 2x2 crafting tables as there are no special shaped recipes (see furnace recipe)
		 * The more advanced 3x3 crafting builder will attempt to "smart" add air items into the crafting recipe. Take for example the furnace
		 * it takes 3 cobble on the top, 3 on the bottom and 2 in the middle with nothing in the exact center. The 2x2 system (below) would completely
		 * fail at this as it doesn't add spaces in and therefore would accept 2 cobble with no space in the middle. 
		 * This and the 3x3 crafting table build in what I call standard format for crafting recipes. This is how you build it if you are wondering.
		 */
		if (id1 != 0) {
			bild.append(id1);
			if (id2 != 0)
				bild.append(',');
			else { // adds the ; if we are at the end here.
				if (id3 != 0 || id4 != 0)
					bild.append(';');
			}
		}
		if (id2 != 0) {
			bild.append(id2);
			if (id3 != 0 || id4 != 0)
				bild.append(';');
		}
		if (id3 != 0) {
			bild.append(id3);
			if (id4 != 0)
				bild.append(',');
		}
		if (id4 != 0)
			bild.append(id4);
		int amount = 0;
		int id = 0;
		if (bild.toString().length() > 0) {
			// get the recipe
			long undecoded = CraftingManager.getRecipe(bild.toString());
			// make sure it exists
			if (undecoded != 0) {
				// get out the amount by doing the inverse of ^
				amount = (int) (undecoded >> 32);
				// this makes sure that we don't have any pesky bits from the amount
				long temp = undecoded << 32;
				// convert to id
				id = (int) (temp >> 32);
			}
		}
		// remove items from the other slots if there is a new item in the output slot
		if (s.getName() == "o") {
			if (i1 != null)
				s1.removeItems(1);
			if (i2 != null)
				s2.removeItems(1);
			if (i3 != null)
				s3.removeItems(1);
			if (i4 != null)
				s4.removeItems(1);
		}
		// this handles actually putting the item in the output slot
		if (id != 0 && amount != 0) {
			int lowest = Integer.MAX_VALUE-1;
			// this makes sure that there is items in the slot
			if (s1.getItemsAmount() < lowest && s1.getItemsAmount() > 0)
				lowest = s1.getItemsAmount();
			if (s2.getItemsAmount() < lowest && s2.getItemsAmount() > 0)
				lowest = s2.getItemsAmount();
			if (s3.getItemsAmount() < lowest && s3.getItemsAmount() > 0)
				lowest = s3.getItemsAmount();
			if (s4.getItemsAmount() < lowest && s4.getItemsAmount() > 0)
				lowest = s4.getItemsAmount();
			// set lowest if there is nothing in any of them
			if (s1.getItemsAmount() == 0 && s2.getItemsAmount() == 0 && s3.getItemsAmount() == 0 && s4.getItemsAmount() == 0)
				lowest = 0;
			// add item to the output if there is items in the input.
			if (lowest > 0) {
				so.setItemStack(new ItemStack(Item.items.get((short)id), amount));
				so.updateText();
			}
		} else {
			so.setItemStack(null);
			so.updateText();
		}
	}
	
}
