package com.brett.voxel.world;

import java.util.ArrayList;
import java.util.List;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.SaveEvent;
import com.brett.sound.AudioController;
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
	
	public static void init(Loader loader) {
		registerBlocks(loader);
		registerItems(loader);
	}
	
	private static void registerBlocks(Loader loader) {
		//TODO: maybe move into their own class
		registerBlock(Block.BLOCK_AIR, new BlockAir());
		
		registerBlock(Block.BLOCK_STONE, new Block(new ModelTexture(loader.loadTexture("stone"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setBlockDropped(Block.BLOCK_COBBLE).setHardness(3).setMiningLevel(2).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.BLOCK_DIRT, new Block(new ModelTexture(loader.loadTexture("dirt"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_WILL, new Block(new ModelTexture(loader.loadTexture("icon/logo"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(Short.MAX_VALUE*2).setMiningLevel(Short.MAX_VALUE*2).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.BLOCK_GRASS, new BlockGrass(new ModelTexture(loader.loadTexture("grassy2"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_SAND, new BlockSand(new ModelTexture(loader.loadTexture("sand"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_CLAY, new Block(new ModelTexture(loader.loadTexture("clay"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.0f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.BLOCK_SNOW, new Block(new ModelTexture(loader.loadTexture("snow"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setLightLevel((byte) 15).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.BLOCK_COBBLE, new Block(new ModelTexture(loader.loadTexture("cobble"))
				).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2.5f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
	}
	
	private static void registerItems(Loader loader) {
		registerItem(new ItemWillPickaxe(Item.ITEM_WILLPICK, new ModelTexture(loader.loadTexture("willpick"))));
		registerItem(new ItemCopperPickaxe(Item.ITEM_COPPERPICK, new ModelTexture(loader.loadTexture("copperpick"))));
		registerItem(new ItemDiamondPickaxe(Item.ITEM_DIAMONDPICK, new ModelTexture(loader.loadTexture("diamondpick"))));
		registerItem(new ItemIronPickaxe(Item.ITEM_IRONPICK, new ModelTexture(loader.loadTexture("ironpick"))));
		registerItem(new ItemStonePickaxe(Item.ITEM_STONEPICK, new ModelTexture(loader.loadTexture("stonepick"))));
		registerItem(new ItemTinPickaxe(Item.ITEM_TINPICK, new ModelTexture(loader.loadTexture("tinpick"))));
		registerItem(new ItemWoodenPickaxe(Item.ITEM_WOODPICK, new ModelTexture(loader.loadTexture("woodenpick"))));
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
