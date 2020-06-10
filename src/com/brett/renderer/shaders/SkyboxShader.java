package com.brett.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.cameras.ICamera;
import com.brett.tools.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "skyboxVertexShader.vert";
	private static final String FRAGMENT_FILE = "skyboxFragmentShader.frag";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubemap;
	private int location_cubemap2;
	private int location_blendFactor;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		
		location_cubemap = super.getUniformLocation("cubeMap");
		location_cubemap2 = super.getUniformLocation("cubeMap2");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_cubemap, 0);
		super.loadInt(location_cubemap2, 1);
	}
	
	public void loadBlendFactor(float f) {
		super.loadFloat(location_blendFactor, f);
	}

	public void loadViewMatrix(ICamera camera, float rot){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		Matrix4f.rotate((float) Math.toRadians(rot), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	public void loadFogColor(float r, float g, float b) {
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}

}
