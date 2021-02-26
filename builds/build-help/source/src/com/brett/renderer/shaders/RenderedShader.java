package com.brett.renderer.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.brett.cameras.Camera;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Feb. 27, 2020
* just another standard shader, but like this is for the illegal geometry I was working on
* This is not used.
*/
public class RenderedShader extends ShaderProgram {

	private static final String VERTEX_FILE = "renderedShader.vert";
	private static final String FRAGMENT_FILE = "renderedShader.frag";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_translationMatrix;
	
	public RenderedShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_translationMatrix = super.getUniformLocation("translationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoord");
	}

	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadTranslationMatrix(Vector3f pos, Vector3f rotation, Vector3f scale) {
		super.loadMatrix(location_translationMatrix, Maths.createTransformationMatrix(pos, rotation, scale));
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
}
