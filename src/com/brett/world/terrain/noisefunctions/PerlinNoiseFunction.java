package com.brett.world.terrain.noisefunctions;

/**
*
* @author brett
*
*/

public class PerlinNoiseFunction extends NoiseFunction {
	
	public PerlinNoiseFunction(long seed) {
		super(seed);
		random.setSeed(seed);
	}

	@Override
	public float getInterpolatedNoise(float x, float z) {
		return perlin(x, z);
	}
	
	private float dotGridGradient(int ix, int iz, float x, float z) {

	    // Compute the distance vector
	    float dx = x - (float)ix;
	    float dy = z - (float)iz;
	    
	    // Compute the dot-product
	    return (dx*getNoise(iz, ix) + dy*getNoise(iz, ix));
	}
	
	private float lerp(float a0, float a1, float w) {
	    return (1.0f - w)*a0 + w*a1;
	}
	
	// this is not perlin i think?
	private float perlin(float x, float z) {
		float centers = getSmoothNoise(x, z);
		float outers = getSmoothNoise(x - 10, z) + getSmoothNoise(x + 10, z) + getSmoothNoise(x - 10, z + 10) + getSmoothNoise(x + 10, z - 10);
		float corners = getSmoothNoise(x + 10, z + 10) + getSmoothNoise(x - 10, z - 10) + getSmoothNoise(x + 10, z - 10) + getSmoothNoise(x - 10, z + 10);
		
		return centers / 8f + outers + corners;
	}
	
	private float getSmoothNoise(float x, float z) {
		int ix = (int)x;
		int iz = (int)z;
		
		float main = getNoise(ix, iz);
		float center = getNoise(ix + 1, iz) + getNoise(ix - 1, iz) + getNoise(ix, iz + 1) + getNoise(ix, iz - 1) / 4f;
		float corners = getNoise(ix + 1, iz + 1) + getNoise(ix - 1, iz + 1) + getNoise(ix + 1, iz - 1) + getNoise(ix - 1, iz - 1)/8f;
		
		return main + center + corners;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed((long) (x * 49632 + z * 325176 + seed));
		return (float)random.nextDouble() * 2f - 1f;
	}
	
}
