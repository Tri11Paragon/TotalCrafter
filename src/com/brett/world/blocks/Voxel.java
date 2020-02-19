package com.brett.world.blocks;

import com.brett.renderer.datatypes.RawModel;

/**
*
* @author brett
* @date Feb. 18, 2020
*/

public class Voxel {
	
	public int id = 0;
	public RawModel model;
	
	public Voxel(int id, RawModel model) {
		this.id = id;
		this.model = model;
	}
	
}
