package com.brett.voxel.world.generators;

import java.util.Random;

import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.generators.noise.DetailNoise;
import com.brett.voxel.world.generators.noise.LevelNoise;
import com.brett.voxel.world.generators.noise.RoughnessNoise;
import com.brett.voxel.world.generators.worldgenmineable.MineData;
import com.brett.voxel.world.generators.worldgenmineable.WorldGenMineable;

/**
*
* @author brett
* @date May 2, 2020
*/

public class WorldGenerator {
	
	private LevelNoise lf;
	private DetailNoise df;
	private RoughnessNoise rf;
	private WorldGenMineable ores;
	private Random rnd;
	private IWorldProvider world;
		
	public WorldGenerator(IWorldProvider world) {
		rnd = new Random(LevelLoader.seed);
		lf = new LevelNoise(LevelLoader.seed);
		df = new DetailNoise(LevelLoader.seed);
		rf = new RoughnessNoise(LevelLoader.seed);
		
		ores = new WorldGenMineable(
				new MineData(Block.IRON, 32, 60, 0.0, 60, 1),
				new MineData(Block.COAL, 40, 70, 0.0),
				new MineData(Block.COPPER, 15, 70, 0.0),
				new MineData(Block.GOLD, 10, 32, 0.0, 32, 1),
				new MineData(Block.REDSTONE, 30, 48, 0.0, 24, 1),
				new MineData(Block.EMERALD, 2, 8, 0.0),
				new MineData(Block.DIAMOND, 5, 20, 0.0, 24, 1)
				);
		this.world = world;		
		//long start = 0;
		//long end = 0;
		
		/*long averageSimplex = 0;
		for (int p = 0; p < 1000; p++) {
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				getBiomeMix(i/16f, LevelLoader.seed/16f);
			}
			end = System.currentTimeMillis();
			averageSimplex += (end - start);
		}
		System.out.println("Time to do simplex(avg1000): " + (averageSimplex/1000d));
		
		long averagePerlin = 0;
		for (int p = 0; p < 1000; p++) {
			start = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				df.perlinNoise(i/16f, 0.5234, LevelLoader.seed/16f);
			}
			end = System.currentTimeMillis();
			averagePerlin += (end - start);
		}
		System.out.println("Time to do Perlin(avg1000): " + (averagePerlin/1000d));
		
		long averageBitshift = 0;
		for (int p = 0; p < 1000; p++) {
			start = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				long doubletime = i >> 5;
			}
			end = System.currentTimeMillis();
			averageBitshift += (end - start);
		}
		System.out.println("Time to do bitshift(avg1000): " + (averageBitshift/1000d));
		
		long averageDivide = 0;
		for (int p = 0; p < 1000; p++){
			start = System.currentTimeMillis();
			for (int i = 0; i < 1000000; i++) {
				long doubletime = i / 32;
			}
			end = System.currentTimeMillis();
			averageDivide += (end - start);
		}
		System.out.println("Time to do divide(avg1000): " + (averageDivide/1000d));*/
	}
	
