package com.brett.world.terrain.noisefunctions;

/**
*
* @author brett
*
*/

public class TrigNoiseFunction extends NoiseFunction {

	private float EXP_AMP;
	private float HILL_HEIGHT;
	
	public TrigNoiseFunction(long seed, float HILL_AMP, float HILL_HEIGHT) {
		super(seed);
		this.EXP_AMP = HILL_AMP;
		this.HILL_HEIGHT = HILL_HEIGHT;
	}

	@Override
	public float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
        int intZ = (int) z;
        float fX = x - intX;
        float fZ = z - intZ;
        
		//return getSmoothNoise(intX, intZ);
        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fX);
        float i2 = interpolate(v3, v4, fX);
        return interpolate(i1, i2, fZ);
	}
	
	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 5f;
        return a * (1f - f) + b * f;
	}
	
	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}
	
	private float getNoise(int x, int z) {
		random.setSeed((int) x * 49632 + z * 325176 + seed);
		//return (float) ((0.5 * Math.sin(random.nextDouble())) * 2 - 1) + (float) ((0.5 * Math.cos(random.nextDouble()) * 2 - 1));
		//float fd = Math.min(Math.max(((float) (Math.sin(Math.pow(random.nextDouble(), 2)) * 2 - 1) + (float) Math.cos(random.nextFloat() / 2) / 2f), 0.5f) * 2 - 1, 0.5f);
		
		// + (EXP_AMP * Math.sin(z * (HILL_HEIGHT))))
		float fd = (float) ((EXP_AMP * Math.sin(x * (HILL_HEIGHT + (0.025 * Math.sin(x * (HILL_HEIGHT * 0.1)))))));
		fd += ((EXP_AMP * Math.sin(z * (HILL_HEIGHT + (0.025 * Math.sin(z * (HILL_HEIGHT * 0.1)))))));
		fd += random.nextFloat()/6f;
		fd += Math.abs((Math.sin(random.nextDouble()/4f)/Math.cos(random.nextDouble()/16f))/8f);
		return fd;
	}

}
