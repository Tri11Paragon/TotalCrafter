package com.brett.voxel.world.blocks;

import com.brett.renderer.datatypes.ModelVAO;

/**
*
* @author brett
* @date Feb. 18, 2020
*/

public class Voxel {
	
	public int id = 0;
	public ModelVAO model;
	
	public Voxel(int id, ModelVAO model) {
		this.id = id;
		this.model = model;
	}
	
}
