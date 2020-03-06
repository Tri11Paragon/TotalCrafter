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
		return (float) (ImprovedNoise.noise(ImprovedNoise.noise(x, getIntNoise(x, z, 2)/20, z), 
				ImprovedNoise.noise(x, ImprovedNoise.noise(x, getIntNoise(x, z, 2)/10, z)*10, z)/10, 
				ImprovedNoise.noise(x, getIntNoise(x, z, 2)/5, z)
				)*(getIntNoise(x, z, 3)*40)) + 50;
	}
	
	public float getIntNoise(float x, float z, int size) {
		float fd = getSimNoise(x, z);
		float factor = 1;
		for (int i = -size; i < size; i++) {
			for (int j = -size; j < size; j++) {
				fd += ImprovedNoise.noise(x+factor*i, this.seed, z+factor*j)*(i/size);
				fd += ImprovedNoise.noise(x-factor*i, this.seed, z-factor*j)*(j/size);
				fd += ImprovedNoise.noise(x-factor*i, this.seed, z-factor*j)*(j/size);
				fd += ImprovedNoise.noise(x+factor*i, this.seed, z+factor*j)*(i/size);
			}
		}
		fd /= size;
		return fd;
	}
	
	public float getSimNoise(float x, float z) {
		float fd = getSmoothNoise(x, z);
		fd += getSmoothNoise(x, z+1)/3;
		fd += getSmoothNoise(x, z-1)/3;
		fd += getSmoothNoise(x-1, z)/3;
		fd += getSmoothNoise(x+1, z)/3;
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
