package com.brett.world.terrain.noisefunctions;

import java.util.Random;

/**
*
* @author brett
*
*/

public abstract class NoiseFunction {

	protected Random random;
	protected long seed;
	
	public NoiseFunction(long seed) {
		this.seed = seed;
		random = new Random(seed);
	}

	public abstract float getInterpolatedNoise(float x, float z);
	
}