	public short[][][] getChunkBlocks(int x, int z){
		int xp = x, zp = z;
		if (x < 0)
			xp = -x;
		if (z < 0)
			zp = -z;
		rnd.setSeed(((xp * LevelLoader.seed * 4923492) + (zp * LevelLoader.seed * 59234)) + LevelLoader.seed);
		short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
		
		int cx = x*Chunk.x;
		int cz = z*Chunk.z;
		
		for (int i = 0; i < Chunk.x; i++) {
			for (int k = 0; k < Chunk.z; k++) {

				// reference numbers
				int cax = i + cx;
				int caz = k + cz;
				
				int ref = (int) getBiomeMix(cax, caz, 1);
				int realref = ref;
				int amount = 28;
				
				for (int j = Chunk.y-6; j > 0; j--) {
					if (ref > 80 && j > 80)
						amount = 20;
					// this should change with seed if I changed the perlin noise correctly.
					double reference = (lf.perlinNoise(cax/128.05234d, j/16.234, caz/128.312394d)*64) + amount;
					if (reference > 0 && j < ref) {
						int j1 = j+1;
						if (blks[i][j1][k] == 0) {
							// assigns the highest grass block in the chunk to be the 'real' reference point
							// this allows for some nice trees.
							if (realref == ref)
								realref = j;
							if (j < 42) {
								blks[i][j][k] = Block.STONE;
							} else if (j < 52) {
								blks[i][j][k] = Block.SAND;
							} else
								blks[i][j][k] = Block.GRASS;
							// generate some flowers and terrain detail
							if (j1 > 52) {
								if (rnd.nextInt(150) == 5) {
									blks[i][j1][k] = Block.REDFLOWER;
								}
								if (rnd.nextInt(100) == 5) {
									blks[i][j1][k] = Block.YELLOWFLOWER;
								}
								if (rnd.nextInt(20) == 5 && realref < 80) {
									blks[i][j1][k] = Block.TALLGRASS;
								}
							}
						// generate a layer of dirt or sand about 4 blocks thick
						} else if(blks[i][j+2][k] == 0 || blks[i][j+3][k] == 0 || blks[i][j+4][k] == 0 || blks[i][j+5][k] == 0) {
							if (j < 42)
								blks[i][j][k] = Block.STONE;
							else if (j < 52) {
								blks[i][j][k] = Block.SAND;
							} else
								blks[i][j][k] = Block.DIRT;
						} else
							blks[i][j][k] = Block.STONE;
					}
				}
				// make sure we can't mine outside the world.
				blks[i][0][k] = Block.WILL;
				
				int tree = rnd.nextInt((int) (800 / getForestAmount(cax, caz)));
				if (tree == 5 && realref < 100 && realref > 52) {
					int height = rnd.nextInt(3) + 4;
					for (int lx = -2; lx < 3; lx++) {
						for (int lz = -2; lz < 3; lz++) {
							int lxp = i + lx;
							int lzp = k + lz;
							for (int hh = 0; hh <= 2; hh++) {
								if (lxp < 0 || lxp > (Chunk.x - 1) || lzp < 0 || lzp > (Chunk.z - 1)) {
									world.chunk.setBlock(lxp + cx, realref + height + hh - 2, lzp + cz, Block.LEAVES);
								} else
									blks[lxp][realref + height + hh - 2][lzp] = Block.LEAVES;
							}
						}
					}
					for (int lx = -1; lx < 2; lx++) {
						for (int lz = -1; lz < 2; lz++) {
							int lxp = i + lx;
							int lzp = k + lz;
							if (lxp < 0 || lxp > (Chunk.x - 1) || lzp < 0 || lzp > (Chunk.z - 1)) {
								world.chunk.setBlock(lxp + cx, realref + height + 1, lzp + cz, Block.LEAVES);
							} else
								blks[lxp][realref + height + 1][lzp] = Block.LEAVES;
						}
					}
					blks[i][realref + height + 2][k] = Block.LEAVES;
					for (int t = realref; t <= realref + height; t++) {
						blks[i][t][k] = Block.LOG;
					}
				}
				
			}
		}
		ores.generateOres(blks);
		return blks;
	}
	
	public float getBiomeMix(float x, float z, float scale) {
		float height = (float) (((lf.perlinNoise(x/128f + (345345/LevelLoader.seed), LevelLoader.seed/128f, z/128f + (53485834/LevelLoader.seed)) + 
				((df.perlinNoise(x/24f + (34595/LevelLoader.seed), LevelLoader.seed/128f, z/24f)*rf.perlinNoise(x/72f, LevelLoader.seed/32f, z/72f + (345992/LevelLoader.seed)))))/scale)*32 + 80);
		
		return height;
	}
	
	public float getForestAmount(float x, float z) {
		return (float) Math.abs(lf.perlinNoise(x/512f + (1/LevelLoader.seed), LevelLoader.seed/78f, z/512f + (1/LevelLoader.seed))) * 10;
	}
	
	public float getDesertAmount(float x, float z) {
		return (float) Math.abs(lf.perlinNoise(x/136f + (1/LevelLoader.seed), LevelLoader.seed/256f, z/128f + (1/LevelLoader.seed))) * 10;
	}
	
}
