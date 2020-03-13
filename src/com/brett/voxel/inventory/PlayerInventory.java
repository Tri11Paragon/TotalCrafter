package com.brett.voxel.inventory;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.brett.renderer.gui.UIMaster;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class PlayerInventory {
	
	private Inventory i;
	
	public PlayerInventory(UIMaster ui) {
		i = new Inventory();
		for (int j = 0; j < 15; j++) {
			for (int k = 0; k < 7; k++) {
				i.addSlot(new Slot(j*64 + 5*j,k*64 + 5*k, 64, 64));
			}
		}
//		i.enable();
		ui.addMenu(i);
	}
	
	public void update() {
		if (Keyboard.next() && Keyboard.isKeyDown(Keyboard.KEY_E)) {
			i.toggleEnabled();
		}
	}
	
	public List<Slot> getSlots(){
		return i.getSlots();
	}
	
}
