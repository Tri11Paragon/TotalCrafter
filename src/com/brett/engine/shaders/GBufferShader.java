package com.brett.engine.shaders;

/**
 * @author Brett
 * @date Jul. 11, 2020
 */

public class GBufferShader extends WorldShader {

	public GBufferShader() {
		super("de_shader.vert", "de_shader.frag");
	}
	
	private int location_gposition;
	private int location_gnormal;
	private int location_gcolor;
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_gposition = getUniformLocation("gPosition");
		location_gnormal = getUniformLocation("gNormal");
		location_gcolor = getUniformLocation("gAlbedoSpec");
	}

	public void connectTextureUnits() {
		super.loadInt(location_gposition, 2);
		super.loadInt(location_gnormal, 1);
		super.loadInt(location_gcolor, 0);
	}

}
