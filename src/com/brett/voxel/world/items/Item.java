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
	
	public static final short WILLPICK = 1024;
	public static final short WOODPICK = 1025;
	public static final short STONEPICK = 1026;
	public static final short IRONPICK = 1027;
	public static final short COPPERPICK = 1028;
	public static final short TINPICK = 1029;
	public static final short DIAMONDPICK = 1030;
	public static final short STICK = 1031;
	public static final short COAL = 1032;
	public static final short IRONINGOT = 1033;
	public static final short GOLDINGOT = 1034;
	public static final short DIAMOND = 1035;
	public static final short EMERALD = 1036;
	public static final short WOODAXE = 1037;
	public static final short STONEAXE = 1038;
	public static final short IRONAXE = 1039;
	public static final short DIAMONDAXE = 1040;
	
	public static Map<Short, Item> items = new HashMap<Short, Item>();
	public static Map<Item, Short> inverseItems = new HashMap<Item, Short>();
	public static Map<Item, Block> itemBlocks = new HashMap<Item, Block>();
	
	private ModelTexture texture;
	private short id;
	private int miningLevel = 0;
	private float miningSpeed = 0.2f;
	private int maxStackSize = 128;
	
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

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public void setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
	}
	
}
