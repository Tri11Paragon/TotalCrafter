package com.brett.voxel.renderer.shaders;

import org.joml.Matrix4f;

import com.brett.cameras.Camera;
import com.brett.renderer.shaders.WorldShader;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Apr. 10, 2020
* pretty standard shader.
*/

public class VEntityShader extends WorldShader {

	private static final String VERTEX_FILE = "vEntityVertexShader.vert";
	private static final String FRAGMENT_FILE = "vEntityFragmentShader.frag";
	
	public VEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}

	@Override
	protected void bindAttributes() {
		// normal setups
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	public void loadTranslationMatrix(Matrix4f trans) {
		super.loadMatrix(location_translationMatrix, trans);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
}
