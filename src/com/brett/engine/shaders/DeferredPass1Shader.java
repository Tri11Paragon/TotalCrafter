package com.brett.engine.shaders;

/**
* @author Brett
* @date 13-Jun-2021
*/

public class DeferredPass1Shader extends WorldShader {

	public DeferredPass1Shader() {
		super("deferred_pass1.vert", "deferred_pass1.frag");
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "data");
		bindAttribute(2, "normal");
		bindAttribute(3, "scale");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}
	
}
