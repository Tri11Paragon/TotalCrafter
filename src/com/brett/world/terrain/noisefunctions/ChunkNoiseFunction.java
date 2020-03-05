package com.brett.world.terrain.noisefunctions;

/**
*
* @author brett
* @date Mar. 4, 2020
*/

public class ChunkNoiseFunction extends NoiseFunction {
	
	public ChunkNoiseFunction(long seed) {
		super(seed);
	}

	@Override
	public float getInterpolatedNoise(float x, float z) {
		return (float) (ImprovedNoise.noise(ImprovedNoise.noise(x, getIntNoise(x, z)/20, z), 
				ImprovedNoise.noise(x, ImprovedNoise.noise(x, getIntNoise(x, z)/10, z)*10, z)/10, 
				ImprovedNoise.noise(x, getIntNoise(x, z)/5, z)
				)*30) + 30;
	}
	
	public float getIntNoise(float x, float z) {
		float fd = getSimNoise(x, z);
		fd = interpolate(fd, getSimNoise(x+1, z+1), getNoise(x, z));
		fd = interpolate(fd, getSimNoise(x-1, z-1), getNoise(x, z));
		return fd;
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
	}
	
	public float getSimNoise(float x, float z) {
		float fd = getSmoothNoise(x, z);
		fd += getSmoothNoise(x, z+1)/3;
		fd += getSmoothNoise(x, z-1)/3;
		fd += getSmoothNoise(x-1, z)/3;
		fd += getSmoothNoise(x+1, z)/3;
		fd += getSmoothNoise(x+2, z)/9;
		fd += getSmoothNoise(x-2, z)/9;
		fd += getSmoothNoise(x, z+2)/9;
		fd += getSmoothNoise(x, z-2)/9;
		return fd;
	}
	
	private float getSmoothNoise(float x, float z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 32f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 15f;
		float center = getNoise(x, z) / 8f;
		return corners + sides + center;
	}
	
	public float getNoise(float x, float z) {
		this.random.setSeed((long) (seed * x * z));
		return this.random.nextFloat() * 2.0f - 1.0f;
	}

}
