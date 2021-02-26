package com.brett.voxel.world;

import java.util.HashMap;
import com.brett.datatypes.Texture;
import com.brett.renderer.Loader;
import com.brett.sound.AudioController;
import com.brett.voxel.inventory.recipe.CraftingManager;
import com.brett.voxel.renderer.RENDERMODE;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.blocks.BlockAir;
import com.brett.voxel.world.blocks.BlockChest;
import com.brett.voxel.world.blocks.BlockCrafting;
import com.brett.voxel.world.blocks.BlockFlower;
import com.brett.voxel.world.blocks.BlockFurnace;
import com.brett.voxel.world.blocks.BlockGrass;
import com.brett.voxel.world.blocks.BlockLeaves;
import com.brett.voxel.world.blocks.BlockSand;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemCopperPickaxe;
import com.brett.voxel.world.items.ItemDiamondAxe;
import com.brett.voxel.world.items.ItemDiamondPickaxe;
import com.brett.voxel.world.items.ItemDiamondShovel;
import com.brett.voxel.world.items.ItemIronAxe;
import com.brett.voxel.world.items.ItemIronPickaxe;
import com.brett.voxel.world.items.ItemIronShovel;
import com.brett.voxel.world.items.ItemStoneAxe;
import com.brett.voxel.world.items.ItemStonePickaxe;
import com.brett.voxel.world.items.ItemStoneShovel;
import com.brett.voxel.world.items.ItemTinPickaxe;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.items.ItemWillPickaxe;
import com.brett.voxel.world.items.ItemWoodenAxe;
import com.brett.voxel.world.items.ItemWoodenPickaxe;
import com.brett.voxel.world.items.ItemWoodenShovel;

/**
*
* @author brett
* @date Mar. 13, 2020
* Registers stuff for the game
*/

public class GameRegistry {
	
	// highest item id
	private static short highestID = 0;
	private static HashMap<Integer, String> textures = new HashMap<Integer, String>();
	/**
	 * stores fuel times for various items
	 * in - item
	 * out - time
	 */
	private static HashMap<Integer, Integer> fuelTimes = new HashMap<Integer, Integer>();
	
