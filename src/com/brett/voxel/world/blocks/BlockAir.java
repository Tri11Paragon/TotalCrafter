package com.brett.voxel.world.blocks;

import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.RENDERMODE;

/**
*
* @author brett
* @date Feb. 13, 2020
*/

public class BlockAir extends Block {
	
	public BlockAir() {
		super(null, 0);
		this.setRendermode(RENDERMODE.TRANSPARENT);
		this.setCollisiontype(COLLISIONTYPE.NOT);
	}
	
}
