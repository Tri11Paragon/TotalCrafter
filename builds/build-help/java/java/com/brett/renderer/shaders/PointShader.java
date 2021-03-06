package com.brett.renderer.shaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.renderer.ProjectionMatrix;
import com.brett.tools.IKeyState;
import com.brett.tools.InputMaster;
import com.brett.tools.Maths;
import com.brett.tools.SettingsLoader;

/**
*
* @author brett
* @date Feb. 25, 2020
* THIS IS AN UNUSED CLASS PLEASE IGNORE
* I only ever use this if I need visual debugging.
* 
*/

public class PointShader extends WorldShader implements IKeyState {

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
	
	private static final String VERTEX_FILE = "pointVertexShader.vert";
	private static final String FRAGMENT_FILE = "pointFragmentShader.frag";
	
	private int location_time;
	private int location_scale;
	private float timeSinceStart = 0;
	
	public PointShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		vbo = storeDataInAttributeList(0, 3, plane);
		GL30.glBindVertexArray(0);
		InputMaster.keyboard.add(this);
		ProjectionMatrix.addShader(this);
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
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
	 * Changes the scale of the points
	 * @param scale
	 */
	public void changeScale(float scale) {
		this.scale = scale;
	}
	
	/**
	 * Adds a point to the world
	 * only can be cleared with the key set in settings
	 * @param point to be added
	 */
	public void addStaticPoint(Vector3f point) {
		this.points.add(point);
	}
	
	/**
	 * Adds a points to the world
	 * only can be cleared with the key set in settings
	 * @param points to be added
	 */
	public void addStaticPoints(Vector3f[] points) {
		for (int i = 0; i < points.length; i++)
			this.points.add(points[i]);
	}
	
	/**
	 * renderers the last static line.
	 */
	public void render(Matrix4f matrix) {
		timeSinceStart += 0.049230f/4;
		this.start();
		super.loadMatrix(location_viewMatrix, matrix);
		super.loadFloat(location_time, timeSinceStart);
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		if (point != null) {
			for (int i = 0; i < point.length; i++) {
				if (point[i] == null)
					continue;
				this.loadTranslationMatrix(point[i]);
				this.loadFloat(location_scale, scale);
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, plane.length/3);
			}
		}
		for (Vector3f v : points){
			if (v == null)
				continue;
			this.loadTranslationMatrix(v);
			this.loadFloat(location_scale, scale);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, plane.length/3);
		}
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
		InputMaster.keyboard.remove(this);
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		this.start();
		super.loadMatrix(location_projectionMatrix, projection);
		this.stop();
	}
	
	public void loadTranslationMatrix(Vector3f pos) {
		super.loadMatrix(location_translationMatrix, Maths.createTransformationMatrix(pos));
	}

	@Override
	public void onKeyPressed(int keys) {
		if (keys == SettingsLoader.KEY_CLEAR)
			points.clear();
	}

	@Override
	public void onKeyReleased(int keys) {
	}

}

