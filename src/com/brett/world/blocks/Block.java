package com.brett.world.blocks;

import java.util.HashMap;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;

/**
*
* @author brett
*	
*	DO NOT USE THIS
*	This is made as a test for a voxel system.
*	this is my first time making anything to do with voxel meshing.
*
*/

public class Block {
	
	public static final HashMap<Integer, Block> blocks = new HashMap<Integer, Block>();
	
	public ModelTexture model;
	
	public Block(ModelTexture model) {
		this.model = model;
	}
	
	public static void registerBlocks(Loader loader) {
		blocks.put(0, new BlockAir());
		blocks.put(1, new Block(new ModelTexture(loader.loadTexture("dirt"))));
	}
	
}
