package com.brett.world.chunks.biome;

import com.brett.world.World;
import com.brett.world.chunks.data.BlockStorage;

/**
 * @author Brett
 * @date 16-Jun-2021
 */

public class BiomeGrasslands extends Biome {

	public BiomeGrasslands(int id, String name, long seed) {
		super(id, name, seed);
	}

	@Override
	public int generateHeight(int x, int y) {
		// noise1.noise(wx / 128d + noise2.nNoise(wx / 32d, 4 / 3492d, wz / 32d + noise1.noise(wx, wz), 8, 4d), wz / 128d) * 64 + 64;
		// (int) ((noise1.noise(x / 512d, y / 512d) + noise2.nNoise(x / 32d, 4 / 3492d, y / 32d + noise1.noise(x, y), 8, 4d)) * 32 + 72)
		/*
		 * (int) ((noise1.noise(x * 512d, y * 512d) * 32 
				+ noise2.nNoise(x / 512d, 4 / 3492d, y / 512d + noise1.noise(x/128d, y/128d), 8, 4d)*16 ) 
				+ noise1.noise((x/4d) * 128d, (y/4d) * 128d)*32
				+ 72)
		 */
		return (int)((noise1.noise(x / 256f, y / 256f) * 32) + (noise2.noise(x/32f, y/32f) * 4) + 72);
		/*return (int) (
				noise1.noise(
						(x 
						/ 
						512d) 
						+ noise2.noise(
								(
										(x + (y >> 4)) 
										* 
										((x << 4) + y)
								)/512d
								), 
						y + ( 
								(double)(x << 4) 
								/ 
								(double)(y >> 3) 
							)
						) 
				) * 32 + 64;*/
	}
	
	@Override
	public double generateNoise(int x, int y, int z) {
		// noise1.noise(wx / 32d, wy / 32d, wz / 32d)
		if (y < 0) {
			if (y < -1000) {
				return noise1.noise(x / 32d, y / 32d, z / 32d) * 23 + noise2.noise(x/8d + noise1.noise(y / 32d) * 2.425d, y/8d + noise1.noise(x / 32d) * 2.452d) * 16.05d;
			} else {
				return noise1.noise(x / 32d, y / 32d, z / 32d) * 23 + noise2.noise(x/8d + noise1.noise(y / 32d) * 2.425d, y/8d + noise1.noise(x / 32d) * 2.452d) * 6.05d;
			}
		} else {
			return noise1.noise(x / 32d, y / 32d, z / 32d);
		}
	}
	
	@Override
	public short generateOres(int x, int y, int z, short id) {
		return super.generateOres(x, y, z, id);
	}
	
	@Override
	public void generate(BlockStorage blocks, World world) {
		super.generate(blocks, world);
	}

}
