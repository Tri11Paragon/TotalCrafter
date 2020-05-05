package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 3, 2020
*/

public class BiomeNoise extends BlockNoise {

	public BiomeNoise(long seed) {
		super(seed, 1, 8, 1.0);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		return (int) super.simplexNoise(x/128.0f, z/128.0f);
	}

}
