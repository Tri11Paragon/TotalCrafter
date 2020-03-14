package com.brett.voxel.inventory;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.IMenu;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class Inventory implements IMenu {
	
	public Inventory() {
		
	}
	
	private List<Slot> slots = new ArrayList<Slot>();
	private List<UIElement> slotAsElements = new ArrayList<UIElement>();
	private boolean enabled = false;
	
	public void addSlot(Slot s) {
		slots.add(s);
		// this is VERY annoying that i have to do this
		// TODO: maybe use a better system so that we don't have to do this
		slotAsElements.add(s);
	}
	
	public void addItemToInventory(ItemStack i) {
		for (Slot s : slots) {
			if (s.getItem() == i.getItem()) {
				int amount = s.getItemStack().increaseStack(i.getAmountInStack());
				i.setStack(amount);
				s.updateText();
				if (amount == 0) {
					i = null;
					return;
				}
				continue;
			}
		}
		for (Slot s : slots) {
			if (s.getItemStack() == null) {
				s.setItemStack(i);
				s.updateText();
				return;
			}
		}
	}

	@Override
	public List<UIElement> render(UIMaster ui) {
		if (enabled) {
			return slotAsElements;
		}
		return null;
	}
	
	@Override
	public List<UIElement> secondardRender(UIMaster ui) {
		if (enabled) {
			GUIRenderer rend = ui.getRenderer();
			rend.startrender();
			for (Slot s : slots) {
				if (s.getItem() != null) {
					Vector2f size = adjustScale(s.getSc());
					Vector2f pos = adjustPos(s.getPos(), size);
					rend.render(s.getItem().getTexture().getID(), pos, size);
				}
			}
			rend.stoprender();
		}
		return null;
	}
	
	private Vector2f adjustPos(Vector2f pos, Vector2f scale) {
		return new Vector2f(pos.x + scale.x * 2, pos.y + scale.y * 2);
	}
	
	private Vector2f adjustScale(Vector2f v) {
		return new Vector2f(v.x/4, v.y/4);
	}
	
	@Override
	public void update() {
		if (enabled) {
			for (Slot s : slots)
				s.update();
		}
	}
	
	public List<Slot> getSlots(){
		return slots;
	}
	
	public void toggleEnabled() {
		enabled = !enabled;
		if (enabled) {
			Mouse.setGrabbed(false);
			for (Slot s : slots){
				if (s.text != null)
					s.text.enableText();;
			}
		}else {
			Mouse.setGrabbed(true);
			for (Slot s : slots) {
				if (s.text != null)
					s.text.disableText();;
			}
		}
	}
	
	public void enable() {
		enabled = true;
		for (Slot s : slots){
			if (s.text != null)
				s.text.enableText();
		}
	}
	
	public void disable() {
		enabled = false;
		for (Slot s : slots) {
			if (s.text != null)
				s.text.disableText();
		}
	}

}
