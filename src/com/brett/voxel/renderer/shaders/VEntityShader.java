package com.brett.voxel.renderer.shaders;

import com.brett.renderer.shaders.ShaderProgram;

/**
*
* @author brett
* @date Apr. 10, 2020
*/

public class VEntityShader extends ShaderProgram {

	private static final String VERTEX_FILE = "vEntityVertexShader.txt";
	private static final String FRAGMENT_FILE = "vEntityFragmentShader.txt";
	
	public VEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		
	}

	@Override
	protected void bindAttributes() {
		
	}
	
}
