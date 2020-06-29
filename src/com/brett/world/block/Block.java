package com.brett.world.block;

import java.util.HashMap;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class Block {
	
	public static final HashMap<Short, Block> blocks = new HashMap<Short, Block>();
	public static final HashMap<Block, Short> inverseBlocks = new HashMap<Block, Short>();
	
	public static final short AIR = 0;
	public static final short STONE = 1;
	public static final short DIRT = 2;
	public static final short GRASS = 3;
	
	public int textureTop, textureBottom, textureLeft, textureRight, textureFront, textureFront2, textureBack;
	
}
