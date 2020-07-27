package com.brett.world.block;

import com.brett.world.World;
import com.brett.world.chunks.data.RenderMode;

/**
* @author Brett
* @date Jun. 29, 2020
*/

public class BlockAir extends Block {

	public BlockAir() {
		super(Block.AIR, 0);
		this.renderMode = RenderMode.TRANSPARENT;
		this.bbox = null;
	}
	
	@Override
	public void onBlockPlaced(World world, short id, int x, int y, int z) {
		return;
	}
	
}
