package com.brett.voxel.world.blocks;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.lighting.LightingEngine;

/**
*
* @author brett
*
*/

public class Block {
	
	public static final HashMap<Short, Block> blocks = new HashMap<Short, Block>();
	public static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	public static final short BLOCK_AIR = 0;
	public static final short BLOCK_STONE = 1;
	public static final short BLOCK_DIRT = 2;
	public static final short BLOCK_WILL = 3;
	public static final short BLOCK_GRASS = 4;
	public static final short BLOCK_SAND = 5;
	public static final short BLOCK_CLAY = 6;
	public static final short BLOCK_SNOW = 7;
	
	public ModelTexture model;
	private int[] breakSound = {0};
	private short droppedBlock = 0;
	private int amountDropped = 1;
	private byte lightLevel = 0;
	
	public Block(ModelTexture model) {
		this.model = model;
	}
	
	public void onBlockCreated(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockPlaced(int x, int y, int z, VoxelWorld world) {
		onBlockUpdated(x, y, z, world);
		if (this.lightLevel > 0)
			LightingEngine.addLightSource(x, y, z, this.lightLevel);
	}
	
	public void onBlockBreaked(int x, int y, int z, VoxelWorld world) {
		onBlockUpdated(x, y, z, world);
		if(this.lightLevel > 0)
			LightingEngine.removeLightSource(x, y, z, this.lightLevel);
	}
	
	public void onBlockUpdated(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockTick(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public byte getLightLevel() {
		return lightLevel;
	}

	public Block setLightLevel(byte lightLevel) {
		this.lightLevel = lightLevel;
		return this;
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
	
	public Block setBlockDropped(short b) {
		this.droppedBlock = b;
		return this;
	}
	
	public short getBlockDropped() {
		if (droppedBlock == 0)
			return inverseBlocks.get(this);
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
