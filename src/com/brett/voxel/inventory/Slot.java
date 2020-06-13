package com.brett.voxel.inventory;

import java.io.Serializable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.gui.UIButton;
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
	protected UIDynamicText text;
	private String name = "";
	private SlotChange sc;
	
	private ItemStack stack;
	
	public Slot(float x, float y, float width, float height) {
		super(texture, hovertexture, null, x, y, width, height);
		text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, calcVec(x+width-21, y+height-19), width/Display.getWidth(), false);
	}
	
	public Slot(String name, float x, float y, float width, float height) {
		super(texture, hovertexture, null, x, y, width, height);
		text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, calcVec(x+width-21, y+height-19), width/Display.getWidth(), false);
		this.name = name;
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
		if (stack == null)
			return 0;
		int amt = stack.decreaseStack(i);
		text.changeText(Integer.toString(stack.getAmountInStack()));
		if (stack.getAmountInStack() <= 0) {
			stack = null;
			text.changeText("");
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
		if (stack != null)
			return stack.getAmountInStack();
		else
			return 0;
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
	
	public int getItemID() {
		if (stack == null)
			return 0;
		return stack.getItem().getId();
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
						if (PlayerSlot.getStack() != null && !name.contains("o")) {
							stack = PlayerSlot.getStack();
							PlayerSlot.changeStack(null);
							text.changeText(Integer.toString(stack.getAmountInStack()));
							if (sc != null)
								sc.onChange(this);
						}
					} else {
						if (PlayerSlot.getStack() == null) {
							PlayerSlot.changeStack(stack);
							stack = null;
							text.changeText("");
							if (sc != null)
								sc.onChange(this);
						} else {
							if (name.contains("o")) {
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									int amt = PlayerSlot.getStack().increaseStack(stack.getAmountInStack());
									PlayerSlot.change();
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
								}
							} else {
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									int amt = stack.increaseStack(PlayerSlot.getStack().getAmountInStack());
									PlayerSlot.getStack().setStack(amt);
									PlayerSlot.change();
									if (amt == 0)
										PlayerSlot.changeStack(null);
									text.changeText(Integer.toString(stack.getAmountInStack()));
									if (sc != null)
										sc.onChange(this);
								} else {
									ItemStack s = PlayerSlot.getStack();
									PlayerSlot.changeStack(stack);
									stack = s;
									text.changeText(Integer.toString(stack.getAmountInStack()));
									if (sc != null)
										sc.onChange(this);
								}
							}
						}
					}
				}
				if(!prevState2 && Mouse.isButtonDown(1)) {
					if (stack != null) {
						if (!name.contains("o")) {
							if(PlayerSlot.getStack() != null) {
								if (PlayerSlot.getStack().getItem() == stack.getItem()) {
									stack.increaseStack(1);
									PlayerSlot.getStack().decreaseStack(1);
									if (PlayerSlot.getStack().getAmountInStack() < 1)
										PlayerSlot.changeStack(null);
									PlayerSlot.change();
									text.changeText(Integer.toString(stack.getAmountInStack()));
									if (sc != null)
										sc.onChange(this);
								}
							} else {
								PlayerSlot.changeStack(new ItemStack(stack.getItem(), stack.getAmountInStack()/2));
								stack.decreaseStack(stack.getAmountInStack()/2);
								if ((PlayerSlot.getStack().getAmountInStack()) <= 0)
									PlayerSlot.changeStack(null);
								PlayerSlot.change();
								if (stack.getAmountInStack() <= 0)
									stack = null;
								if (stack != null)
									text.changeText(Integer.toString(stack.getAmountInStack()));
								if (sc != null)
									sc.onChange(this);
							}
						}
					} else {
						if(PlayerSlot.getStack() != null && !name.contains("o")) {
							stack = new ItemStack(PlayerSlot.getStack().getItem(), 1);
							PlayerSlot.getStack().decreaseStack(1);
							if (PlayerSlot.getStack().getAmountInStack() < 1)
								PlayerSlot.changeStack(null);
							PlayerSlot.change();
							text.changeText(Integer.toString(stack.getAmountInStack()));
							if (sc != null)
								sc.onChange(this);
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

	public Slot setSc(SlotChange sc) {
		this.sc = sc;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
