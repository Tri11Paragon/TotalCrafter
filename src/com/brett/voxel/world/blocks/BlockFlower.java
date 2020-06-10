package com.brett.voxel.world.blocks;

import com.brett.datatypes.Texture;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.world.MeshStore;

/**
*
* @author brett
* @date May 16, 2020
* flower object class
*/

public class BlockFlower extends Block {
	
	public BlockFlower(Texture model, int texture) {
		super(model, MeshStore.flowerVerts, MeshStore.uvFlowerCompleteCompress, texture);
		this.setCollisiontype(COLLISIONTYPE.NOT);
	}

}
