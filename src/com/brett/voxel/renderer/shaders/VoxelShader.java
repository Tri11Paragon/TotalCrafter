package com.brett.voxel.renderer.shaders;

import com.brett.renderer.shaders.WorldShader;

// maybe add some comments?

/**
 * @author brett
 * One of the first voxel class. early feb? jan? idk
 * Also a pretty standard shader
 */
public class VoxelShader extends WorldShader {

	private static final String VERTEX_FILE = "voxelVertexShader.vert";
	private static final String FRAGMENT_FILE = "voxelFragmentShader.frag";
	
	public VoxelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		System.out.println(location_projectionMatrix);
		System.out.println(location_translationMatrix);
		System.out.println(location_viewMatrix);
	}
	
}
