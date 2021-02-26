package com.brett.voxel.inventory;

import org.joml.Vector2f;

import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.GUIRenderer;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class InventoryMaster {
	
	/**
	 * Inits all inventory stuff needed
	 */
	public static void init(Loader loader) {
		Slot.texture = loader.loadSpecialTexture("inventory/slot");
		Slot.hovertexture = loader.loadSpecialTexture("inventory/slothover");
		Hotbar.hoverTexture = Slot.hovertexture;
	}
	
	private static Vector2f pos = new Vector2f();
	/**
	 * renders any held items
	 */
	public static void render(GUIRenderer renderer) {
		renderer.startrender();
		if (PlayerSlot.getStack() != null) {
			if (PlayerSlot.getStack().getItem() != null) { // lol thats a lot of getters
				// render the item texture
				renderer.render(PlayerSlot.getStack().getItem().getTexture().getID(), (int)DisplayManager.mouseX, (int)DisplayManager.mouseY, 32, 32);
				// set its position based on mouse pos
				pos.x = (float)(DisplayManager.mouseX + 27)/(float)DisplayManager.WIDTH;
				pos.y = (float)(DisplayManager.mouseY + 29)/(float)DisplayManager.HEIGHT;
				// set the text position
				PlayerSlot.text.setPosition(pos);
				PlayerSlot.change();
			}
		}
		renderer.stoprender();
	}
	
}
