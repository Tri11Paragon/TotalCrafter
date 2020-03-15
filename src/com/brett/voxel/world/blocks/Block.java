package com.brett.voxel.world.blocks;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.ModelTexture;
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
	public static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	public static short BLOCK_AIR = 0;
	public static short BLOCK_STONE = 1;
	public static short BLOCK_DIRT = 2;
	public static short BLOCK_WILL = 3;
	public static short BLOCK_GRASS = 4;
	public static short BLOCK_SAND = 5;
	public static short BLOCK_CLAY = 6;
	public static short BLOCK_SNOW = 7;
	
	public ModelTexture model;
	private int[] breakSound = {0};
	private Block droppedBlock = null;
	private int amountDropped = 1;
	
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
	
	public Block setAmountDropped(int amount) {
		this.amountDropped = amount;
		return this;
	}
	
	public int getAmountDropped() {
		return amountDropped;
	}
	
	public Block setBlockDropped(Block b) {
		this.droppedBlock = b;
		return this;
	}
	
	public Block getBlockDropped() {
		if (droppedBlock == null)
			return this;
		return droppedBlock;
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
	
}
