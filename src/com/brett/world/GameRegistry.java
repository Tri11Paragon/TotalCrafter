package com.brett.world;

import java.util.HashMap;

import com.brett.world.block.Block;
import com.brett.world.block.BlockAir;
import com.brett.world.block.BlockGlowstone;
import com.brett.world.chunks.biome.Biome;
import com.brett.world.chunks.biome.BiomeExtremeHills;
import com.brett.world.chunks.biome.BiomeForest;
import com.brett.world.chunks.biome.BiomeGrasslands;
import com.brett.world.chunks.biome.BiomeMountains;
import com.brett.world.chunks.biome.BiomeOld;
import com.brett.world.chunks.biome.BiomePlains;
import com.brett.world.chunks.biome.BiomeSavana;
import com.brett.world.item.Item;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class GameRegistry {
	
	private static int highestID = 0;
	
	public static HashMap<Short, String> blockTextures = new HashMap<Short, String>();
	public static HashMap<String, Short> blockTextureIDs = new HashMap<String, Short>();
	public static HashMap<Integer, Biome> biomes = new HashMap<Integer, Biome>();
	
	public static HashMap<Short, String> registerTextures() {
		registerTexture((short)0, "stone2", "stone");
		registerTexture((short)1, "dirt", "dirt");
		registerTexture((short)2, "grass", "grass");
		registerTexture((short)3, "stone3", "basalt");
		registerTexture((short)4, "cobble", "cracked");
		registerTexture((short)5, "glowstone", "glowstone");
		return blockTextures;
	}
	
	public static void registerBiomes() {
		// TODO: this seed
		registerBiome(new BiomeGrasslands(Biome.GRASSLANDS, "Grasslands", 694));
		registerBiome(new BiomeMountains(Biome.MOUNTAINS, "Mountains", 694));
		registerBiome(new BiomeOld(Biome.OLD, "old generator", 694));
		registerBiome(new BiomeForest(Biome.FOREST, "Forest", 694));
		registerBiome(new BiomePlains(Biome.PLAINS, "Plains", 694));
		registerBiome(new BiomeSavana(Biome.SAVANA, "Savana", 694));
		registerBiome(new BiomeExtremeHills(Biome.EXTREME_HILLS, "Extreme Hills", 694));
	}
	
	public static void registerBlocks() {
		registerBlock(new BlockAir());
		registerBlock(new Block(Block.STONE, getTexture("stone")));
		registerBlock(new Block(Block.DIRT, getTexture("dirt")));
		registerBlock(new Block(Block.GRASS, getTexture("grass")));
		registerBlock(new Block(Block.BASALT, getTexture("basalt")));
		registerBlock(new BlockGlowstone(Block.GLOWSTONE, getTexture("glowstone")).setLightLevel(15));
	}
	
	public static void registerItems() {
		
	}
	
	public static void registerTexture(short id, String texture) {
		blockTextures.put(id, texture);
		blockTextureIDs.put(texture, id);
	}
	
	public static void registerTexture(short id, String texture, String textureName) {
		blockTextures.put(id, texture);
		blockTextureIDs.put(textureName, id);
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
	
	public static void registerBiome(Biome b) {
		biomes.put(b.getId(), b);
	}
	
	public static Biome getBiomeById(int id) {
		return biomes.get(id);
	}
	
	public static int getIdByBlock(Block b) {
		return Block.inverseBlocks.get(b);
	}
	
	public static int getTexture(String texture) {
		if (blockTextureIDs.containsKey(texture))
			return blockTextureIDs.get(texture);
		else
			return -1;
	}
	
	public static Block getBlock(short id) {
		return Block.blocks.get(id) != null ? Block.blocks.get(id) : Block.blocks.get(Block.AIR);
	}
	
}
