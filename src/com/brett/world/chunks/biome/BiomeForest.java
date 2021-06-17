package com.brett.world.chunks.biome;

import com.brett.world.World;
import com.brett.world.chunks.data.BlockStorage;

/**
* @author Brett
* @date 16-Jun-2021
*/

public class BiomeForest extends Biome {

	public BiomeForest(int id, String name, long seed) {
		super(id, name, seed);
	}
	
	@Override
	public int generateHeight(int x, int z) {
		return super.generateHeight(x, z);
	}
	
	@Override
	public double generateNoise(int x, int y, int z) {
		return super.generateNoise(x, y, z);
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
