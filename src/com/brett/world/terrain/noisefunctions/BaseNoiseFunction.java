package com.brett.world.terrain.noisefunctions;

/**
*
* @author brett
*
*/

public class BaseNoiseFunction extends NoiseFunction {
	
	
	public BaseNoiseFunction(long seed) {
		super(seed);
	}

	@Override
	public float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;
         
        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
		//return (float) (Math.cos(x)+Math.sin(z));
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
	}
	
	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 32f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 15f;
		float center = getNoise(x, z) / 8f;
		return corners + sides + center;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed((long) (x * 49632 + z * 325176 + seed));
		return (float)random.nextDouble() * 2f - 1f;
	}

}
