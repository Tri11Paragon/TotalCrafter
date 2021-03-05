int to1D(int x, int y, int z){
	return (z * 16 * 128) + (y * 16) + x;
}

float fade(float t) {
		return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
}

float lerp(float t, float a, float b) {
		return a + t * (b - a);
}

float grad(int hash, float x, float y, float z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		float u = h < 8.0f ? x : y; // INTO 12 GRADIENT DIRECTIONS.
		float v = h < 4.0f ? y : h == 12.0f || h == 14.0f ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
}

float perlinNoise(global const int *p, const float ix, const float iy, const float iz){
	float x = ix;
	float y = iy;
	float z = iz;
	int X = (int) floor(x) & 255;
	int Y = (int) floor(y) & 255;
	int Z = (int) floor(z) & 255;
	
	x -= floor(x);
	y -= floor(y);
	z -= floor(z);
	
	float u = fade(x); // COMPUTE FADE CURVES
	float v = fade(y); // FOR EACH OF X,Y,Z.
	float w = fade(z);
	
	int A = p[X] + Y;
	int AA = p[A] + Z;
	int AB = p[A + 1] + Z; // HASH COORDINATES OF
	int B = p[X + 1] + Y;
	int BA = p[B] + Z;
	int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,
	
	return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), // AND ADD
				grad(p[BA], x - 1, y, z)), // BLENDED
				lerp(u, grad(p[AB], x, y - 1, z), // RESULTS
						grad(p[BB], x - 1, y - 1, z))), // FROM 8
				lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), // CORNERS
						grad(p[BA + 1], x - 1, y, z - 1)), // OF CUBE
						lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
}

kernel void worldgen(global const int *p, global const int *refh, global const int *cpos, global int *out){
	int index = get_global_id(0);
	
	int indexCopy = index;
	int cx = cpos[0];
	int cz = cpos[1];
	
	int k = (int)(indexCopy / (16 * 128));
	indexCopy -= (k * 16 * 128);
	int j = (int) (indexCopy / 16);
	indexCopy -= j * 16;
	int i = indexCopy;
	
	int cax = i + cx;
	int caz = k + cz;
	
	int amount = 30;
	
	int ref = refh[i * 16 + k];
	int realref = ref;
	
	if (ref > 80 && j > 85){
		amount = 22;
	}
	
	float reference = perlinNoise(p, cax/128.05234f, j/16.234f, caz/128.312394f)*64 + amount;
	
	if (reference > 0 && j < ref){
		int j1 = j+1;
		if (out[to1D(i, j1, k)] == 0){
			if (realref == ref){
				realref = j;
			}
			if (j < 42){
				out[to1D(i, j, k)] = 1;
			} else if (j < 52){
				out[to1D(i, j, k)] = 5;
			} else { 
				out[to1D(i, j, k)] = 4;
			}
		} else if (out[to1D(i, j+2, k)] == 0 || out[to1D(i, j+3, k)] == 0 || out[to1D(i, j+4, k)] == 0 || out[to1D(i, j+5, k)] == 0){
			if (j < 42){
				out[to1D(i, j, k)] = 1;
			} else if (j < 52){
				out[to1D(i, j, k)] = 5;
			} else {
				out[to1D(i, j, k)] = 4;
			}
		} else{
			out[to1D(i, j, k)] = 1;
		}
	}
	if (j == 0){
		out[to1D(i, 0, k)] = 3;
	}
}

