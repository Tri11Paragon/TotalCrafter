package com.brett.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;
import com.brett.renderer.shaders.ShaderProgram;
import com.brett.tools.Maths;
import com.brett.world.cameras.ICamera;

// maybe add some comments?

public class VoxelShader extends ShaderProgram {

	private static final String VERTEX_FILE = "voxelVertexShader.txt";
	private static final String GEOMETRY_FILE = "voxelGeometryShader.txt";
	private static final String FRAGMENT_FILE = "voxelFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lup;
	private int location_ldown;
	private int location_lleft;
	private int location_lright;
	private int location_lfront;
	private int location_lback;

	public VoxelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, GEOMETRY_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lup = super.getUniformLocation("lup");
		location_ldown = super.getUniformLocation("ldown");
		location_lleft = super.getUniformLocation("lleft");
		location_lright = super.getUniformLocation("lright");
		location_lfront = super.getUniformLocation("lback");
		location_lback = super.getUniformLocation("lfront");
	}
	
	public void loadLightData(float up, float down, float left, float right, float back, float front) {
		// bad way of loading light data
		// as GPUs like large amounts of data at a time
		// might be better to load a matrix with this, and the position.
		// then do translation matrix on the GPU.
		super.loadFloat(location_lup, up);
		super.loadFloat(location_ldown, down);
		super.loadFloat(location_lleft, left);
		super.loadFloat(location_lright, right);
		super.loadFloat(location_lfront, front);
		super.loadFloat(location_lback, back);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(ICamera camera){
		Matrix4f viewMatrix = Maths.createViewMatrixOTHER(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
