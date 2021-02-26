package com.brett.voxel.world.generators.worldgenmineable;

import com.brett.voxel.world.chunk.Chunk;

/**
*
* @author brett
* @date May 3, 2020
* Data structure to store data about ores
*/

public class MineData {
	
	/*
	 * if this is called after the random determined by the rareness then the world generator will
	 * try this # of times to spawn the amount of ore specified by spawn batch.
	 */
	private int spawnTries;
	private int spawnBatch;
	/*
	 * The world generator will generate a random number between 0 and 1. if this number is less then the generated number
	 * then a batch of this mineable block will be generated.
	 */
	private double rareness;
	// min and max spawn levels
	private int maxLevel;
	private int minLevel;
	// block to use
	private short block;
	
	public MineData(short block, int spawnTries, int spawnBatch, double rareness) {
		super();
		this.block = block;
		this.spawnTries = spawnTries;
		this.spawnBatch = spawnBatch;
		this.rareness = rareness;
		this.maxLevel = Chunk.y-1;
		this.minLevel = 1;
	}
	
	public MineData(short block, int spawnTries, int spawnBatch, double rareness, int maxLevel, int minLevel) {
		super();
		this.block = block;
		this.spawnTries = spawnTries;
		this.spawnBatch = spawnBatch;
		this.rareness = rareness;
		this.maxLevel = maxLevel < Chunk.y ? maxLevel : Chunk.y-1;
		this.minLevel = minLevel > 0 ? minLevel : 1;
	}
	
	/*
	 * getters and setters methods
	 */

	public int getSpawnTries() {
		return spawnTries;
	}

	public void setSpawnTries(int spawnTries) {
		this.spawnTries = spawnTries;
	}

	public int getSpawnBatch() {
		return spawnBatch;
	}

	public void setSpawnBatch(int spawnBatch) {
		this.spawnBatch = spawnBatch;
	}

	public double getRareness() {
		return rareness;
	}

	public void setRareness(double rareness) {
		this.rareness = rareness;
	}
	
	public short getBlock() {
		return this.block;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public void setBlock(short block) {
		this.block = block;
	}
	
}
