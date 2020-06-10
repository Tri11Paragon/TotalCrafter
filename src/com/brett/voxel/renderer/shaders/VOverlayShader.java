package com.brett.voxel.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;

import com.brett.cameras.ICamera;
import com.brett.renderer.shaders.ShaderProgram;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Apr. 14, 2020
*/

public class VOverlayShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "voxelOverlayVertexShader.vert";
	private static final String FRAGMENT_FILE = "voxelOverlayFragmentShader.frag";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	
	public VOverlayShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(ICamera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	
}