	public static void init(Loader loader) {
		// register blocks
		registerBlocks(loader);
		registerItems(loader);
		// register fuels
		registerFuel(Item.COAL, 800);
		registerFuel(Block.PLANKS, 150);
		registerFuel(Block.LOG, 150);
		registerFuel(Block.LEAVES, 50);
		registerFuel(Item.STICK, 50);
		
		// register smelting
		
		CraftingManager.registerSmeltingRecipe(Block.IRONORE, Item.IRONINGOT, 100);
		CraftingManager.registerSmeltingRecipe(Block.COBBLE, Block.STONE, 100);
		CraftingManager.registerSmeltingRecipe(Block.SAND, Block.GLASS, 100);
		CraftingManager.registerSmeltingRecipe(Block.GOLDORE, Item.GOLDINGOT, 100);
		
		// register crafting
		
		CraftingManager.registerCrafting(Block.LOG + "", Block.PLANKS, 4);
		CraftingManager.registerCrafting(Block.PLANKS + ";" + Block.PLANKS, Item.STICK, 16);
		CraftingManager.registerCrafting(Block.PLANKS + "," + Block.PLANKS + ";" + Block.PLANKS + "," + Block.PLANKS, Block.CRAFT, 1);
		CraftingManager.registerCrafting(Block.PLANKS + "," + Block.PLANKS + "," + Block.PLANKS + 
				";0,"+Item.STICK + ",0;0," + Item.STICK +",0", Item.WOODPICK, 1);
		CraftingManager.registerCrafting(Block.COBBLE + "," + Block.COBBLE + "," + Block.COBBLE + 
				";0,"+Item.STICK + ",0;0," + Item.STICK +",0", Item.STONEPICK, 1);
		CraftingManager.registerCrafting(Item.IRONINGOT + "," + Item.IRONINGOT + "," + Item.IRONINGOT + 
				";0,"+Item.STICK + ",0;0," + Item.STICK +",0", Item.IRONPICK, 1);
		CraftingManager.registerCrafting(Item.DIAMOND + "," + Item.DIAMOND + "," + Item.DIAMOND + 
				";0,"+Item.STICK + ",0;0," + Item.STICK +",0", Item.DIAMONDPICK, 1);
		CraftingManager.registerCrafting("8,8,8;8,0,8;8,8,8", Block.FURNACE, 1);
		CraftingManager.registerCrafting("19,19,19;19,0,19;19,19,19", Block.CHEST, 1);
		CraftingManager.registerCrafting(Block.PLANKS + "," + Block.PLANKS + ",0;" 
										+ Block.PLANKS + "," + Item.STICK + ",0;0," + Item.STICK + ",0", Item.WOODAXE, 1);
		CraftingManager.registerCrafting(Block.COBBLE + "," + Block.COBBLE + ",0;" 
				+ Block.COBBLE + "," + Item.STICK + ",0;0," + Item.STICK + ",0", Item.STONEAXE, 1);
		CraftingManager.registerCrafting(Item.IRONINGOT + "," + Item.IRONINGOT + ",0;" 
				+ Item.IRONINGOT + "," + Item.STICK + ",0;0," + Item.STICK + ",0", Item.IRONAXE, 1);
		CraftingManager.registerCrafting(Item.DIAMOND + "," + Item.DIAMOND + ",0;" 
				+ Item.DIAMOND + "," + Item.STICK + ",0;0," + Item.STICK + ",0", Item.DIAMONDAXE, 1);
		CraftingManager.registerCrafting("0,"+Block.PLANKS+",0;0,"+Item.STICK+",0;0,"+Item.STICK+",0", Item.WOODSHOVEL, 1);
		CraftingManager.registerCrafting("0,"+Block.COBBLE+",0;0,"+Item.STICK+",0;0,"+Item.STICK+",0", Item.STONESHOVEL, 1);
		CraftingManager.registerCrafting("0,"+Item.IRONINGOT+",0;0,"+Item.STICK+",0;0,"+Item.STICK+",0", Item.IRONSHOVEL, 1);
		CraftingManager.registerCrafting("0,"+Item.DIAMOND+",0;0,"+Item.STICK+",0;0,"+Item.STICK+",0", Item.DIAMONDSHOVEL, 1);
	}
	
