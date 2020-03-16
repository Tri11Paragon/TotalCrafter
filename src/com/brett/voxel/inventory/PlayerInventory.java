package com.brett.voxel.inventory;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.brett.IKeyState;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/
//TODO: make this a extension of inventory.
public class PlayerInventory implements IKeyState{
	
	private Inventory i;
	private Hotbar h;
	@SuppressWarnings("unused")
	private GUIRenderer rend;
	
	public PlayerInventory(UIMaster ui) {
		float sizeX = 64*15 + 5*15;
		float sizeY = 64*7 + 5*7;
		float x = Display.getWidth()/2 - sizeX/2;
		float y = Display.getHeight()/2 - sizeY/2;
		i = new Inventory(694, "player");
		h = new Hotbar(i, ui);
		for (int j = 0; j < 15; j++) {
			for (int k = 0; k < 7; k++) {
				i.addSlot(new Slot(x + (j*64 + 5*j),y + (k*64 + 5*k), 64, 64));
			}
		}
//		i.enable();
		i.loadInventory();
		ui.addMenu(i);
		ui.addMenu(h);
		this.rend = ui.getRenderer();
	}
	
	public void addItemToInventory(ItemStack i) {
		if (!h.addItemToInventorySimilar(i))
			if (!this.i.addItemToInventorySimilar(i))
				if (!this.h.addItemToInventory(i))
					this.i.addItemToInventory(i);
				
	}
	
	public ItemStack getItemInSelectedSlot() {
		return h.getItemSelected();
	}
	
	public Slot getSelectedSlot() {
		return h.getSelectedSlot();
	}
	
	public void update() {
		
	}
	
	public List<Slot> getSlots(){
		return i.getSlots();
	}
	
	public void saveInventory() {
		i.saveInventory();
	}

	@Override
	public void onKeyPressed() {
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			i.toggleEnabled();
		}
	}

	@Override
	public void onKeyReleased() {
	}
	
	public void cleanup() {
		h.saveInventory();
		i.saveInventory();
	}
	
}
