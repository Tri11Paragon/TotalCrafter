package com.brett.voxel.inventory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.datatypes.GUITexture;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.IMenu;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.gui.MainMenu;
import com.brett.voxel.networking.server.Server;
import com.brett.voxel.networking.server.ServerWorld;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
* the inventory base class!
*/

public class Inventory implements IMenu, Serializable {
	
	private static final long serialVersionUID = -5462596077735511349L;
	
	private List<Slot> slots = new ArrayList<Slot>();
	private List<UIElement> slotAsElements = new ArrayList<UIElement>();
	private boolean enabled = false;
	public final String NBTID;
	
	public Inventory(int seed) {
		StringBuilder b = new StringBuilder();
		Random r = new Random(seed);
		// create the ID for this inventory.
		for(int i = 0; i < 10; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		if (VoxelWorld.isRemote) {
			b.append(MainMenu.username);
			NBTID = b.toString();
			// connect to server
			VoxelWorld.localClient.inventories.add(this);
		} else
			NBTID = b.toString();
	}
	
	public Inventory(String NBTID) {
		if (VoxelWorld.isRemote) {
			// connect to server
			this.NBTID = NBTID + MainMenu.username;
			VoxelWorld.localClient.inventories.add(this);
		} else
			this.NBTID = NBTID;
	}
	
	public Inventory(int seed, String NBTID) {
		StringBuilder b = new StringBuilder();
		b.append(NBTID);
		Random r = new Random(seed);
		// create NBT id.
		for(int i = 0; i < 5; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		if (VoxelWorld.isRemote) {
			b.append(MainMenu.username);
			this.NBTID = b.toString();
			// connect to server.
			VoxelWorld.localClient.inventories.add(this);
		} else
			this.NBTID = b.toString();
	}
	
	/**
	 * sets the inventory background
	 */
	public void setBackground(GUITexture texture) {
		slotAsElements.add(texture);
	}
	
	/**
	 * adds slot to this inventory.
	 */
	public void addSlot(Slot s) {
		slots.add(s);
		// this is VERY annoying that i have to do this
		// yes i am aware of typeof / instanceof
		// TODO: maybe use a better system so that we don't have to do this
		slotAsElements.add(s);
	}
	
	/**
	 * Adds item to the inventory. If there is similar it will add to that stack
	 * if there is no similar it will add to a null slot. if there is no open slot then
	 * it will return false. Returns true when item has been added.
	 */
	public boolean addItemToInventory(ItemStack i) {
		// adds and item into the inventory.
		for (Slot s : slots) {
			// adds item if they are similar.
			if (s.getItem() == i.getItem()) {
				int amount = s.getItemStack().increaseStack(i.getAmountInStack());
				// sets the stack's amount to the amount that was unable to be added.
				i.setStack(amount);
				s.updateText();
				if (amount == 0) {
					i = null;
					// returns true when there is no items left to be added
					return true;
				}
				continue;
			}
		}
		// why do this here? well we need to check the whole inventory for similar before adding new
		for (Slot s : slots) {
			// adds item only if they are different.
			if (s.getItemStack() == null) {
				s.setItemStack(i);
				s.updateText();
				return true;
			}
		}
		// return false if no more items can be added.
		return false;
	}
	
	/**
	 * Adds item to the inventory only if there is a similar item.
	 */
	public boolean addItemToInventorySimilar(ItemStack i) {
		// save as above but only does similar. I don't know why I thought
		// this was a good idea or if anything uses it but
		// whatever *sniff*
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
					// render the item textures as secondary objects on top
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
		return new Vector2f(pos.x + scale.x * 3/2, pos.y + scale.y * 3/2);
	}
	
	private Vector2f adjustScale(Vector2f v) {
		return new Vector2f(v.x/3, v.y/3);
	}
	
	@Override
	public void update() {
		if (enabled) {
			// update all the slots if this inventory is enabled.
			for (Slot s : slots)
				s.update();
		}
	}
	
	public List<Slot> getSlots(){
		return slots;
	}
	
	public void toggleEnabled() {
		// toggles the inventory.
		enabled = !enabled;
		// enables / disables text.
		if (enabled) {
			Mouse.setGrabbed(false);
			for (Slot s : slots){
				if (s.text != null)
					s.text.enableText();
			}
		}else {
			Mouse.setGrabbed(true);
			for (Slot s : slots) {
				if (s.text != null)
					s.text.disableText();
			}
		}
	}
	
	/**
	 * Enable the inventory.
	 */
	public void enable() {
		enabled = true;
		for (Slot s : slots){
			if (s.text != null)
				s.text.enableText();
		}
	}
	
	/**
	 * Disable the inventory.
	 */
	public void disable() {
		enabled = false;
		for (Slot s : slots) {
			if (s.text != null)
				s.text.disableText();
		}
	}
	
	/**
	 * Saves the inventory.
	 */
	public void saveInventory() {
		System.out.println("Saving " + NBTID + " Inventory");
		DataOutputStream is = null;
		try {
			// saves based on if we are the server or if we are connected to a server
			// or if we are playing single player.
			// "the proof is trivial and left as an exercise to the reader."
			String world = ChunkStore.worldLocation;
			if (Server.server != null)
				world = ServerWorld.worldLocation;
			if (VoxelWorld.isRemote) {
				if (MainMenu.ip.trim().length() < 1)
					MainMenu.ip = "localhost";
				world = "worlds/servers/" + MainMenu.username + "/" + MainMenu.ip + "/";
				new File(world).mkdirs();
				new File(world + NBTID + ".dat").mkdirs();
			}
			is = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(world + NBTID + ".dat")));
		} catch (FileNotFoundException e) {return;}
		try {
			// write all the stack data to the inventory file.
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
			// saves loads on if we are the server or if we are connected to a server
			// or if we are playing single player.
			// "the proof is trivial and left as an exercise to the reader."
			String world = ChunkStore.worldLocation;
			if (Server.server != null)
				world = ServerWorld.worldLocation;
			if (VoxelWorld.isRemote) {
				if (MainMenu.ip.trim().length() < 1)
					MainMenu.ip = "localhost";
				world = "worlds/servers/" + MainMenu.username + "/" + MainMenu.ip + "/";
				new File(world).mkdirs();
			}
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(world + NBTID + ".dat")));
		} catch (FileNotFoundException e) {return;}
		try {
			// since the inventory size isn't going to change then we can just loop through all the
			// slots and try to fill them with items
			for (int i = 0; i < slots.size(); i++) {
				is.readInt();
				Slot s = slots.get(i);
				short id = is.readShort();
				int amt = is.readInt();
				// make sure we don't add the ones we only saved for order (they have no items)
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
	
	/**
	 * DOESN'T WORK / UNUSED / MIGHT WORK
	 */
    public static StringBuilder dataToString(byte[] a) { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        for (int i = 0; i < a.length; i++) {
        	if (a[i] == 0)
        		break;
            ret.append((char) a[i]); 
        } 
        return ret; 
    } 
	
}
