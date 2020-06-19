package com.brett.voxel.world.generators.worldgenmineable;

import java.util.Random;

import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.chunk.Chunk;

/**
*
* @author brett
* @date May 3, 2020
*/

public class WorldGenMineable {
	
	private MineData[] mineables;
	private Random r;
	
	public WorldGenMineable(MineData... mineables) {
		this.mineables = mineables;
		this.r = new Random(LevelLoader.seed);
	}
	
	public void generateOres(short[][][] c) {
		int tries = 0;
		// generate some mineables
		for (int i = 0; i < mineables.length; i++) {
			MineData md = mineables[i];
			// make sure the generator get random
			if (md.getRareness() < r.nextDouble()) {
				for (int t = 0; t < md.getSpawnTries(); t++) {
					// get a random pos in the chunk
					int x = r.nextInt(Chunk.x);
					int z = r.nextInt(Chunk.z);
					int y = r.nextInt(Chunk.y);
					/// generate some new tries if we can't find stone in the levels
					while ((y > md.getMaxLevel() || y < md.getMinLevel()) && (c[x][y][z] != 1) && tries < 100) {
						y = r.nextInt(Chunk.y);
						tries++;
					}
					tries = 0;
					// make sure we actually have stone and is in the correct levels
					if((c[x][y][z] != 1) || (y > md.getMaxLevel() || y < md.getMinLevel()))
						continue;
					// get the batch size
					int size = md.getSpawnBatch();
					c[x][y][z] = md.getBlock();
					// generate around the spot (creating pseudo veins) TODO: improve this with real veins
					for (int s = 0; s < size; s++) {
						// generate a pos
						int ax = x+((r.nextInt(Chunk.x/2))*(r.nextInt(2)*2-1));
						int ay = y+((r.nextInt(Chunk.y/2))*(r.nextInt(2)*2-1));
						int az = z+((r.nextInt(Chunk.z/2))*(r.nextInt(2)*2-1));
						// make sure the positions are in the chunk
						if (ax >= Chunk.x)
							ax = Chunk.x-1;
						if (ay >=  Chunk.y)
							ay = Chunk.y-1;
						if (az >= Chunk.z)
							az = Chunk.z-1;
						if (ax < 0)
							ax = 0;
						if (az < 0)
							az = 0;
						if (ay < 0)
							ay = 0;
						// make sure its in the min and max levels
						if (ay < md.getMinLevel() || ay > md.getMaxLevel())
							continue;
						if (c[ax][ay][az] != 1)
							continue;
						// place the block
						c[ax][ay][az] = md.getBlock();
					}
				}
			}
		}
	}
	
}
