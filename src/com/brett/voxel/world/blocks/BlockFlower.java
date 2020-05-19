package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.world.MeshStore;

/**
*
* @author brett
* @date May 16, 2020
*/

public class BlockFlower extends Block {
	
	public BlockFlower(ModelTexture model, int texture) {
		super(model, MeshStore.flowerVerts, MeshStore.uvFlowerCompleteCompress, texture);
	}

}
