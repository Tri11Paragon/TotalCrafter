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
	private static final int MAX_JOINTS = 50;
	
	private int location_jointTransforms[];
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public VEntityShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_jointTransforms = new int[MAX_JOINTS];
		for (int i = 0; i < MAX_JOINTS; i++) {
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms["+i+"]");
		}
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "jointIndices");
		super.bindAttribute(4, "weights");
	}
	
	public void loadMatrixArray(Matrix4f[] matrix) {
		for(int i = 0; i < matrix.length; i++){
			super.loadMatrix(location_jointTransforms[i], matrix[i]);
		}
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
}
