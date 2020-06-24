package com.brett.engine.ui.font.fontRendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.brett.engine.shaders.ShaderProgram;

/**
 * @author brett
 * pretty standard shader
 * please refer to the shader program for information about the functions
 */
public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "fontVertex.vert";
	private static final String FRAGMENT_FILE = "fontFragment.frag";
	
	private int location_color;
	private int location_outline;
	private int location_projectionMatrix;
	private int location_translationMatrix;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_outline = super.getUniformLocation("outlineColor");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_translationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	protected void loadColor(Vector3f color){
		super.loadVector(location_color, color);
	}
	
	protected void loadColorOutline(Vector3f color){
		super.loadVector(location_outline, color);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadTranslationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_translationMatrix, matrix);
	}
}
