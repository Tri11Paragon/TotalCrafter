package com.brett.renderer.postprocessing.bloom;

import com.brett.renderer.shaders.ShaderProgram;

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
