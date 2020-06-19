package com.brett.voxel.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;

import com.brett.renderer.shaders.ShaderProgram;

// maybe add some comments?

/**
 * @author brett
 * One of the first voxel class. early feb? jan? idk
 * Also a pretty standard shader
 */
public class VoxelShader extends ShaderProgram {

	private static final String VERTEX_FILE = "voxelVertexShader.vert";
	private static final String FRAGMENT_FILE = "voxelFragmentShader.frag";
	
	private int location_transformationMatrix;
	private int location_project;
	private int location_view;

	public VoxelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(0, "textureCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_project = super.getUniformLocation("projection");
		location_view = super.getUniformLocation("view");
	}

	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_project, projection);
	}
	
	public void loadViewMatrix(Matrix4f view){
		super.loadMatrix(location_view, view);
	}
	
}
