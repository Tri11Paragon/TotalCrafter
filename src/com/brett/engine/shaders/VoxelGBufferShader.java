package com.brett.engine.shaders;

import org.joml.Vector3d;

/**
 * @author Brett
 * @date Jul. 11, 2020
 */

public class VoxelGBufferShader extends WorldShader {

	private int location_cam;

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
		location_cam = super.getUniformLocation("cameraPos");
	}
	
	public void loadCamera(Vector3d pos) {
		super.loadVector(location_cam, pos);
	}

}
