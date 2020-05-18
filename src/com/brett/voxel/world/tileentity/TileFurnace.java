package com.brett.voxel.world.tileentity;

import java.util.List;

import org.lwjgl.opengl.Display;

import com.brett.voxel.inventory.Slot;
import com.brett.voxel.inventory.container.Container;
import com.brett.voxel.inventory.recipe.CraftingManager;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date May 17, 2020
*/

public class TileFurnace extends Container {
	
	private int fuel;
	private int progress;
	
	@Override
	public void spawnTileEntity(int x, int y, int z, VoxelWorld world) {
		super.spawnTileEntity(x, y, z, world);
		float sizeX = 48*1;
		float sizeY = 48*1;
		float xz = Display.getWidth()/2 - sizeX/2;
		float yz = Display.getHeight()/2 - sizeY/2 + 80;
		super.i.addSlot(new Slot(xz - 100,yz - 375, 48, 48));
		super.i.addSlot(new Slot(xz - 100,yz - 250, 48, 48));
		super.i.addSlot(new Slot(xz + 100,yz - 300, 48, 48));
	}
	
	@Override
	public void load() {
		super.load();
		String fue = super.getNBT().loadNBT("fuel");
		String prog = super.getNBT().loadNBT("progress");
		if (fue != null)
			fuel = Integer.parseInt(fue);
		if (prog != null)
			progress = Integer.parseInt(prog);
		System.out.println(fuel);
	}
	
	@Override
	public void save() {
		super.getNBT().saveNBT("fuel", fuel);
		super.getNBT().saveNBT("progress", progress);
		super.save();
	}
	
	@Override
	public void renderUpdate() {
		super.renderUpdate();
		List<Slot> slots = i.getSlots();
		slots.get(0).updateText();
		slots.get(1).updateText();
		slots.get(2).updateText();
	}
	
	@Override
	public void tick(long skip) {
		super.tick(skip);
		if (i != null) {
			List<Slot> slots = i.getSlots();
			if (slots.get(1).getItem() != null) {
				if (slots.get(0).getItem() != null) {
					long val = CraftingManager.getFurnaceRecipe(slots.get(0).getItemID());
					if (val == 0)
						return;
					int fuelTime = GameRegistry.getItemFuel(slots.get(1).getItemID());
					if (fuel <= 0 && fuelTime > 0) {
						slots.get(1).getItemStack().decreaseStack(1);
						if (slots.get(1).getItemStack().getAmountInStack() <= 0)
							slots.get(1).changeItem(null);
						fuel = fuelTime;
					}
					if (fuel > 0) {
						long temp = val << 32;
						int id = (int) (temp >> 32);
						int time = (int)(val >> 32);
						progress+=skip;
						if (progress < time)
							fuel--;
						if (progress >= time) {
							if (slots.get(2).getItem() == null) {
								slots.get(2).setItemStack(new ItemStack(Item.items.get((short)id), 1));
							} else {
								if (slots.get(2).getItemID() == id) {
									slots.get(2).getItemStack().increaseStack(1);
								} else
									return;
							}
							progress = 0;
							slots.get(0).getItemStack().decreaseStack(1);
							if (slots.get(0).getItemsAmount() <= 0)
								slots.get(0).changeItem(null);
						}
					}
				}
			}
		}
	}
	
}
