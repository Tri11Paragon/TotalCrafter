package com.brett.voxel.world.items;

/**
*
* @author brett
* @date Mar. 11, 2020
*/

public class ItemStack {
	
	public static final int stackSize = 128;
	
	private int amountInStack;
	private Item item;
	
	public ItemStack(Item i, int amount) {
		this.item = i;
		this.amountInStack = amount;
	}
	
	public boolean decreaseStack(int amount) {
		if ((amountInStack - amount) < 0) {
			amountInStack = 0;
			return false;
		}
		amountInStack -= amount;
		return true;
	}
	
	public int getAmountInStack() {
		return amountInStack;
	}
	
	public Item getItem() {
		return item;
	}
	
}
