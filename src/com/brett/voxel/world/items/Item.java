package com.brett.voxel.world.items;

import java.util.HashMap;
import java.util.Map;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.world.blocks.Block;

/**
*
* @author brett
* @date Mar. 10, 2020
*/

public class Item {
	
	public static Map<Short, Item> items = new HashMap<Short, Item>();
	private static Map<Item, Block> itemBlocks = new HashMap<Item, Block>();
	
	private ModelTexture texture;
	private short id;
	
	public Item(short id, ModelTexture texture) {
		this.id = id;
		this.texture = texture;
	}
	
	public static void registerItems(Loader loader) {
		
	}
	
	public ModelTexture getTexture() {
		return texture;
	}

	public short getId() {
		return id;
	}

	protected void registerItem(short id, ModelTexture texture) {
		items.put(id, new Item(id, texture));
	}
	
	protected Item registerItemBlock(short id, Block b) {
		Item i = new Item(id, b.model);
		itemBlocks.put(i, b);
		items.put(id, i);
		return i;
	}
	
	protected Item registerItemBlock(short id, short blockID) {
		Item i = new Item(id, Block.blocks.get(blockID).model);
		itemBlocks.put(i, Block.blocks.get(blockID));
		items.put(id, i);
		return i;
	}
	
}
