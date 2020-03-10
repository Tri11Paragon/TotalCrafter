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
	
	public static void registerBlocks(Loader loader) {
		blocks.put((short) 0, new BlockAir());
		blocks.put((short) 1, new Block(new ModelTexture(loader.loadTexture("stone"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 2, new Block(new ModelTexture(loader.loadTexture("dirt"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 3, new Block(new ModelTexture(loader.loadTexture("icon/logo"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 4, new Block(new ModelTexture(loader.loadTexture("grassy2"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 5, new BlockSand(new ModelTexture(loader.loadTexture("sand")), (short) 5).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 6, new Block(new ModelTexture(loader.loadTexture("clay"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
		blocks.put((short) 7, new Block(new ModelTexture(loader.loadTexture("snow"))).setBreakSound(AudioController.loadSound("bounce.ogg")));
	}
	
}
