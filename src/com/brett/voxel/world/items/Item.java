package com.brett.voxel.world.items;

import java.util.HashMap;
import java.util.Map;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.world.blocks.Block;

/**
*
* @author brett
* @date Mar. 10, 2020
*/

public class Item {
	
	public static Map<Short, Item> items = new HashMap<Short, Item>();
	public static Map<Item, Short> inverseItems = new HashMap<Item, Short>();
	public static Map<Item, Block> itemBlocks = new HashMap<Item, Block>();
	
	private ModelTexture texture;
	private short id;
	private int miningLevel = 0;
	private float miningSpeed = 0.2f;
	
	public Item(short id, ModelTexture texture) {
		this.id = id;
		this.texture = texture;
	}
	
	public ModelTexture getTexture() {
		return texture;
	}

	public short getId() {
		return id;
	}

	public int getMiningLevel() {
		return miningLevel;
	}

	public Item setMiningLevel(int miningLevel) {
		this.miningLevel = miningLevel;
		return this;
	}

	public float getMiningSpeed() {
		return miningSpeed;
	}

	public Item setMiningSpeed(float miningSpeed) {
		this.miningSpeed = miningSpeed;
		return this;
	}
	
}
