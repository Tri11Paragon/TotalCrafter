package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 29, 2020
*/

public class DetailNoise extends BlockNoise {

	public DetailNoise(long seed) {
		super(seed, 1);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		return (int) fastNoise(x/8f, z/8f);
	}

}
