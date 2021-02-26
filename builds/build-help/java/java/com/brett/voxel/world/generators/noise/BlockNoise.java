package com.brett.voxel.world.generators.noise;

import java.util.Random;

/**
*
* @author brett
* @date May 2, 2020
*/

public abstract class BlockNoise {
	
	private long seed;
	private Random random;
	private float AMPLITUDE = 60f;
	private int OCTAVES = 8;
	private double ROUGHNESS = 0.4f;
	
	public BlockNoise(long seed) {
		this.seed = seed;
		this.random = new Random();
		// generate some randomness
		Random temprand = new Random(seed);
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[temprand.nextInt(permutation.length)]; }
	}
	
	public BlockNoise(long seed, float amplitude, int octaves, double roughness) {
		this.seed = seed;
		this.random = new Random();
		Random temprand = new Random(seed);
		// generate some randomness
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[temprand.nextInt(permutation.length)]; }
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[i]; }
		this.AMPLITUDE = amplitude;
		this.OCTAVES = octaves;
		this.ROUGHNESS = roughness;
		/*Random temprand = new Random(seed);
        for(int i = 0; i < 256; i++) {
            int from = temprand.nextInt(p.length);
            int to = temprand.nextInt(p.length);
           
            int temp = p[from];
            p[from] = p[to];
            p[to] = temp;
        }*/
	}
	
	public BlockNoise(long seed, float amplitude) {
		this.seed = seed;
		this.random = new Random();
		Random temprand = new Random(seed);
		// generate some randomness
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[temprand.nextInt(permutation.length)]; }
		this.AMPLITUDE = amplitude;
	}
	
	public BlockNoise(long seed, double roughness) {
		this.seed = seed;
		this.random = new Random();
		Random temprand = new Random(seed);
		// generate some randomness
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[temprand.nextInt(permutation.length)]; }
		this.ROUGHNESS = roughness;
	}
	
	public BlockNoise(long seed, int octaves) {
		this.seed = seed;
		this.random = new Random();
		Random temprand = new Random(seed);
		// generate some randomness
		for (int i=0; i < 256 ; i++) {p[256+i] = p[i] = permutation[temprand.nextInt(permutation.length)]; }
		this.OCTAVES = octaves;
	}
	
	public abstract int getBlockHeight(float x, float z);
	
	/**
	 * returns simplex noise
	 * This is a very slow function
	 */
	public float simplexNoise(float x, float z) {
		x = x < 0 ? -x : x;
    	z = z < 0 ? -z : z;
        float total = 0;
        // this is just a java implementation of the simplex noise algorithm
        // it is very slow.
        float d = (float) Math.pow(2, OCTAVES-1);
        // add octaves to the noise
        for(int i=0;i<OCTAVES;i++){
        	// generates height based on simplex octaves
            float freq = (float) (Math.pow(2, i) / d);
            float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
            total += generateHeight((x)*freq, (z)*freq) * amp;
        }
        return (float) total;
	}
	
	/**
	 * Generates semi smooth noise
	 */
	public float fastNoise(float x, float z) {
		if (x < 0)
			x = -x;
		if (z < 0)
			z = -z;
		return (generateHeight(x, z) * AMPLITUDE);
	}
 
    public float generateHeight(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;
		
		// get noise from area around this point
		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		// interpolate it
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
    }
     
    /**
     * simple interpolate function using cos
     */
    public static float interpolate(float a, float b, float blend){
        double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }
 
    /**
     * gets noise from the area around the point in an attempt to smooth it.
     */
    private float getSmoothNoise(int x, int z) {
    	// get the sides.
    	// divide by constant as sides are less valuable then center points.
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1)) / 16f;
        float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
                + getNoise(x, z + 1)) / 8f;
        // get the center
        float center = getNoise(x, z) / 4f;
        // add them all together
        return corners + sides + center;
    }
 
    /**
     * gets noise at a point.
     */
    private float getNoise(int x, int z) {
        random.setSeed(x * 231 + z * 4322 + seed);
        return random.nextFloat() * 2f - 1f;
    }
	
	/**
	 * Ken Perlin's noise
	 */
	public double perlinNoise(double x, double y, double z) {
		int X = (int) Math.floor(x) & 255, // FIND UNIT CUBE THAT
				Y = (int) Math.floor(y) & 255, // CONTAINS POINT.
				Z = (int) Math.floor(z) & 255;
		x -= Math.floor(x); // FIND RELATIVE X,Y,Z
		y -= Math.floor(y); // OF POINT IN CUBE.
		z -= Math.floor(z);
		double u = fade(x), // COMPUTE FADE CURVES
				v = fade(y), // FOR EACH OF X,Y,Z.
				w = fade(z);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, // HASH COORDINATES OF
				B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

		return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), // AND ADD
				grad(p[BA], x - 1, y, z)), // BLENDED
				lerp(u, grad(p[AB], x, y - 1, z), // RESULTS
						grad(p[BB], x - 1, y - 1, z))), // FROM 8
				lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), // CORNERS
						grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
						lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
	}

	/**
	 * From Ken Perlin
	 */
	private double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	/**
	 * From Ken Perlin
	 */
	private double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	/**
	 * From Ken Perlin
	 */
	private double grad(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
				v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	/**
	 * From Ken Perlin
	 */
	final int p[] = new int[512], permutation[] = { 151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7,
			225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62,
			94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175,
			74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92,
			41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18,
			169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124,
			123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223,
			183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253,
			19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210,
			144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
			84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141,
			128, 195, 78, 66, 215, 61, 156, 180 };
}
