package com.brett.voxel.world.blocks;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.sound.AudioController;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
*
*/

public class Block {
	
	public static final HashMap<Short, Block> blocks = new HashMap<Short, Block>();
	private static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	public ModelTexture model;
	private int[] breakSound = {0};
	
	public Block(ModelTexture model) {
		this.model = model;
	}
	
	public void onBlockCreated(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockPlaced(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockBreaked(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockUpdated(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockTick(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public Block setBreakSound(int[] sound) {
		this.breakSound = sound;
		return this;
	}
	
	public Block setBreakSound(int sound) {
		this.breakSound[0] = sound;
		return this;
	}
	
	public void playBreakSound(Vector3f pos) {
		playBreakSound(pos.x, pos.y, pos.z);
	}
	
	public void playBreakSound(float x, float y, float z) {
		VoxelScreenManager.staticSource.setPosition(x, y, z);
		VoxelScreenManager.staticSource.play(breakSound[Maths.randInt(0, breakSound.length-1)]);
		VoxelScreenManager.staticSource.setReferenceDistance(6);
		VoxelScreenManager.staticSource.setRollOffFactor(5);
		VoxelScreenManager.staticSource.setMaxDistance(15);
	}
	
	public int[] getBreakSounds() {
		return breakSound;
	}
	
	public int getBreakSound() {
		return breakSound[0];
	}
	
	public static short BLOCK_AIR = 0;
	public static short BLOCK_STONE = 1;
	public static short BLOCK_DIRT = 2;
	public static short BLOCK_WILL = 3;
	public static short BLOCK_GRASS = 4;
	public static short BLOCK_SAND = 5;
	public static short BLOCK_CLAY = 6;
	public static short BLOCK_SNOW = 7;
	
	public static void registerBlocks(Loader loader) {
		registerBlock((short) BLOCK_AIR, new BlockAir());
		registerBlock((short) BLOCK_STONE, new Block(new ModelTexture(loader.loadTexture("stone"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_DIRT, new Block(new ModelTexture(loader.loadTexture("dirt"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_WILL, new Block(new ModelTexture(loader.loadTexture("icon/logo"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_GRASS, new BlockGrass(new ModelTexture(loader.loadTexture("grassy2"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_SAND, new BlockSand(new ModelTexture(loader.loadTexture("sand"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_CLAY, new Block(new ModelTexture(loader.loadTexture("clay"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		registerBlock((short) BLOCK_SNOW, new Block(new ModelTexture(loader.loadTexture("snow"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
	}
	
	private static void registerBlock(short id, Block b) {
		blocks.put(id, b);
		inverseBlocks.put(b, id);
	}
	
	public static short getIdByBlock(Block b) {
		return inverseBlocks.get(b);
	}
	
}
