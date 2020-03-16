package com.brett.voxel.inventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

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
		Hotbar.hoverTexture = Slot.hovertexture;
	}
	
	private static Vector2f pos = new Vector2f();
	public static void render(GUIRenderer renderer) {
		renderer.startrender();
		if (PlayerSlot.getStack() != null) {
			if (PlayerSlot.getStack().getItem() != null) { // lol thats a lot of getters
				renderer.render(PlayerSlot.getStack().getItem().getTexture().getID(), Mouse.getX(), Display.getHeight() - Mouse.getY(), 32, 32);
				pos.x = (float)(Mouse.getX() + 27)/(float)Display.getWidth();
				pos.y = (float)(Display.getHeight() - Mouse.getY() + 29)/(float)Display.getHeight();
				PlayerSlot.text.setPosition(pos);
				PlayerSlot.change();
			}
		}
		renderer.stoprender();
	}
	
}
