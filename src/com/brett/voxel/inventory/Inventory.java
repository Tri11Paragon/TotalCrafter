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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import com.brett.renderer.datatypes.GUITexture;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.IMenu;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.gui.MainMenu;
import com.brett.voxel.networking.PACKETS;
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
*/

public class Inventory implements IMenu {
	
	private List<Slot> slots = new ArrayList<Slot>();
	private List<UIElement> slotAsElements = new ArrayList<UIElement>();
	private boolean enabled = false;
	public final String NBTID;
	
	public Inventory(int seed) {
		StringBuilder b = new StringBuilder();
		Random r = new Random(seed);
		for(int i = 0; i < 10; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		if (VoxelWorld.isRemote) {
			b.append(MainMenu.username);
			NBTID = b.toString();
			VoxelWorld.localClient.inventories.add(this);
			VoxelWorld.localClient.requestInventory(NBTID);
		} else
			NBTID = b.toString();
	}
	
	public Inventory(String NBTID) {
		if (VoxelWorld.isRemote) {
			this.NBTID = NBTID + MainMenu.username;
			VoxelWorld.localClient.inventories.add(this);
			VoxelWorld.localClient.requestInventory(NBTID);
		} else
			this.NBTID = NBTID;
	}
	
	public Inventory(int seed, String NBTID) {
		StringBuilder b = new StringBuilder();
		b.append(NBTID);
		Random r = new Random(seed);
		for(int i = 0; i < 5; i++) {
			b.append((int)Math.abs(r.nextInt(10)));
		}
		if (VoxelWorld.isRemote) {
			b.append(MainMenu.username);
			this.NBTID = b.toString();
			VoxelWorld.localClient.inventories.add(this);
			VoxelWorld.localClient.requestInventory(NBTID);
		} else
			this.NBTID = b.toString();
	}
	
	public void setBackground(GUITexture texture) {
		slotAsElements.add(texture);
	}
	
	public void addSlot(Slot s) {
		slots.add(s);
		// this is VERY annoying that i have to do this
		// yes i am aware of typeof / instanceof
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
					if (VoxelWorld.localClient != null)
						VoxelWorld.localClient.sendInventory(this);
					return true;
				}
				continue;
			}
		}
		for (Slot s : slots) {
			if (s.getItemStack() == null) {
				s.setItemStack(i);
				s.updateText();
				if (VoxelWorld.localClient != null)
					VoxelWorld.localClient.sendInventory(this);
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
					if (VoxelWorld.localClient != null)
						VoxelWorld.localClient.sendInventory(this);
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
		return new Vector2f(pos.x + scale.x * 3/2, pos.y + scale.y * 3/2);
	}
	
	private Vector2f adjustScale(Vector2f v) {
		return new Vector2f(v.x/3, v.y/3);
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
		if (VoxelWorld.localClient != null)
			VoxelWorld.localClient.requestInventory(NBTID);
		enabled = !enabled;
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
	
	public void enable() {
		if (VoxelWorld.localClient != null)
			VoxelWorld.localClient.requestInventory(NBTID);
		enabled = true;
		for (Slot s : slots){
			if (s.text != null)
				s.text.enableText();
		}
	}
	
	public void disable() {
		if (VoxelWorld.localClient != null)
			VoxelWorld.localClient.sendInventory(this);
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
			String world = ChunkStore.worldLocation;
			if (Server.server != null)
				world = ServerWorld.worldLocation;
			if (VoxelWorld.isRemote) {
				if (MainMenu.ip.trim().length() < 1)
					MainMenu.ip = "localhost";
				world = "worlds/servers/" + MainMenu.username + "/" + MainMenu.ip + "/";
				new File(world).mkdirs();
			}
			is = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(world + NBTID + ".dat")));
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
	
	public byte[] serialize() {
		byte[] nbtstr = NBTID.getBytes();
		ByteBuffer buff = ByteBuffer.allocate(6 + 4*slots.size() + 4 * slots.size() + nbtstr.length + 4);
		buff.put(PACKETS.INVENTORYSEND);
		if (VoxelWorld.localClient != null)
			buff.putInt(VoxelWorld.localClient.id);
		else
			buff.putInt(0);
		buff.putInt(slots.size());
		for (int i = 0; i < slots.size(); i++) {
			Slot s = slots.get(i);
			if (s.getItemStack() == null) {
				buff.putInt(-1);
				buff.putInt(-1);
			} else {
				buff.putInt(s.getItemID());
				buff.putInt(s.getItemsAmount());
			}
		}
		buff.put(Byte.MIN_VALUE);
		for (int i = 0; i < nbtstr.length; i++) {
			buff.put(nbtstr[i]);
		}
		return buff.array();
	}
	
	public void deserialize(byte[] bytes) {
		ByteBuffer buff = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 6000));
		buff.get();
		buff.getInt();
		int size = buff.getInt();
		for (int i = 0; i < size; i++) {
			int id = buff.getInt();
			int amount = buff.getInt();
			if (id > 0) {
				ItemStack st = new ItemStack(Item.items.get((short)id), amount);
				slots.get(i).setItemStack(st);
			}
		}
	}
	
	public void deserialize(byte[] bytes, boolean b) {
		ByteBuffer buff = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 6000));
		buff.get();
		buff.getInt();
		int size = buff.getInt();
		for (int i = 0; i < size; i++) {
			slots.add(new Slot(0,0,0,0));
		}
		for (int i = 0; i < size; i++) {
			int id = buff.getInt();
			int amount = buff.getInt();
			if (id > 0) {
				ItemStack st = new ItemStack(Item.items.get((short)id), amount);
				slots.get(i).setItemStack(st);
			}
		}
	}
	
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
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
