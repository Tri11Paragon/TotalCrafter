package com.brett.voxel.inventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.brett.renderer.Loader;
import com.brett.renderer.gui.GUIRenderer;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class InventoryMaster {
	
	/**
	 * Inits all inventory items
	 */
	public static void init(Loader loader) {
		Slot.texture = loader.loadSpecialTexture("inventory/slot");
		Slot.hovertexture = loader.loadSpecialTexture("inventory/slothover");
	}
	
	public static void render(GUIRenderer renderer) {
		renderer.startrender();
		if (PlayerSlot.itemInHand != null)
			if (PlayerSlot.itemInHand.getItem() != null) // lol thats a lot of getters
				renderer.render(PlayerSlot.itemInHand.getItem().getTexture().getID(), Mouse.getX(), Display.getHeight() - Mouse.getY(), 32, 32);
		renderer.stoprender();
	}
	
}
