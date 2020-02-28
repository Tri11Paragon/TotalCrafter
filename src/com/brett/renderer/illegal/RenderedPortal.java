package com.brett.renderer.illegal;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Fbo;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.shaders.RenderedShader;
import com.brett.world.cameras.Camera;
import com.tester.Main;

/**
*
* @author brett
* @date Feb. 27, 2020
*/

public class RenderedPortal {
	
	private Vector3f temp;
	private Vector3f temp2;
	
	private Vector3f inrotation;
	private Vector3f outrotation;
	private Vector3f inscale;
	private Vector3f outscale;
	private Vector3f inpos;
	private Vector3f outpos;
	private Fbo front;
	private Fbo back;
	private RenderedShader shader;
	
	private int vao;
	private int vbo;
	private int vbo2;
	
	/**
	 * @param loader 
	 * @param camera 
	 * @param perspective 
	 * @param model 
	 * @param rotation 
	 * @param scale 
	 * @param inpos 
	 * @param outpos 
	 */
	public RenderedPortal(Matrix4f perspective, RawModel model, Vector3f inpos, Vector3f outpos,  
			Vector3f inrotation, Vector3f outrotation, Vector3f inscale, Vector3f outscale) {
		shader = new RenderedShader();
		shader.start();
		shader.loadProjectionMatrix(perspective);
		shader.stop();
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		vbo = storeDataInAttributeList(0, 3, planePoints);
		vbo2 = storeDataInAttributeList(1, 2, planeUVs);
		GL30.glBindVertexArray(0);
		temp = new Vector3f(0.0f,0.0f,0.0f);
		temp2 = new Vector3f(0.0f,0.0f,0.0f);
		this.inrotation = inrotation;
		this.outrotation = outrotation;
		this.inscale = inscale;
		this.outscale = outscale;
		this.inpos = inpos;
		this.outpos = outpos;
		this.front = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		this.back = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		Main.ui.addUITexture(front.getColourTexture(), -1, -1, 0, 0, 500, 500);
	}
	
	public void prepareRenderFrontFBO(Camera camera) {
		temp = protectVectors(camera.getPosition());
		front.bindFrameBuffer();
		camera.getPosition().x = (outpos.x);
		camera.getPosition().y = (outpos.y);
		camera.getPosition().z = (outpos.z);
	}
	
	public void unbindFrontFBO(Camera camera) {
		front.unbindFrameBuffer();
		//camera.setPosition(temp);
		camera.getPosition().x = (temp.x);
		camera.getPosition().y = (temp.y);
		camera.getPosition().z = (temp.z);
	}
	
	public void prepareRenderBackFBO(Camera camera) {
		back.bindFrameBuffer();
		temp2 = protectVectors(camera.getPosition());
		camera.getPosition().x = (inpos.x);
		camera.getPosition().y = (inpos.y);
		camera.getPosition().z = (inpos.z);
	}
	
	public void unbindBackFBO(Camera camera) {
		back.unbindFrameBuffer();
		//camera.setPosition(temp2);
		camera.getPosition().x = (temp2.x);
		camera.getPosition().y = (temp2.y);
		camera.getPosition().z = (temp2.z);
	}
	
	public void render(Camera camera) {
		this.shader.start();
		this.shader.loadViewMatrix(camera);
		MasterRenderer.disableCulling();
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		this.shader.loadTranslationMatrix(inpos, inrotation, inscale);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, front.getColourTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, planePoints.length/3);
		
		//this.shader.loadTranslationMatrix(outpos, outrotation, outscale);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, back.getColourTexture());
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, planePoints.length/3);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		MasterRenderer.enableCulling();
		this.shader.stop();
	}
	
	public void cleanup() {
		this.shader.cleanUp();
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
		GL15.glDeleteBuffers(vbo2);
	}
	
	private float[] planePoints = {
			-1f, 1f, 0.0f,
		     -1f, -1f, 0.0f,
		     1f,  -1f, 0.0f,
		     -1f, 1f, 0.0f,
		     1f, -1f, 0.0f,
		     1f,  1f, 0.0f
	};  
	
	private float[] planeUVs = {
			0, 1,
			1, 1,
			1, 0,
			0, 1,
			1, 0,
			1, 1
	};
	
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
	
	private Vector3f protectVectors(Vector3f v) {
		return new Vector3f(v);
	}
	
}
