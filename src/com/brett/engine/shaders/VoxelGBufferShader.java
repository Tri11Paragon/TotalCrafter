package com.brett.engine.shaders;

/**
* @author Brett
* @date Jul. 11, 2020
*/

public class VoxelGBufferShader extends WorldShader {

	public VoxelGBufferShader() {
		super("g_buffer.vert", "g_buffer.frag");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "data");
		bindAttribute(2, "normals");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}

}
