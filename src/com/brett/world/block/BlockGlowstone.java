package com.brett.world.block;

import com.brett.world.Lighting;
import com.brett.world.World;

/**
 * @author Brett
 * @date Jul. 9, 2020
 */

public class BlockGlowstone extends Block {

	public BlockGlowstone(short id, int texture) {
		super(id, texture);
		super.lightLevel = 15;
	}

	@Override
	public void onBlockPlaced(World world, short id, int x, int y, int z) {
		Lighting.updateLighting(x, y, z, 15);
		world.getChunkWorld(x, y, z).lights.setWorld(x, y, z, 15);
		super.onBlockPlaced(world, id, x, y, z);
	}

	@Override
	public void onBlockDestroyed(World world, short id, int x, int y, int z) {
		Lighting.updateLightingRemove(x, y, z, 15);
		world.getChunkWorld(x, y, z).lights.setWorld(x, y, z, 0);
		super.onBlockDestroyed(world, id, x, y, z);
	}

}
