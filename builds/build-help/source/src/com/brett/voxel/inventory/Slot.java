package com.brett.voxel.inventory;

import java.io.Serializable;

import com.brett.DisplayManager;

import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.gui.UIButton;
import com.brett.tools.InputMaster;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/
public class Slot extends UIButton implements Serializable {
	
	private static final long serialVersionUID = -7434485717414603002L;
	
	public static int texture,hovertexture;
	
	protected transient UIDynamicText text;
	private transient SlotChange sc;
	
	private String name = "";
	private boolean slotChanged = false;
	
	// items stack in the slot
	private ItemStack stack;
	
	public Slot(float x, float y, float width, float height) {
		super(texture, hovertexture, null, x, y, width, height);
		text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, calcVec(x+width-21, y+height-19), width/DisplayManager.WIDTH, false);
	}
	
	public Slot(String name, float x, float y, float width, float height) {
		super(texture, hovertexture, null, x, y, width, height);
		text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, calcVec(x+width-21, y+height-19), width/DisplayManager.WIDTH, false);
		this.name = name;
	}
	
	/**
	 * changes the item stack in the slot
	 * returns the old item stack
	 */
	public ItemStack changeItem(ItemStack stack) {
		slotChanged = true;
		ItemStack old = this.stack;
		this.stack = stack;
		return old;
	}
	
	/**
	 * add a number of items to the current stack
	 */
	public int addItems(int i) {
		if (stack == null)
			return i;
		// increase the stack and return the amount not able to be added
		slotChanged = true;
		int amt = stack.increaseStack(i);
		text.changeTextNoUpdate(Integer.toString(stack.getAmountInStack()));
		return amt;
	}
	
	/**
	 * removes item from the stack
	 * returns the amount unable to be removed
	 */
	public int removeItems(int i) {
		if (stack == null)
			return 0;
		slotChanged = true;
		// decrease the items, update text and return the amount not able to be removed.
		int amt = stack.decreaseStack(i);
		text.changeText(Integer.toString(stack.getAmountInStack()));
		// remove the stack if there is no items
		if (stack.getAmountInStack() <= 0) {
			stack = null;
			text.changeText("");
		}
		return amt;
	}
	
	/**
	 * updates the slot text
	 */
	public void updateText() {
		if (stack != null)
			text.changeText(Integer.toString(stack.getAmountInStack()));
		else
			text.changeText("");
	}
	
	/**
	 * gets the amount of items in a stack
	 */
	public int getItemsAmount() {
		if (stack != null)
			return stack.getAmountInStack();
		else
			return 0;
	}
	
	/**
	 * sets the item stack
	 */
	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	/**
	 * gets the item stack
	 */
	public ItemStack getItemStack() {
		return stack;
	}
	
	/**
	 * gets the item. returns null if there is none
	 */
	public Item getItem() {
		if (stack == null)
			return null;
		return stack.getItem();
	}
	
	/**
	 * gets the item id, if there is none then it returns 0
	 */
	public int getItemID() {
		if (stack == null)
			return 0;
		return stack.getItem().getId();
	}
	
	private boolean prevState;
	private boolean prevState2;
	public void update() {
		float mx = (float)DisplayManager.mouseX;
		float my = (float)DisplayManager.mouseY;
		// adjusts for mouse pos being in bottom coner instead of top
		//my = DisplayManager.HEIGHT - my;
		// make sure we are over the slot
		if (mx > px && mx < (px + pw)) {
			if (my > py && my < (py + ph)) {
				// change to hover texture
				super.texture2 = ht;
				// make sure we are only pressing the button once
				if (!prevState && InputMaster.mouseDown[0]) {
					if (stack == null) {
						// if there is item in the player hand then add it to the slot
						if (PlayerSlot.getStack() != null && !name.contains("o")) {
							stack = PlayerSlot.getStack();
							// remove from the player slot
							PlayerSlot.changeStack(null);
							// update text and stuff
							text.changeText(Integer.toString(stack.getAmountInStack()));
							slotChanged = true;
							if (sc != null)
								sc.onChange(this);
						}
					} else {
						// if there is no item in the player hand then remove item
						// from this slot and put it in the hand
						if (PlayerSlot.getStack() == null) {
							PlayerSlot.changeStack(stack);
							stack = null;
							// update the text
							text.changeText("");
							slotChanged = true;
							if (sc != null)
								sc.onChange(this);
						} else {
							// if we are an output slot we don't want items able to be placed in us.
							if (name.contains("o")) {
								// if the item in the player hand is the same then we add the item in the slot to the hand
								// if we can
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									// increase the player stack
									int amt = PlayerSlot.getStack().increaseStack(stack.getAmountInStack());
									PlayerSlot.change();
									// update the text in the slot with how much is left in it
									if (amt == 0) {
										this.stack = null;
										text.changeText("");
										if (sc != null)
											sc.onChange(this);
									} else {
										stack.setStack(amt);
										text.changeText(Integer.toString(stack.getAmountInStack()));
										//if (sc != null)
											//sc.onChange(this);
									}
									slotChanged = true;
								}
							} else {
								// if the item in the player hand is the same then we add the item amount in the slot to the hand
								// if we can
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									int amt = stack.increaseStack(PlayerSlot.getStack().getAmountInStack());
									PlayerSlot.getStack().setStack(amt);
									PlayerSlot.change();
									// update the player slot and change the text
									if (amt == 0)
										PlayerSlot.changeStack(null);
									text.changeText(Integer.toString(stack.getAmountInStack()));
									if (sc != null)
										sc.onChange(this);
								} else {
									// if its not the same then we will swap the items the in slot
									ItemStack s = PlayerSlot.getStack();
									PlayerSlot.changeStack(stack);
									stack = s;
									// update the texts.
									text.changeText(Integer.toString(stack.getAmountInStack()));
									if (sc != null)
										sc.onChange(this);
								}
								slotChanged  = true;
							}
						}
					}
				}
				// make sure we are only running once per button press.
				if(!prevState2 && InputMaster.mouseDown[1]) {
					// make sure the stack exists
					if (stack != null) {
						// make sure we are not an output slot
						if (!name.contains("o")) {
							if(PlayerSlot.getStack() != null) {
								// if the player has items and the stack has items that are the same
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									// we decrease the player stack and increase the stack
									stack.increaseStack(1);
									PlayerSlot.getStack().decreaseStack(1);
									// remove 0 stacks
									if (PlayerSlot.getStack().getAmountInStack() < 1)
										PlayerSlot.changeStack(null);
									// update the text
									PlayerSlot.change();
									text.changeText(Integer.toString(stack.getAmountInStack()));
									slotChanged = true;
									if (sc != null)
										sc.onChange(this);
								}
							} else {
								// take half of the stack in the slot and put it in the players hand.
								PlayerSlot.changeStack(new ItemStack(stack.getItem(), stack.getAmountInStack()/2));
								stack.decreaseStack(stack.getAmountInStack()/2);
								// remove 0 stacks
								if ((PlayerSlot.getStack().getAmountInStack()) <= 0)
									PlayerSlot.changeStack(null);
								// update the text
								PlayerSlot.change();
								if (stack.getAmountInStack() <= 0)
									stack = null;
								if (stack != null)
									text.changeText(Integer.toString(stack.getAmountInStack()));
								slotChanged = true;
								if (sc != null)
									sc.onChange(this);
							}
						}
					} else {
						// make sure we are not an output and that there is a stack in the players hand
						if(PlayerSlot.getStack() != null && !name.contains("o")) {
							// if so then add one to the this slot
							stack = new ItemStack(PlayerSlot.getStack().getItem(), 1);
							// remove one from the player slot
							PlayerSlot.getStack().decreaseStack(1);
							// remove 0 stacks and update text.
							if (PlayerSlot.getStack().getAmountInStack() < 1)
								PlayerSlot.changeStack(null);
							PlayerSlot.change();
							text.changeText(Integer.toString(stack.getAmountInStack()));
							slotChanged = true;
							if (sc != null)
								sc.onChange(this);
						}
					}
				}
				prevState = InputMaster.mouseDown[0];
				prevState2 = InputMaster.mouseDown[1];
			} else {
				// no more hover texture
				super.texture2 = -1;
			}
		} else {
			super.texture2 = -1;
		}
	}
	
	public boolean getSlotChanged() {
		if(slotChanged) {
			slotChanged = false;
			return true;
		}
		return false;
	}

	public Slot setSc(SlotChange sc) {
		this.sc = sc;
		return this;
	}

	/**
	 * gets the named identifier of this slot
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
