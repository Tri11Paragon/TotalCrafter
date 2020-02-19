package com.brett.renderer.postprocessing;

import com.brett.renderer.shaders.ShaderProgram;

/**
*
* @author brett
*
*/

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "contrastVertex.txt";
	private static final String FRAGMENT_FILE = "contrastFragment.txt";
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
