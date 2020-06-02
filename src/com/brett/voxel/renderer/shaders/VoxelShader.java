package com.brett.voxel.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;
import com.brett.renderer.shaders.ShaderProgram;

// maybe add some comments?

public class VoxelShader extends ShaderProgram {

	private static final String VERTEX_FILE = "voxelVertexShader.vert";
	private static final String FRAGMENT_FILE = "voxelFragmentShader.frag";
	
	private int location_transformationMatrix;

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
	}

	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
}
