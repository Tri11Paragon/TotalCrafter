package com.brett.voxel.inventory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.IMenu;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/

public class Inventory implements IMenu {
	
	private final String NBTID;
	
	public Inventory(int seed) {
		StringBuilder b = new StringBuilder();
		Random r = new Random(seed);
		for(int i = 0; i < 30; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		NBTID = b.toString();
	}
	
	public Inventory(String NBTID) {
		this.NBTID = NBTID;
	}
	
	public Inventory(int seed, String NBTID) {
		StringBuilder b = new StringBuilder();
		b.append(NBTID);
		Random r = new Random(seed);
		for(int i = 0; i < 30; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		this.NBTID = b.toString();
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
	
	public boolean addItemToInventory(ItemStack i) {
		for (Slot s : slots) {
			if (s.getItem() == i.getItem()) {
				int amount = s.getItemStack().increaseStack(i.getAmountInStack());
				i.setStack(amount);
				s.updateText();
				if (amount == 0) {
					i = null;
					return true;
				}
				continue;
			}
		}
		for (Slot s : slots) {
			if (s.getItemStack() == null) {
				s.setItemStack(i);
				s.updateText();
				return true;
			}
		}
		return false;
	}
	
	public boolean addItemToInventorySimilar(ItemStack i) {
		for (Slot s : slots) {
			if (s.getItem() == i.getItem()) {
				int amount = s.getItemStack().increaseStack(i.getAmountInStack());
				i.setStack(amount);
				s.updateText();
				if (amount == 0) {
					i = null;
					return true;
				}
				continue;
			}
		}
		return false;
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
	
	public void saveInventory() {
		System.out.println("Saving " + NBTID + " Inventory");
		DataOutputStream is = null;
		try {
			is = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(ChunkStore.worldLocation + "/" + NBTID + ".dat")));
		} catch (FileNotFoundException e) {return;}
		try {
			
			for (int i = 0; i < slots.size(); i++) {
				is.writeInt(i);
				Slot s = slots.get(i);
				if(s.getItemStack() != null) {
					is.writeShort(Item.inverseItems.get(s.getItem()));
					is.writeInt(s.getItemsAmount());
				} else {
					is.writeShort(-1);
					is.writeInt(0);
				}
			}
			
			is.close();
		} catch (IOException e) {}
		System.out.println("Inventory Saved.");
	}
	
	public void loadInventory() {
		DataInputStream is = null;
		try {
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(ChunkStore.worldLocation + "/" + NBTID + ".dat")));
		} catch (FileNotFoundException e) {return;}
		try {
			
			for (int i = 0; i < slots.size(); i++) {
				is.readInt();
				Slot s = slots.get(i);
				short id = is.readShort();
				int amt = is.readInt();
				if (id < 0)
					continue;
				s.setItemStack(new ItemStack(Item.items.get(id), amt));
				s.updateText();
			}
			
			is.close();
		} catch (IOException e) {}
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
