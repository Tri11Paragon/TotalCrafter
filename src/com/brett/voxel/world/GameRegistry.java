package com.brett.voxel.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.SaveEvent;
import com.brett.sound.AudioController;
import com.brett.voxel.inventory.recipe.CraftingManager;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.blocks.BlockAir;
import com.brett.voxel.world.blocks.BlockGrass;
import com.brett.voxel.world.blocks.BlockSand;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemCopperPickaxe;
import com.brett.voxel.world.items.ItemDiamondPickaxe;
import com.brett.voxel.world.items.ItemIronPickaxe;
import com.brett.voxel.world.items.ItemStonePickaxe;
import com.brett.voxel.world.items.ItemTinPickaxe;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.items.ItemWillPickaxe;
import com.brett.voxel.world.items.ItemWoodenPickaxe;

/**
*
* @author brett
* @date Mar. 13, 2020
*/

public class GameRegistry {
	
	private static short highestID = 0;
	private static List<SaveEvent> saveEvents = new ArrayList<SaveEvent>();
	private static HashMap<Integer, String> textures = new HashMap<Integer, String>();
	
	public static void init(Loader loader) {
		registerBlocks(loader);
		registerItems(loader);
		CraftingManager.registerCrafting(Block.BLOCK_LOG + "", Block.BLOCK_PLANKS, 4);
		CraftingManager.registerCrafting(Block.BLOCK_PLANKS + ";" + Block.BLOCK_PLANKS, Item.ITEM_STICK, 4);
	}
	
