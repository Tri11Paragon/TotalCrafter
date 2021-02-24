package com.brett.voxel.world.blocks;

import java.util.HashMap;

import org.joml.Vector3f;

import com.brett.datatypes.Texture;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.RENDERMODE;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.lighting.LightingEngine;

/**
*
* @author brett
* The block class!
* to thing this started as something that had objects 
* per every block in the world
*/

public class Block {
	
	// define the block maps
	public static final HashMap<Short, Block> blocks = new HashMap<Short, Block>();
	public static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	// define all the block ids
	public static final short AIR = 0;
	public static final short STONE = 1;
	public static final short DIRT = 2;
	public static final short WILL = 3;
	public static final short GRASS = 4;
	public static final short SAND = 5;
	public static final short CLAY = 6;
	public static final short SNOW = 7;
	public static final short COBBLE = 8;
	public static final short GLASS = 9;
	public static final short GLOWSTONE = 10;
	public static final short IRONORE = 11;
	public static final short GOLDORE = 12;
	public static final short DIAMONDORE = 13;
	public static final short COALORE = 14;
	public static final short REDSTONEORE = 15;
	public static final short EMERALDORE = 16;
	public static final short LOG = 17;
	public static final short LEAVES = 18;
	public static final short PLANKS = 19;
	public static final short CRAFT = 20;
	public static final short COPPER = 21;
	public static final short YELLOWFLOWER = 22;
	public static final short REDFLOWER = 23;
	public static final short TALLGRASS = 24;
	public static final short FURNACE = 25;
	public static final short CHEST = 26;
	
	public Texture model;
	public int textureTop, textureBottom, textureLeft, textureRight, textureFront, textureFront2, textureBack;
	private int[] breakSound = {0};
	private short droppedBlock = 0;
	private int amountDropped = 1;
	private byte lightLevel = 0;
	private float hardness = 1;
	private int miningLevel = 0;
	private int effectiveTool = 0;
	private RENDERMODE rendermode = RENDERMODE.SOLID;
	private COLLISIONTYPE collisiontype = COLLISIONTYPE.SOLID;
	private float[] specialVerts;
	private float[] specialTextures;
	
	public Block(Texture model, float[] verts, float[] texs, int textureIndex) {
		this.model = model;
		this.specialVerts = verts;
		this.specialTextures = texs;
		this.textureFront = textureIndex;
		rendermode = RENDERMODE.SPECIAL;
	}
	
	public Block(Texture model, int textureIndex) {
		this.model = model;
		this.textureTop = textureIndex;
		this.textureBottom = textureIndex;
		this.textureLeft = textureIndex;
		this.textureRight = textureIndex;
		this.textureFront = textureIndex;
		this.textureFront2 = textureIndex;
		this.textureBack = textureIndex;
		rendermode = RENDERMODE.SOLID;
	}
	
	public Block(Texture model) {
		this.model = model;
		this.textureTop = 0;
		rendermode = RENDERMODE.SOLID;
	}
	
	/**
	 * called when the block gets placed
	 */
	public void onBlockPlaced(int x, int y, int z, IWorldProvider world) {
		//onBlockUpdated(x, y, z, world);
		if (this.lightLevel > 0)
			LightingEngine.addLightSource(x, y, z, this.lightLevel);
	}
	
	/**
	 * called when the block is destroyed.
	 */
	public void onBlockBreaked(int x, int y, int z, IWorldProvider world) {
		//onBlockUpdated(x, y, z, world);
		if(this.lightLevel > 0)
			LightingEngine.removeLightSource(x, y, z, this.lightLevel);
	}
	
	/**
	 * called as a random block update
	 */
	public void onBlockUpdated(int x, int y, int z, IWorldProvider world) {
		
	}
	
	/**
	 * unused.
	 */
	public void onBlockTick(int x, int y, int z, IWorldProvider world) {
		
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
	
	/**
	 * plays this blocks break sound
	 */
	public void playBreakSound(float x, float y, float z) {
		VoxelScreenManager.staticSource.setPosition(x, y, z);
		VoxelScreenManager.staticSource.play(breakSound[Maths.randomInt(0, breakSound.length-1)]);
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

	public float[] getSpecialVerts() {
		return specialVerts;
	}

	public Block setSpecialVerts(float[] specialVerts) {
		this.specialVerts = specialVerts;
		return this.setRendermode(RENDERMODE.SPECIAL);
	}

	public float[] getSpecialTextures() {
		return specialTextures;
	}

	public Block setSpecialTextures(float[] specialTextures) {
		this.specialTextures = specialTextures;
		return this;
	}

	public COLLISIONTYPE getCollisiontype() {
		return collisiontype;
	}

	public Block setCollisiontype(COLLISIONTYPE collisiontype) {
		this.collisiontype = collisiontype;
		return this;
	}
	
}
