package com.brett.engine.shaders;

/**
 * @author Brett
 * @date Jun. 21, 2020
 */

public class VoxelShader extends WorldShader {

	public VoxelShader() {
		super("voxel.vert", "voxel.frag");
	}
	
	public VoxelShader(String f1, String f2) {
		super(f1, f2);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "data");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}

}
