/** 
*	Brett Terpstra
*	Feb 25, 2020
*	
*/ 

package com.brett.renderer.shaders;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.tools.Maths;
import com.brett.world.cameras.Camera;

public class LineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "lineVertexShader.txt";
	private static final String FRAGMENT_FILE = "lineFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	public LineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}
	
	public void render(Vector3f pos1, Vector3f pos2) {
		this.start();
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		float[] f = {pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z};
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(f);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0,3,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL20.glEnableVertexAttribArray(0);
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrixOTHER(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
