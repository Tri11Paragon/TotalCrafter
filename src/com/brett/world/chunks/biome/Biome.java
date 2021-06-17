package com.brett.world.chunks.biome;

import com.brett.utils.Noise;
import com.brett.world.World;
import com.brett.world.chunks.data.BlockStorage;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class Biome {

	public static final int GRASSLANDS = 1;
	public static final int MOUNTAINS = 2;
	public static final int OLD = 3;
	public static final int FOREST = 4;
	public static final int PLAINS = 5;
	public static final int SAVANA = 6;
	public static final int EXTREME_HILLS = 7;
	
	private final int id;
	private final String name;
	protected int dirtAmount = 4;
	protected int lowPoint = -120;
	protected Noise noise1;
	protected Noise noise2;
	
	public Biome(int id, String name, long seed) {
		this.id = id;
		this.noise1 = new Noise(seed);
		this.noise2 = new Noise(seed & 0xFF + seed >> 0xFF);
		this.name = name;
	}
	
	/**
	 * creates the height map
	 * @param x in world space
	 * @param z in world space
	 * @return height of world at point
	 */
	public int generateHeight(int x, int z) {
		return 0;
	}
	
	/**
	 * values less then -Noise.RANGE_3D / 2 mean air
	 * @param x in world space
	 * @param y in world space
	 * @param z in world space
	 * @return positive values if block is, negative if block isn't
	 */
	public double generateNoise(int x, int y, int z) {
		return 0;
	}
	
	/**
	 * generate ore at x,y,z
	 * @param x in world space
	 * @param y in world space
	 * @param z in world space
	 * @param id of block currently here
	 * @return block id of new block ore
	 */
	public short generateOres(int x, int y, int z, short id) {
		return id;
	}
	
	/**
	 * called at the end of the world generation cycle.
	 */
	public void generate(BlockStorage blocks, World world) {
		
	}
	
	public int getDirtAmount() {
		return dirtAmount;
	}
	
	public int getLowHeight() {
		return lowPoint;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
}