	private static void registerBlocks(Loader loader) {
		//TODO: maybe move into their own class
		registerBlock(Block.BLOCK_AIR, new BlockAir());
		
		registerBlock(Block.BLOCK_STONE, new Block(new ModelTexture(loader.loadTexture("stone")), 2
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setBlockDropped(Block.BLOCK_COBBLE).setHardness(3).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_DIRT, new Block(new ModelTexture(loader.loadTexture("dirt")), 1
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_WILL, new Block(new ModelTexture(loader.loadTexture("icon/logo")), 7
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(Short.MAX_VALUE*2).setMiningLevel(Short.MAX_VALUE*2).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.BLOCK_GRASS, new BlockGrass(new ModelTexture(loader.loadTexture("grassy2"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_SAND, new BlockSand(new ModelTexture(loader.loadTexture("sand"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_CLAY, new Block(new ModelTexture(loader.loadTexture("clay")), 8
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.0f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_SNOW, new Block(new ModelTexture(loader.loadTexture("snow")), 5
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.BLOCK_COBBLE, new Block(new ModelTexture(loader.loadTexture("cobble")), 3
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2.5f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_GLASS, new Block(new ModelTexture(loader.loadTexture("glass")), 9
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2.5f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_PICKAXE).setTransparent(true));
		
		registerBlock(Block.BLOCK_GLOWSTONE, new Block(new ModelTexture(loader.loadTexture("glowstone")), 10
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.78f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_PICKAXE).setLightLevel((byte) 15));
		
		registerBlock(Block.BLOCK_IRON, new Block(new ModelTexture(loader.loadTexture("iron_ore")), 19
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.38f).setMiningLevel(2).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_GOLD, new Block(new ModelTexture(loader.loadTexture("gold_ore")), 11
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.68f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_REDSTONE, new Block(new ModelTexture(loader.loadTexture("redstone_ore")), 12
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2.48f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_EMERALD, new Block(new ModelTexture(loader.loadTexture("emerald_ore")), 13
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(4.79f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_DIAMOND, new Block(new ModelTexture(loader.loadTexture("diamond_ore")), 14
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(3.72f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_COAL, new Block(new ModelTexture(loader.loadTexture("coal_ore")), 15
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.78f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_LOG, new Block(new ModelTexture(loader.loadTexture("pine")), 22
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.78f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
		
		registerBlock(Block.BLOCK_LEAVES, new Block(new ModelTexture(loader.loadTexture("leaves_oak")), 23
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.60f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_SWORD).setTransparent(true));
		
		registerBlock(Block.BLOCK_PLANKS, new Block(new ModelTexture(loader.loadTexture("planks")), 6
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.59f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
	}
	
	private static void registerItems(Loader loader) {
		registerItem(new ItemWillPickaxe(Item.ITEM_WILLPICK, new ModelTexture(loader.loadTexture("willpick"))));
		registerItem(new ItemCopperPickaxe(Item.ITEM_COPPERPICK, new ModelTexture(loader.loadTexture("copperpick"))));
		registerItem(new ItemDiamondPickaxe(Item.ITEM_DIAMONDPICK, new ModelTexture(loader.loadTexture("diamondpick"))));
		registerItem(new ItemIronPickaxe(Item.ITEM_IRONPICK, new ModelTexture(loader.loadTexture("ironpick"))));
		registerItem(new ItemStonePickaxe(Item.ITEM_STONEPICK, new ModelTexture(loader.loadTexture("stonepick"))));
		registerItem(new ItemTinPickaxe(Item.ITEM_TINPICK, new ModelTexture(loader.loadTexture("tinpick"))));
		registerItem(new ItemWoodenPickaxe(Item.ITEM_WOODPICK, new ModelTexture(loader.loadTexture("woodenpick"))));
		registerItem(new Item(Item.ITEM_STICK, new ModelTexture(loader.loadTexture("sticks"))));
	}
	
	public static HashMap<Integer, String> registerTextures() {
		textures.put(0, "grass");
		textures.put(1, "dirt");
		textures.put(2, "stone");
		textures.put(3, "cobble");
		textures.put(4, "sand");
		textures.put(5, "snow");
		textures.put(6, "planks");
		textures.put(7, "will");
		textures.put(8, "clay");
		textures.put(9, "glass");
		textures.put(10, "glowstone");
		textures.put(11, "gold_ore");
		textures.put(12, "redstone_ore");
		textures.put(13, "emerald_ore");
		textures.put(14, "diamond_ore");
		textures.put(15, "coal_ore");
		textures.put(16, "coal_block");
		textures.put(17, "brick");
		textures.put(18, "bookshelf");
		textures.put(19, "iron_ore");
		textures.put(20, "grass_side");
		textures.put(21, "oak");
		textures.put(22, "pine");
		textures.put(23, "leaves_oak");
		textures.put(24, "crafting_table_top");
		textures.put(25, "crafting_table_side");
		textures.put(26, "crafting_table_front");
		textures.put(27, "planks_oak");
		return textures;
	}
	
	public static void preSaveEvent() {
		for (int i = 0; i < saveEvents.size(); i++) 
			saveEvents.get(i).preSaveEvent();
	}
	
	public static void postSaveEvent() {
		for (int i = 0; i < saveEvents.size(); i++) 
			saveEvents.get(i).postSaveEvent();
	}
	
	public static void registerSaveEvent(SaveEvent e) {
		saveEvents.add(e);
	}
	
	public static void removeSaveEvent(SaveEvent e) {
		saveEvents.remove(e);
	}
	
	protected static void registerItem(short id, ModelTexture texture) {
		Item i = new Item(id, texture);
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
	}
	
	protected static void registerItem(Item i) {
		Item.items.put(i.getId(), i);
		Item.inverseItems.put(i, i.getId());
	}
	
	private static Item registerItemBlock(short id) {
		Item i = new Item(id, Block.blocks.get(id).model);
		Item.itemBlocks.put(i, Block.blocks.get(id));
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
		return i;
	}
	
	private static void registerBlock(short id, Block b) {
		if (id > highestID)
			highestID = id;
		Block.blocks.put(id, b);
		registerItemBlock(id);
		Block.inverseBlocks.put(b, id);
	}
	
	public static short getIdByBlock(Block b) {
		return Block.inverseBlocks.get(b);
	}
	
}
