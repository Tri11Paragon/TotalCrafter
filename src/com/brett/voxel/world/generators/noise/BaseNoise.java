package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 29, 2020
*/

public class BaseNoise extends BlockNoise {

	public BaseNoise(long seed) {
		super(seed);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		return (int) fastNoise(x, z);
	}

}
