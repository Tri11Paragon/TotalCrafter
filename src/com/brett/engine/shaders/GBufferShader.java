package com.brett.engine.shaders;

import org.joml.Vector3d;

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

	public static final int MAX_LIGHTS = 128;
	
	private int[] location_light = new int[MAX_LIGHTS];
	private int location_amount;
	private int location_view;
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_light = new int[MAX_LIGHTS];
		location_gposition = getUniformLocation("gPosition");
		location_gnormal = getUniformLocation("gNormal");
		location_gcolor = getUniformLocation("gAlbedoSpec");
		location_amount = super.getUniformLocation("lightAmount");
		location_view = super.getUniformLocation("viewPos");
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_light[i] = super.getUniformLocation("lightPos[" + i + "]");
		}
	}

	public void loadLightData(int lightIndex, float x, float y, float z) {
		super.loadVector(location_light[lightIndex], x, y, z);
	}

	public void loadLightAmount(float amount) {
		super.loadFloat(location_amount, amount);
	}

	public void loadViewPos(Vector3d pos) {
		super.loadVector(location_view, pos);
	}

	public void connectTextureUnits() {
		super.loadInt(location_gposition, 2);
		super.loadInt(location_gnormal, 1);
		super.loadInt(location_gcolor, 0);
	}

}
