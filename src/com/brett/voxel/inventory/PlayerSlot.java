package com.brett.voxel.inventory;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.font.UIDynamicText;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class PlayerSlot {
	
	private static ItemStack itemInHand;
	public static UIDynamicText text = new UIDynamicText("", 0.8f, VoxelScreenManager.monospaced, new Vector2f(Mouse.getX(), Mouse.getY()), 1.0f, false);
	
	
	public static ItemStack getStack() {
		return itemInHand;
	}
	
	public static void change() {
		if (itemInHand != null)
			text.changeText(Integer.toString(itemInHand.getAmountInStack()));
	}
	
	public static void changeStack(ItemStack s) {
		if (s == null) {
			text.disableText();
			itemInHand = null;
			return;
		}
		text.changeText(Integer.toString(s.getAmountInStack()));
		if (!text.getEnabled())
			text.enableText();
		itemInHand = s;
	}
	
}
