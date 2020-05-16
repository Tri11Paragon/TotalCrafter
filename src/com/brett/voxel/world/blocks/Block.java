package com.brett.voxel.world.blocks;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.RENDERMODE;
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
	public static final short BLOCK_COBBLE = 8;
	public static final short BLOCK_GLASS = 9;
	public static final short BLOCK_GLOWSTONE = 10;
	public static final short BLOCK_IRON = 11;
	public static final short BLOCK_GOLD = 12;
	public static final short BLOCK_DIAMOND = 13;
	public static final short BLOCK_COAL = 14;
	public static final short BLOCK_REDSTONE = 15;
	public static final short BLOCK_EMERALD = 16;
	public static final short BLOCK_LOG = 17;
	public static final short BLOCK_LEAVES = 18;
	public static final short BLOCK_PLANKS = 19;
	public static final short BLOCK_CRAFT = 20;
	public static final short BLOCK_COPPER = 21;
	
	public ModelTexture model;
	public int textureTop, textureBottom, textureLeft, textureRight, textureFront, textureBack;
	private int[] breakSound = {0};
	private short droppedBlock = 0;
	private int amountDropped = 1;
	private byte lightLevel = 0;
	private float hardness = 1;
	private int miningLevel = 0;
	private int effectiveTool = 0;
	private RENDERMODE rendermode = RENDERMODE.SOLID;

	public Block(ModelTexture model, int textureIndex) {
		this.model = model;
		this.textureTop = textureIndex;
		this.textureBottom = textureIndex;
		this.textureLeft = textureIndex;
		this.textureRight = textureIndex;
		this.textureFront = textureIndex;
		this.textureBack = textureIndex;
		rendermode = RENDERMODE.SOLID;
	}
	
	public Block(ModelTexture model) {
		this.model = model;
		this.textureTop = 0;
		rendermode = RENDERMODE.SOLID;
	}
	
	public void onBlockPlaced(int x, int y, int z, VoxelWorld world) {
		//onBlockUpdated(x, y, z, world);
		if (this.lightLevel > 0)
			LightingEngine.addLightSource(x, y, z, this.lightLevel);
	}
	
	public void onBlockBreaked(int x, int y, int z, VoxelWorld world) {
		//onBlockUpdated(x, y, z, world);
		if(this.lightLevel > 0)
			LightingEngine.removeLightSource(x, y, z, this.lightLevel);
	}
	
	public void onBlockUpdated(int x, int y, int z, VoxelWorld world) {
		
	}
	
	public void onBlockTick(int x, int y, int z, VoxelWorld world) {
		
	}
	
	/**
	 * Called when the player right clicks on this block
	 * return true if you don't want blocks to be placed.
	 */
	public boolean onBlockInteract(int x, int y, int z, VoxelWorld world, PlayerInventory i) {
		return false;
	}
	
	public int getMiningLevel() {
		return miningLevel;
	}

	public Block setMiningLevel(int miningLevel) {
		this.miningLevel = miningLevel;
		return this;
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
	
	public Block setHardness(float f) {
		this.hardness = f;
		return this;
	}
	
	public float getHardness() {
		return this.hardness;
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
	
	public Block setTextureIndex(int i) {
		this.textureTop = i;
		this.textureBottom = i;
		this.textureLeft = i;
		this.textureRight = i;
		this.textureFront = i;
		this.textureBack = i;
		return this;
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

	public int getEffectiveTool() {
		return effectiveTool;
	}

	public Block setEffectiveTool(int effectiveTool) {
		this.effectiveTool = effectiveTool;
		return this;
	}

	public RENDERMODE getRendermode() {
		return rendermode;
	}

	public Block setRendermode(RENDERMODE rendermode) {
		this.rendermode = rendermode;
		return this;
	}
	
}
