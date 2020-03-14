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
	
	public ItemStack(short id, int amount) {
		this.item = Item.items.get(id);
		this.amountInStack = amount;
	}
	
	public void setStack(int amount) {
		this.amountInStack = amount;
	}
	
	/**
	 * Returns the amount of items not added to the stack
	 */
	public int increaseStack(int amount) {
		int last = amountInStack;
		if (amountInStack + amount > stackSize) {
			amountInStack = stackSize;
			return (amount + last) - stackSize;
		}
		amountInStack += amount;
		return 0;
	}
	
	public int decreaseStack(int amount) {
		if ((amountInStack - amount) < 0) {
			int returned = amount - amountInStack;
			amountInStack = 0;
			return returned;
		}
		amountInStack -= amount;
		return 0;
	}
	
	public int getAmountInStack() {
		return amountInStack;
	}
	
	public Item getItem() {
		return item;
	}
	
}
