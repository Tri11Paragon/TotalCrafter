package com.brett.voxel.world.blocks;

import com.brett.datatypes.Texture;
import com.brett.voxel.world.IWorldProvider;

/**
*
* @author brett
* @date Mar. 8, 2020
*/

public class BlockSand extends Block {

	// TODO: add in blockfalling class
	// TODO: improove this:
	
	public BlockSand(Texture model) {
		super(model, 4);
	}
	
	@Override
	public void onBlockUpdated(int x, int y, int z, IWorldProvider world) {
		super.onBlockUpdated(x, y, z, world);
		// there is some kind of issue with this
		// only happens in - chunks
		// but the get and set functions don't seem to be what to blame
		/**
		 * UPDATE:
		 * though the tests i did showed that there should not be a need for a bias
		 * especially though the mousepicker test.
		 * howeever biasing this works out Soooooooo
		 * this is what we are doing
		 */
		// please don't remove marks for this
		if (world.chunk.getBlockBIAS(x, y - 1, z) == 0) {
			world.chunk.setBlockBIAS(x, y, z, 0);
			world.chunk.setBlockBIAS(x, y - 1, z, Block.SAND);
			world.updateBlocksAround(x, y, z);
			world.updateBlocksAround(x, y - 1, z);
		}
	}

}
