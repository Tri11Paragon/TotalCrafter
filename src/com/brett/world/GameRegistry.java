package com.brett.world;

import java.util.HashMap;

import com.brett.world.block.Block;
import com.brett.world.block.BlockAir;
import com.brett.world.item.Item;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class GameRegistry {
	
	private static short highestID = 0;
	
	public static HashMap<Integer, String> blockTextures = new HashMap<Integer, String>();
	public static HashMap<String, Integer> blockTextureIDs = new HashMap<String, Integer>();
	
	public static HashMap<Integer, String> registerTextures() {
		registerTexture(0, "stone2");
		registerTexture(1, "dirt");
		return blockTextures;
	}
	
	public static void registerBlocks() {
		registerBlock(new BlockAir());
		registerBlock(new Block(Block.STONE, getTexture("stone2")));
		registerBlock(new Block(Block.DIRT, getTexture("dirt")));
	}
	
	public static void registerItems() {
		
	}
	
	public static void registerTexture(int id, String texture) {
		blockTextures.put(id, texture);
		blockTextureIDs.put(texture, id);
	}
	
	/**
	 * registers an item
	 */
	protected static Item registerItem(short id, int textureID) {
		Item i = new Item(id, textureID);
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
		return i;
	}
	
	/**
	 * registers item
	 */
	protected static void registerItem(Item i) {
		Item.items.put(i.getId(), i);
		Item.inverseItems.put(i, i.getId());
	}
	
	/**
	 * registers a block as an item.
	 */
	private static Item registerItemBlock(short id) {
		Item i = new Item(id, Block.blocks.get(id).textureFront);
		Item.itemBlocks.put(i, Block.blocks.get(id));
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
		return i;
	}
	
	/**
	 * registers a block, also registers the block as an item of the same id.
	 */
	private static void registerBlock(Block b) {
		if (b.id > highestID)
			highestID = b.id;
		Block.blocks.put(b.id, b);
		registerItemBlock(b.id);
		Block.inverseBlocks.put(b, b.id);
	}
	
	public static short getIdByBlock(Block b) {
		return Block.inverseBlocks.get(b);
	}
	
	public static int getTexture(String texture) {
		return blockTextureIDs.get(texture);
	}
	
	public static Block getBlock(short id) {
		return Block.blocks.get(id) != null ? Block.blocks.get(id) : Block.blocks.get((short)0);
	}
	
}
