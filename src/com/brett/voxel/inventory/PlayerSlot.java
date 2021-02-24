package com.brett.voxel.inventory;

import java.io.Serializable;

import org.joml.Vector2f;

import com.brett.DisplayManager;
import com.brett.renderer.font.UIDynamicText;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
* Slot for what's in the players hand
*/

public class PlayerSlot implements Serializable {
	
	private static final long serialVersionUID = -4508178413249010682L;
	
	private static ItemStack itemInHand;
	public static UIDynamicText text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, new Vector2f((float)DisplayManager.mouseX, (float)DisplayManager.mouseY), 1.0f, false);
	
	
	public static ItemStack getStack() {
		return itemInHand;
	}
	
	/**
	 * changes the text around the mouse indicating the size of the stack
	 */
	public static void change() {
		if (itemInHand != null)
			text.changeText(Integer.toString(itemInHand.getAmountInStack()));
	}
	
	/**
	 * changes the item stack in hand.
	 */
	public static void changeStack(ItemStack s) {
		if (s == null) {
			text.disableText();
			itemInHand = null;
			return;
		}
		// update the text
		text.changeText(Integer.toString(s.getAmountInStack()));
		if (!text.getEnabled())
			text.enableText();
		// change the item
		itemInHand = s;
	}
	
}
