package com.brett.world.item;

import java.util.HashMap;
import java.util.Map;

import com.brett.world.block.Block;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class Item {
	
	public static Map<Short, Item> items = new HashMap<Short, Item>();
	public static Map<Item, Short> inverseItems = new HashMap<Item, Short>();
	public static Map<Item, Block> itemBlocks = new HashMap<Item, Block>();
	
	public short id;
	public int textureID;
	
	public Item(short id, int textureID) {
		this.id = id;
		this.textureID = textureID;
	}
	
	public short getId() {
		return id;
	}
	
}
