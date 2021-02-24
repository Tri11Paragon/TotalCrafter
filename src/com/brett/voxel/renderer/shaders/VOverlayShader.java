package com.brett.voxel.renderer.shaders;

import org.joml.Matrix4f;

import com.brett.cameras.ICamera;
import com.brett.renderer.shaders.WorldShader;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Apr. 14, 2020
* Overlay shader for blocks. Also pretty standard shader.
*/

public class VOverlayShader extends WorldShader {
	
	private static final String VERTEX_FILE = "voxelOverlayVertexShader.vert";
	private static final String FRAGMENT_FILE = "voxelOverlayFragmentShader.frag";

	
	public VOverlayShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_translationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(ICamera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	
}
