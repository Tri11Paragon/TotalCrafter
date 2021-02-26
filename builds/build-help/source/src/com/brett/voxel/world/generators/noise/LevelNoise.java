package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 29, 2020
*/

public class LevelNoise extends BlockNoise {

	public LevelNoise(long seed) {
		super(seed, 1, 1, 1);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		return (int) fastNoise(x/128f, z/128f);
	}
	
}
