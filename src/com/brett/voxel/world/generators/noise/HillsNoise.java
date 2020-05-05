package com.brett.voxel.world.generators.noise;

/**
*
* @author brett
* @date May 3, 2020
*/

public class HillsNoise extends BlockNoise {
	
	public HillsNoise(long seed) {
		super(seed, 50, 12, 1.0);
	}

	@Override
	public int getBlockHeight(float x, float z) {
		float total = 0;
		for (int i = 0; i < 4; i++) {
			total += super.simplexNoise(((x*i)/16f), ((z*i)/32f));
		}
		total /= 4;
		return (int) total + 45;
	}
	
}
