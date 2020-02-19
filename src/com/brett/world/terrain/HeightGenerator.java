package com.brett.world.terrain;

import com.brett.world.terrain.noisefunctions.BaseNoiseFunction;
import com.brett.world.terrain.noisefunctions.NoiseFunction;

/**
*
* @author brett
*
*/

public class HeightGenerator extends HeightData {

	public static float AMPLITUDE = 70f;
	public static int OCTAVES = 1;
	public static float ROUGHNESS = 0.3f;
	
	private long seed;
	private int xOffset = 0;
    private int zOffset = 0;
    private NoiseFunction noiseFunction;
	
    //only works with POSITIVE gridX and gridZ values
	public HeightGenerator(int gridX, int gridZ, int vertexCount, BaseNoiseFunction function) {
		this.size = vertexCount;
		xOffset = gridX * (vertexCount-1);
        zOffset = gridZ * (vertexCount-1);
        this.noiseFunction = function;
	}
	
	public HeightGenerator(int gridX, int gridZ, int vertexCount, NoiseFunction function) {
		this.size = vertexCount;
		xOffset = gridX * (vertexCount-1);
        zOffset = gridZ * (vertexCount-1);
        this.noiseFunction = function;
	}
	
	public HeightGenerator() {
		this.seed = 694;
		this.noiseFunction = new BaseNoiseFunction(seed);
	}
	
	public HeightGenerator(long seed) {
		this.seed = seed;
		this.noiseFunction = new BaseNoiseFunction(seed);
	}
	
	public float getHeight(int x, int z) {
		float total = 0;
        float d = (float) Math.pow(2, OCTAVES-1);
        for(int i=0;i<OCTAVES;i++){
            float freq = (float) (Math.pow(2, i) / d);
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += noiseFunction.getInterpolatedNoise((x+xOffset)*freq, (z + zOffset)*freq) * amp;
        }
        return total;
	}
	
}
