package com.brett.world.chunks.biome;

import java.util.HashMap;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class BiomePalette {
	
	public final HashMap<Byte, Biome> biomes = new HashMap<Byte, Biome>();
	
	public BiomePalette() {
		
	}
	
	public BiomePalette(byte[] ids, Biome[] b) {
		if (ids.length != b.length)
			throw new RuntimeException("id array isn't equal to b array. please ensure that each id has a biome");
		for (int i = 0; i < ids.length; i++) {
			if (biomes.get(ids[i]) == null)
			biomes.put(ids[i], b[i]);
			else {
				biomes.remove(ids[i]);
				biomes.put(ids[i], b[i]);
			}
		}
	}

	public void assignBiome(byte id, Biome b) {
		if (biomes.get(id) == null)
			biomes.put(id, b);
		else {
			biomes.remove(id);
			biomes.put(id, b);
		}
	}
	
	
}
