package com.brett.renderer.shaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

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
	private int vbo = 0;
	private float scale = 0;
	private Vector3f[] point = null;
	
	private List<Vector3f> points = new ArrayList<Vector3f>();
	private float[] plane = {
			-0.5f, 0.5f, 0.0f,
		     -0.5f, -0.5f, 0.0f,
		     0.5f,  -0.5f, 0.0f,
		     -0.5f, 0.5f, 0.0f,
		     0.5f, -0.5f, 0.0f,
		     0.5f,  0.5f, 0.0f
		};  
	
	private static final String VERTEX_FILE = "pointVertexShader.txt";
	private static final String FRAGMENT_FILE = "pointFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_translationMatrix;
	private int location_time;
	private int location_scale;
	private float timeSinceStart = 0;
	
	public PointShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		vbo = storeDataInAttributeList(0, 3, plane);
		GL30.glBindVertexArray(0);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_translationMatrix = super.getUniformLocation("translationMatrix");
		location_time = super.getUniformLocation("time");
		location_scale = super.getUniformLocation("scale");
	}
	
	/**
	 * This draws only one line and will erase the old line drawn with this function.
	 */
	public void createStaticPoints(Vector3f[] points, float scale) {
		this.point = points;
		this.scale = scale;
	}
	
	/**
	 * renderers the last static line.
	 */
	public void render() {
		timeSinceStart += 0.049230f/4;
		this.start();
		super.loadFloat(location_time, timeSinceStart);
		if (point != null) {
			GL30.glBindVertexArray(vao);
			GL20.glEnableVertexAttribArray(0);
			for (int i = 0; i < point.length; i++) {
				if (point[i] == null)
					continue;
				this.loadTranslationMatrix(point[i]);
				this.loadFloat(location_scale, scale);
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, plane.length);
			}
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
		}
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
	
	public void loadTranslationMatrix(Vector3f pos) {
		super.loadMatrix(location_translationMatrix, Maths.createTransformationMatrix(pos));
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}

