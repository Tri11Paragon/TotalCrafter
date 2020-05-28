package com.brett.voxel.world.generators;

import java.util.Random;

import com.brett.voxel.world.LevelLoader;
import com.brett.voxel.world.VoxelWorld;
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
	private Random rnd;
	private VoxelWorld world;
		
	public WorldGenerator(VoxelWorld world) {
		rnd = new Random(LevelLoader.seed);
		hf = new HillsNoise(LevelLoader.seed);
		ff = new ForestNoise(LevelLoader.seed);
		bf = new BiomeNoise(((LevelLoader.seed/2)*128)/255);
		ores = new WorldGenMineable(
				//new MineData(Block.BLOCK_GLOWSTONE, 1, 8, 0.0),
				new MineData(Block.IRON, 32, 60, 0.0, 60, 1),
				new MineData(Block.COAL, 40, 70, 0.0),
				new MineData(Block.COPPER, 45, 70, 0.0),
				new MineData(Block.GOLD, 10, 32, 0.0, 32, 1),
				new MineData(Block.REDSTONE, 30, 48, 0.0, 24, 1),
				new MineData(Block.EMERALD, 2, 8, 0.0),
				new MineData(Block.DIAMOND, 5, 20, 0.0, 24, 1)
				);
		BIOMESFUNCTIONS = new BlockNoise[] {hf, ff, ff};
		this.world = world;
	}
	
	public short[][][] getChunkBlocks(int x, int z){
		int xp = x, zp = z;
		if (x < 0)
			xp = -x;
		if (z < 0)
			zp = -z;
		rnd.setSeed(((xp * LevelLoader.seed * 4923492) + (zp * LevelLoader.seed * 59234)) + LevelLoader.seed);
		short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
		for (int i = 0; i < Chunk.x; i++) {
			for (int k=0; k < Chunk.z; k++) {
				int ref = (int) BlockNoise.interpolate(hf.getBlockHeight(i + x*Chunk.x, k + z*Chunk.z), 
						ff.getBlockHeight(i + x*Chunk.x, k + z*Chunk.z), 
						getBiomeNoise(i + x*Chunk.x, k + z*Chunk.z));
				int tree = rnd.nextInt(250);
				if (tree == 5 && ref < 90 && ref > 30) {
					int height = rnd.nextInt(3)+4;
					for (int lx = -2; lx < 3; lx++) {
						for (int lz = -2; lz < 3; lz++) {
							int lxp = i+lx;
							int lzp = k+lz;
							for (int hh = 0; hh <= 2; hh++) {							
								if (lxp < 0 || lxp > (Chunk.x-1) || lzp < 0 || lzp > (Chunk.z-1)) {
									world.chunk.setBlock(lxp + x*Chunk.x, ref+height+hh-2, lzp + z*Chunk.z, Block.LEAVES);
								} else
									blks[lxp][ref+height+hh-2][lzp] = Block.LEAVES;
							}
						}
					}
					for (int lx = -1; lx < 2; lx++) {
						for (int lz = -1; lz < 2; lz++) {
							int lxp = i+lx;
							int lzp = k+lz;					
							if (lxp < 0 || lxp > (Chunk.x - 1) || lzp < 0 || lzp > (Chunk.z - 1)) {
								world.chunk.setBlock(lxp + x * Chunk.x, ref + height + 1, lzp + z * Chunk.z, Block.LEAVES);
							} else
								blks[lxp][ref + height+1][lzp] = Block.LEAVES;
						}
					}
					blks[i][ref + height+2][k] = Block.LEAVES;
					for (int t = ref; t <= ref+height; t++) {
						blks[i][t][k] = Block.LOG;
					}
				}
				if (ref > 30) {
					if (rnd.nextInt(150) == 5) {
						if (ref+1 < Chunk.y)
							blks[i][ref+1][k] = Block.REDFLOWER;
					}
					if (rnd.nextInt(100) == 5) {
						if (ref+1 < Chunk.y)
							blks[i][ref+1][k] = Block.YELLOWFLOWER;
					}
					if (rnd.nextInt(20) == 5 && ref<80) {
						if (ref+1 < Chunk.y)
							blks[i][ref+1][k] = Block.TALLGRASS;
					}
				}
				for (int j=0; j <= ref; j++) {
					if (ref < 30) {
						if (j > ref-4)
							blks[i][j][k] = Block.SAND;
					} else {
						if (j == ref)
							blks[i][j][k] = Block.GRASS;
						if (j < ref && j > ref-4)
							blks[i][j][k] = Block.DIRT;
					}
					if (j < ref-3)
						blks[i][j][k] = Block.STONE;
					if (j == 0)
						blks[i][j][k] = Block.WILL;
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
	
	public int getNaturalBlockHeight(int x, int z) {
		int ref = (int) BlockNoise.interpolate(hf.getBlockHeight(x, z), 
				ff.getBlockHeight(x, z), 
				getBiomeNoise(x, z));
		return ref;
	}
	
}