	private static void registerBlocks(Loader loader) {
		//TODO: maybe move into their own class
		// register blocks
		registerBlock(Block.AIR, new BlockAir());
		
		registerBlock(Block.STONE, new Block(new Texture(loader.loadTexture("stone")), 2
				).setBlockDropped(Block.COBBLE).setHardness(3).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.DIRT, new Block(new Texture(loader.loadTexture("dirt")), 1
				).setHardness(1.14f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.WILL, new Block(new Texture(loader.loadTexture("icon/logo")), 7
				).setHardness(Short.MAX_VALUE*2).setMiningLevel(Short.MAX_VALUE*2).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.GRASS, new BlockGrass(new Texture(loader.loadTexture("grassy2"))
				).setHardness(2).setEffectiveTool(ItemTool.TOOL_SHOVEL).setMiningLevel(0));
		
		registerBlock(Block.SAND, new BlockSand(new Texture(loader.loadTexture("sand"))
				).setHardness(0.9f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.CLAY, new Block(new Texture(loader.loadTexture("clay")), 8
				).setHardness(1.0f).setEffectiveTool(ItemTool.TOOL_SHOVEL));
		
		registerBlock(Block.SNOW, new Block(new Texture(loader.loadTexture("snow")), 5
				).setHardness(0.5f).setEffectiveTool(ItemTool.TOOL_SWORD));
		
		registerBlock(Block.COBBLE, new Block(new Texture(loader.loadTexture("cobble")), 3
				).setHardness(2.5f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.GLASS, new Block(new Texture(loader.loadTexture("glass")), 9
				).setHardness(2.5f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_PICKAXE)
				.setRendermode(RENDERMODE.TRANSPARENT).setBlockDropped((short)-1));
		
		registerBlock(Block.GLOWSTONE, new Block(new Texture(loader.loadTexture("glowstone")), 10
				).setHardness(0.78f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_PICKAXE).setLightLevel((byte) 15));
		
		registerBlock(Block.IRONORE, new Block(new Texture(loader.loadTexture("iron_ore")), 19
				).setHardness(1.38f).setMiningLevel(2).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.GOLDORE, new Block(new Texture(loader.loadTexture("gold_ore")), 11
				).setHardness(1.68f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.REDSTONEORE, new Block(new Texture(loader.loadTexture("redstone_ore")), 12
				).setHardness(2.48f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.EMERALDORE, new Block(new Texture(loader.loadTexture("emerald_ore")), 13
				).setHardness(4.79f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE)
				.setBlockDropped(Item.EMERALD));
		
		registerBlock(Block.DIAMONDORE, new Block(new Texture(loader.loadTexture("diamond_ore")), 14
				).setHardness(3.72f).setMiningLevel(3).setEffectiveTool(ItemTool.TOOL_PICKAXE)
				.setBlockDropped(Item.DIAMOND));
		
		registerBlock(Block.COALORE, new Block(new Texture(loader.loadTexture("coal_ore")), 15
				).setHardness(1.78f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE)
				.setBlockDropped(Item.COAL));
		
		registerBlock(Block.LOG, new Block(new Texture(loader.loadTexture("pine")), 22
				).setHardness(0.78f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
		
		registerBlock(Block.LEAVES, new BlockLeaves(new Texture(loader.loadTexture("leaves_oak")), 23
				).setHardness(0.60f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_SWORD)
				.setRendermode(RENDERMODE.TRANSPARENT));
		
		registerBlock(Block.PLANKS, new Block(new Texture(loader.loadTexture("planks")), 6
				).setHardness(0.59f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
		
		registerBlock(Block.CRAFT, new BlockCrafting(new Texture(loader.loadTexture("crafting_table_front"))
				).setHardness(0.39f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
		
		registerBlock(Block.COPPER, new Block(new Texture(loader.loadTexture("copper_ore")), 28
				).setHardness(0.99f).setMiningLevel(1).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.YELLOWFLOWER, new BlockFlower(new Texture(loader.loadTexture("flower_yellow")), 29
				).setHardness(0.1f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_HAND));
		
		registerBlock(Block.REDFLOWER, new BlockFlower(new Texture(loader.loadTexture("flower_red")), 30
				).setHardness(0.1f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_HAND));
		
		registerBlock(Block.TALLGRASS, new BlockFlower(new Texture(loader.loadTexture("tallgrass")), 31
				).setHardness(0.1f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_HAND)
				.setBlockDropped((short)-1));
		
		registerBlock(Block.FURNACE, new BlockFurnace(new Texture(loader.loadTexture("furnace_front_off")))
				.setBlockDropped(Block.FURNACE).setHardness(2.98f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_PICKAXE));
		
		registerBlock(Block.CHEST, new BlockChest(new Texture(loader.loadTexture("chest_front")))
				.setBlockDropped(Block.CHEST).setHardness(2.98f).setMiningLevel(0).setEffectiveTool(ItemTool.TOOL_AXE));
		
	}
	
	private static void registerItems(Loader loader) {
		//register items
		registerItem(new ItemWillPickaxe(Item.WILLPICK, new Texture(loader.loadTexture("willpick"))));
		registerItem(new ItemCopperPickaxe(Item.COPPERPICK, new Texture(loader.loadTexture("copperpick"))));
		registerItem(new ItemDiamondPickaxe(Item.DIAMONDPICK, new Texture(loader.loadTexture("diamondpick"))));
		registerItem(new ItemIronPickaxe(Item.IRONPICK, new Texture(loader.loadTexture("ironpick"))));
		registerItem(new ItemStonePickaxe(Item.STONEPICK, new Texture(loader.loadTexture("stonepick"))));
		registerItem(new ItemTinPickaxe(Item.TINPICK, new Texture(loader.loadTexture("tinpick"))));
		registerItem(new ItemWoodenPickaxe(Item.WOODPICK, new Texture(loader.loadTexture("woodenpick"))));
		registerItem(new Item(Item.STICK, new Texture(loader.loadTexture("sticks"))));
		registerItem(new Item(Item.COAL, new Texture(loader.loadTexture("coal"))));
		registerItem(new Item(Item.IRONINGOT, new Texture(loader.loadTexture("iron_ingot"))));
		registerItem(new Item(Item.GOLDINGOT, new Texture(loader.loadTexture("gold_ingot"))));
		registerItem(new Item(Item.DIAMOND, new Texture(loader.loadTexture("diamond"))));
		registerItem(new Item(Item.EMERALD, new Texture(loader.loadTexture("emerald"))));
		registerItem(new ItemWoodenAxe(Item.WOODAXE, new Texture(loader.loadTexture("wood_axe"))));
		registerItem(new ItemStoneAxe(Item.STONEAXE, new Texture(loader.loadTexture("stone_axe"))));
		registerItem(new ItemIronAxe(Item.IRONAXE, new Texture(loader.loadTexture("iron_axe"))));
		registerItem(new ItemDiamondAxe(Item.DIAMONDAXE, new Texture(loader.loadTexture("diamond_axe"))));
		registerItem(new ItemWoodenShovel(Item.WOODSHOVEL, new Texture(loader.loadTexture("wood_shovel"))));
		registerItem(new ItemStoneShovel(Item.STONESHOVEL, new Texture(loader.loadTexture("stone_shovel"))));
		registerItem(new ItemIronShovel(Item.IRONSHOVEL, new Texture(loader.loadTexture("iron_shovel"))));
		registerItem(new ItemDiamondShovel(Item.DIAMONDSHOVEL, new Texture(loader.loadTexture("diamond_shovel"))));
	}
	
	public static HashMap<Integer, String> registerTextures() {
		// register textures
		/*
		 * Textures need to be defined in order, starting at 0.
		 * Why not use a list then?
		 * because I wanted to be able to just look here
		 * and know the texture id.
		 */
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
		textures.put(28, "copper_ore");
		textures.put(29, "flower_yellow");
		textures.put(30, "flower_red");
		textures.put(31, "tallgrass");
		textures.put(32, "furnace_front_off");
		textures.put(33, "furnace_front_on");
		textures.put(34, "furnace_side");
		textures.put(35, "furnace_top");
		textures.put(36, "chest_top");
		textures.put(37, "chest_side");
		textures.put(38, "chest_front");
		return textures;
	}
	
	/**
	 * registers an item
	 */
	protected static void registerItem(short id, Texture texture) {
		Item i = new Item(id, texture);
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
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
		Item i = new Item(id, Block.blocks.get(id).model);
		Item.itemBlocks.put(i, Block.blocks.get(id));
		Item.items.put(id, i);
		Item.inverseItems.put(i, id);
		return i;
	}
	
	/**
	 * registers a block, also registers the block as an item of the same id.
	 */
	private static void registerBlock(short id, Block b) {
		if (id > highestID)
			highestID = id;
		Block.blocks.put(id, b);
		registerItemBlock(id);
		Block.inverseBlocks.put(b, id);
	}
	
	/**
	 * returns the fuel of the item.
	 */
	public static int getItemFuel(int input) {
		if (fuelTimes.containsKey(input))
			return fuelTimes.get(input);
		else
			return 0;
	}
	
	/**
	 * registers a fuel
	 */
	public static void registerFuel(int input, int time) {
		fuelTimes.put(input, time);
	}
	
	/*
	 * returns the block id from the block.
	 */
	public static short getIdByBlock(Block b) {
		return Block.inverseBlocks.get(b);
	}
	
}
