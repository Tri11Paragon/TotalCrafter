package com.brett.voxel.world.chunk;

import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.blocks.Block;

/**
*
* @author brett
* @date May 13, 2020
* placeholder data-type for chunk nul generation.
*/

public class NulChunk {
	
	private short[][][] blocks = new short[Chunk.x][Chunk.y][Chunk.z];
	private IWorldProvider s;
	
	public NulChunk(IWorldProvider world) {
		this.s = world;
	}
	
	public short[][][] getBlocks(){
		return blocks;
	}
	
	/**
	 * puts the blocks into this chunk, without overriding existing blocks
	 * input blocks need to be the same size as the chunk size
	 */
	public short[][][] integrate(short[][][] genBlocks) {
		for (int i = 0; i < this.blocks.length; i++) {
			for(int j = 0; j < this.blocks.length; j++) {
				for (int k = 0; k < this.blocks.length; k++) {
					if (this.blocks[i][j][k] == 0) {
						this.blocks[i][j][k] = genBlocks[i][j][k];
					}
				}
			}
		}
		return this.blocks;
	}
	
	public void setBlock(int x, int y, int z, int rx, int rz, int block) {
		if (x >= Chunk.x || z >= Chunk.z)
			return;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y >= Chunk.y)
			y = Chunk.y-1;
		if (y < 0)
			y = 0;
		if (block != 0) {
			Block b = Block.blocks.get((short) block);
			if (b != null)
				b.onBlockPlaced(rx, y, rz, s);
		}
		blocks[x][y][z] = (short)block;
	}
	
}
