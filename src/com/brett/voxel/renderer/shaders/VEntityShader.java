package com.brett.voxel.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;

import com.brett.renderer.shaders.ShaderProgram;
import com.brett.tools.Maths;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Apr. 10, 2020
*/

public class VEntityShader extends ShaderProgram {

	private static final String VERTEX_FILE = "vEntityVertexShader.vert";
	private static final String FRAGMENT_FILE = "vEntityFragmentShader.frag";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public VEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("translationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	public void loadTranslationMatrix(Matrix4f trans) {
		super.loadMatrix(location_transformationMatrix, trans);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
}
