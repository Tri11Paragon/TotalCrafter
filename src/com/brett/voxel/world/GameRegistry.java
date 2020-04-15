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
		registerBlock((short) Block.BLOCK_AIR, new BlockAir());
		registerBlock((short) Block.BLOCK_STONE, new Block(new ModelTexture(loader.loadTexture("stone"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1).setMiningLevel(1));
		registerBlock((short) Block.BLOCK_DIRT, new Block(new ModelTexture(loader.loadTexture("dirt"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f));
		registerBlock((short) Block.BLOCK_WILL, new Block(new ModelTexture(loader.loadTexture("icon/logo"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(Short.MAX_VALUE*2).setMiningLevel(Short.MAX_VALUE*2));
		registerBlock((short) Block.BLOCK_GRASS, new BlockGrass(new ModelTexture(loader.loadTexture("grassy2"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(2));
		registerBlock((short) Block.BLOCK_SAND, new BlockSand(new ModelTexture(loader.loadTexture("sand"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(0.5f));
		registerBlock((short) Block.BLOCK_CLAY, new Block(new ModelTexture(loader.loadTexture("clay"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setHardness(1.0f));
		registerBlock((short) Block.BLOCK_SNOW, new Block(new ModelTexture(loader.loadTexture("snow"))).setBreakSound(AudioController.loadSound("bounce.ogg")).setLightLevel((byte) 15).setHardness(0.5f));
	}
	
	private static void registerItems(Loader loader) {
		
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
