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

/**
*
* @author brett
* @date Feb. 25, 2020
*/

public class PointShader extends ShaderProgram {

	private int vao = 0;
	private int count = 0;
	private int vbo = 0;
	
	private static final String VERTEX_FILE = "pointVertexShader.txt";
	private static final String FRAGMENT_FILE = "pointFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_translationMatrix;
	
	public PointShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		this.start();
		this.loadTranslationMatrix();
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_translationMatrix = super.getUniformLocation("translationMatrix");
	}
	
	// TODO: add persistant lines
	public void createStaticPoints(Vector3f[] points) {
		if (vao != 0)
			GL30.glDeleteVertexArrays(vao);
		if (vbo != 0)
			GL15.glDeleteBuffers(vbo);
		vao = 0;
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		float[] f = new float[points.length*3];
		for (int i = 0; i < f.length/3; i+=3) {
			if (points[i] == null)
				continue;
			f[i] = points[i].x;
			f[i+1] = points[i].y;
			f[i+2] = points[i].z;
		}
		count = f.length;
		vbo = 0;
		vbo = storeDataInAttributeList(0, 3, f);
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * renderers the last static line.
	 */
	public void render() {
		if (vao == 0)
			return;
		this.start();
		GL11.glPointSize(500);
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL11.glDrawArrays(GL11.GL_POINTS, 0, count);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		this.stop();
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private int storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadTranslationMatrix() {
		super.loadMatrix(location_translationMatrix, Maths.createTransformationMatrix(new Vector3f(0,0,0), 0, 0, 0, 1));
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}

