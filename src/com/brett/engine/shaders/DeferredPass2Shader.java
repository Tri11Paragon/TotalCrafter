package com.brett.engine.shaders;

import org.joml.Vector3d;

/**
* @author Brett
* @date 14-Jun-2021
*/

public class DeferredPass2Shader extends WorldShader {

	public DeferredPass2Shader() {
		super("deferred_pass2.vert", "deferred_pass2.frag");
	}
	
	private int location_gposition;
	private int location_gnormal;
	private int location_gcolor;
	private int location_amount;
	private int location_view;

	public static final int MAX_LIGHTS = 32;
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_gposition = getUniformLocation("gPosition");
		location_gnormal = getUniformLocation("gNormal");
		location_gcolor = getUniformLocation("gAlbedoSpec");
		location_amount = super.getUniformLocation("lightAmount");
		location_view = super.getUniformLocation("viewPos");
	}
	
	public void loadLightAmount(float amount) {
		super.loadFloat(location_amount, amount);
	}

	public void loadViewPos(Vector3d pos) {
		super.loadVector(location_view, pos);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_gposition, 0);
		super.loadInt(location_gnormal, 1);
		super.loadInt(location_gcolor, 2);
	}
	
}
