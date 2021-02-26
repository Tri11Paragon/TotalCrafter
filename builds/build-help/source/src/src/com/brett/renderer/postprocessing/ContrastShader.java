package com.brett.renderer.postprocessing;

import com.brett.renderer.shaders.ShaderProgram;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing class (2020-6-11)
 *
 */
public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "contrastVertex.vert";
	private static final String FRAGMENT_FILE = "contrastFragment.frag";
	
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
