package com.brett.voxel.inventory;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class PlayerInventory {
	
	private Inventory i;
	@SuppressWarnings("unused")
	private GUIRenderer rend;
	public Slot heldSlot;
	
	public PlayerInventory(UIMaster ui) {
		float sizeX = 64*15 + 5*15;
		float sizeY = 64*7 + 5*7;
		float x = Display.getWidth()/2 - sizeX/2;
		float y = Display.getHeight()/2 - sizeY/2;
		i = new Inventory();
		for (int j = 0; j < 15; j++) {
			for (int k = 0; k < 7; k++) {
				i.addSlot(new Slot(x + (j*64 + 5*j),y + (k*64 + 5*k), 64, 64));
			}
		}
		heldSlot = new Slot(Display.getWidth()/2 - 35, sizeY + 128, 64, 64);
		i.addSlot(heldSlot);
//		i.enable();
		ui.addMenu(i);
		this.rend = ui.getRenderer();
	}
	
	public void addItemToInventory(ItemStack i) {
		this.i.addItemToInventory(i);
	}
	
	private boolean state = false;
	public void update() {
		state = Keyboard.next();
		//if (state && Keyboard.isKeyDown(Keyboard.KEY_T)) {
		//	i.addItemToInventory(new ItemStack(Item.items.get((short)1), 10));
		//}
		if (state && Keyboard.isKeyDown(Keyboard.KEY_E)) {
			i.toggleEnabled();
		}
	}
	
	public List<Slot> getSlots(){
		return i.getSlots();
	}
	
}
