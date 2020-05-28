package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 3, 2020
*/

public class ForestNoise extends BlockNoise {

	public ForestNoise(long seed) {
		super(seed, 50, 10, 0.9);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		return (int) super.simplexNoise(x/32f, z/64f) + 50;	
	}

}
