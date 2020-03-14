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
	
	/**
	 * Constructors
	 */
	public ItemStack(Item i, int amount) {
		this.item = i;
		this.amountInStack = amount;
	}
	
	public ItemStack(short id, int amount) {
		this.item = Item.items.get(id);
		this.amountInStack = amount;
	}
	
	/**
	 * sets the amount in the stack
	 */
	public void setStack(int amount) {
		this.amountInStack = amount;
	}
	
	/**
	 * adds to the stack
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
	
	/**
	 * Decreases the amount in this stack
	 * returns the amount leftover if removing all
	 * results in a negative stack value
	 */
	public int decreaseStack(int amount) {
		if ((amountInStack - amount) < 0) {
			int returned = amount - amountInStack;
			amountInStack = 0;
			return returned;
		}
		amountInStack -= amount;
		return 0;
	}
	
	/**
	 * returns amount in stack
	 */
	public int getAmountInStack() {
		return amountInStack;
	}
	
	public Item getItem() {
		return item;
	}
	
}
