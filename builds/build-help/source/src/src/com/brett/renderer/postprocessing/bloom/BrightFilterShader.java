package com.brett.renderer.postprocessing.bloom;

import com.brett.renderer.shaders.ShaderProgram;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing class (2020-6-11)
 *
 */
public class BrightFilterShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "simpleVertex.vert";
	private static final String FRAGMENT_FILE = "brightFilterFragment.frag";
	
	public BrightFilterShader() {
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
