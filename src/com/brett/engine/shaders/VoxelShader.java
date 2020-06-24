package com.brett.engine.shaders;

/**
 * @author Brett
 * @date Jun. 21, 2020
 */

public class VoxelShader extends WorldShader {

	public VoxelShader() {
		super("voxel.vert", "voxel.frag");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttributes();
		bindAttribute(0, "position");
		bindAttribute(1, "data");
	}
	
}
