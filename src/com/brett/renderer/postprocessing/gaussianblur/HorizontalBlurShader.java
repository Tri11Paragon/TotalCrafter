package com.brett.renderer.postprocessing.gaussianblur;

import com.brett.renderer.shaders.ShaderProgram;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing class (2020-6-11)
 *
 */
public class HorizontalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "yes/horizontalBlurVertex.vert";
	private static final String FRAGMENT_FILE = "yes/blurFragment.frag";
	
	private int location_targetWidth;
	
	protected HorizontalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void loadTargetWidth(float width){
		super.loadFloat(location_targetWidth, width);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_targetWidth = super.getUniformLocation("targetWidth");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
