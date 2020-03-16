package com.brett.voxel.inventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.brett.renderer.font.GUIDynamicText;
import com.brett.renderer.gui.UIButton;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/
public class Slot extends UIButton {
	
	public static int texture,hovertexture;
	protected GUIDynamicText text;
	
	private ItemStack stack;
	
	public Slot(float x, float y, float width, float height) {
		super(texture, hovertexture, null, x, y, width, height);
		text = new GUIDynamicText("", 0.8f, VoxelScreenManager.monospaced, calcVec(x+width-21, y+height-19), width/Display.getWidth(), false);
	}
	
	public ItemStack changeItem(ItemStack stack) {
		ItemStack old = this.stack;
		this.stack = stack;
		return old;
	}
	
	public int addItems(int i) {
		int amt = stack.increaseStack(i);
		text.changeTextNoUpdate(Integer.toString(stack.getAmountInStack()));
		return amt;
	}
	
	public int removeItems(int i) {
		int amt = stack.decreaseStack(i);
		text.changeTextNoUpdate(Integer.toString(stack.getAmountInStack()));
		if (stack.getAmountInStack() <= 0) {
			stack = null;
			text.changeTextNoUpdate("");
		}
		return amt;
	}
	
	public void updateText() {
		if (stack != null)
			text.changeText(Integer.toString(stack.getAmountInStack()));
		else
			text.changeText("");
	}
	
	public int getItemsAmount() {
		return stack.getAmountInStack();
	}
	
	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	public ItemStack getItemStack() {
		return stack;
	}
	
	public Item getItem() {
		if (stack == null)
			return null;
		return stack.getItem();
	}
	
	private boolean prevState;
	private boolean prevState2;
	public void update() {
		float mx = Mouse.getX();
		float my = Mouse.getY();
		my = Display.getHeight() - my;
		if (mx > px && mx < (px + pw)) {
			if (my > py && my < (py + ph)) {
				super.texture2 = ht;
				if (!prevState && Mouse.isButtonDown(0)) {
					if (stack == null) {
						if (PlayerSlot.getStack() != null) {
							stack = PlayerSlot.getStack();
							PlayerSlot.changeStack(null);
							text.changeText(Integer.toString(stack.getAmountInStack()));
						}
					} else {
						if (PlayerSlot.getStack() == null) {
							PlayerSlot.changeStack(stack);
							stack = null;
							text.changeText("");
						} else {
							if (PlayerSlot.getStack().getItem() == stack.getItem()) {
								int amt = stack.increaseStack(PlayerSlot.getStack().getAmountInStack());
								PlayerSlot.getStack().setStack(amt);
								PlayerSlot.change();
								if (amt == 0)
									PlayerSlot.changeStack(null);
								text.changeText(Integer.toString(stack.getAmountInStack()));
							} else {
								ItemStack s = PlayerSlot.getStack();
								PlayerSlot.changeStack(stack);
								stack = s;
								text.changeText(Integer.toString(stack.getAmountInStack()));
							}
						}
					}
				}
				if(!prevState2 && Mouse.isButtonDown(1)) {
					if (stack != null) {
						if(PlayerSlot.getStack() != null) {
							if (PlayerSlot.getStack().getItem() == stack.getItem()) {
								int amt = stack.increaseStack(PlayerSlot.getStack().getAmountInStack()/2);
								PlayerSlot.getStack().setStack(PlayerSlot.getStack().getAmountInStack()/2 + amt);
								PlayerSlot.change();
								if ((PlayerSlot.getStack().getAmountInStack()/2 + amt) == 0)
									PlayerSlot.changeStack(null);
								text.changeText(Integer.toString(stack.getAmountInStack()));
							}
						}
					}
				}
				prevState = Mouse.isButtonDown(0);
				prevState2 = Mouse.isButtonDown(1);
			} else {
				super.texture2 = -1;
			}
		} else {
			super.texture2 = -1;
		}
	}
	
}
