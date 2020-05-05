package com.brett.voxel.world.generators;

import com.brett.voxel.world.LevelLoader;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.generators.noise.BiomeNoise;
import com.brett.voxel.world.generators.noise.BlockNoise;
import com.brett.voxel.world.generators.noise.ForestNoise;
import com.brett.voxel.world.generators.noise.HillsNoise;
import com.brett.voxel.world.generators.worldgenmineable.MineData;
import com.brett.voxel.world.generators.worldgenmineable.WorldGenMineable;

/**
*
* @author brett
* @date May 2, 2020
*/

public class WorldGenerator {
	
	public static enum BIOMES {FOREST, HILLS, DESERT};
	public static BlockNoise[] BIOMESFUNCTIONS;
	
	private HillsNoise hf;
	private ForestNoise ff;
	private BiomeNoise bf;
	private WorldGenMineable ores;
		
	public WorldGenerator() {
		hf = new HillsNoise(LevelLoader.seed);
		ff = new ForestNoise(LevelLoader.seed);
		bf = new BiomeNoise(((LevelLoader.seed/2)*128)/255);
		ores = new WorldGenMineable(
				new MineData(Block.BLOCK_GLOWSTONE, 1, 8, 0.0),
				new MineData(Block.BLOCK_IRON, 32, 60, 0.0, 60, 1),
				new MineData(Block.BLOCK_COAL, 40, 70, 0.0),
				new MineData(Block.BLOCK_GOLD, 10, 32, 0.0, 32, 1),
				new MineData(Block.BLOCK_REDSTONE, 30, 48, 0.0, 24, 1),
				new MineData(Block.BLOCK_EMERALD, 2, 8, 0.0),
				new MineData(Block.BLOCK_DIAMOND, 5, 20, 0.0, 24, 1)
				);
		BIOMESFUNCTIONS = new BlockNoise[] {hf, ff, ff};
	}
	
	public short[][][] getChunkBlocks(int x, int z){
		short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
		for (int i = 0; i < Chunk.x; i++) {
			for (int k=0; k < Chunk.z; k++) {
				int ref = (int) BlockNoise.interpolate(hf.getBlockHeight(i + x*Chunk.x, k + z*Chunk.z), 
						ff.getBlockHeight(i + x*Chunk.x, k + z*Chunk.z), 
						getBiomeNoise(i + x*Chunk.x, k + z*Chunk.z));
				for (int j=0; j < Chunk.y; j++) {
					if (j == ref)
						blks[i][j][k] = 4;
					if (j < ref && j > ref-4)
						blks[i][j][k] = 2;
					if (j < ref-3)
						blks[i][j][k] = 1;
					if (j == 0)
						blks[i][j][k] = 3;
				}
			}
		}
		ores.generateOres(blks);
		return blks;
	}
	
	public void spawnTrees() {
		
	}
	
	public BlockNoise[] getBiomeList(int x, int z) {
		return new BlockNoise[] {BIOMESFUNCTIONS[getBiomeType(x, z)], BIOMESFUNCTIONS[getBiomeType(x+1, z)], BIOMESFUNCTIONS[getBiomeType(x-1, z)],
				BIOMESFUNCTIONS[getBiomeType(x, z+1)], BIOMESFUNCTIONS[getBiomeType(x, z-1)]};
	}
	
	public int getBiomeType(int x, int z) {
		int size = BIOMES.values().length;
		int biome = (int) ((getBiomeNoise(x, z)) * size);
		if (biome >= size)
			biome = size - 1;
		if (biome < 0)
			biome = 0;
		return (biome);
	}
	
	public float getBiomeNoise(int x, int z) {
		return bf.simplexNoise(x/128f, z/128f);
	}
	
}
