package com.brett.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;

public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "yes/shadowVertexShader.vert";
	private static final String FRAGMENT_FILE = "yes/shadowFragmentShader.frag";
	
	private int location_mvpMatrix;

	public ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	public void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}
	
	public void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
	}

}
