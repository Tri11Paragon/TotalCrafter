/**package com.brett.renderer.illegal;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.cameras.Camera;
import com.brett.datatypes.ModelVAO;
import com.brett.renderer.Fbo;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.shaders.RenderedShader;
import com.brett.tools.Maths;

/**
*
* @author brett
* @date Feb. 27, 2020
* 
* This is currently put on hold.
* PLEASE IGNORE THIS CLASS AS IT IS NOT BEING USED.
* its just me testing some non-euclidean geometry.
*/

/**public class RenderedPortal {
	
	private Vector3f temp;
	private Vector3f temp2;
	private float yaw;
	private float pitch;
	private float roll;
	
	private Vector3f inrotation;
	private Vector3f outrotation;
	private Vector3f inscale;
	private Vector3f outscale;
	private Vector3f inpos;
	private Vector3f outpos;
	private Fbo front;
	private Fbo back;
	private RenderedShader shader;
	private Camera camera;
	
	private int vao;
	private int vbo;
	private int vbo2;
	
	public RenderedPortal(Camera camera, Matrix4f perspective, ModelVAO model, Vector3f inpos, Vector3f outpos,  
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
		this.camera = camera;
		this.inrotation = inrotation;
		this.outrotation = outrotation;
		this.inscale = inscale;
		this.outscale = outscale;
		this.inpos = inpos;
		this.outpos = outpos;
		this.front = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);
		this.back = new Fbo(DisplayManager.WIDTH, DisplayManager.HEIGHT, Fbo.DEPTH_RENDER_BUFFER);
	}
	
	public Vector3f prepareRenderFrontFBO() {
		temp = protectVectors(camera.getPosition());
		front.bindFrameBuffer();
		Vector3f cameraYawHyp = Maths.distance(temp, inpos);
		Vector3f.add(outpos, cameraYawHyp, camera.getPosition());
		yaw=camera.getYaw();
		pitch=camera.getPitch();
		roll=camera.getRoll();
		float angle = (float) Math.toDegrees(Math.atan(cameraYawHyp.x / cameraYawHyp.z));
		if (angle < 0)
			angle = 360 - (1-angle);
		camera.setYawPitchRoll(outrotation.y - angle, outrotation.x, outrotation.z);
		return camera.getPosition();
	}
	
	public void unbindFrontFBO() {
		front.unbindFrameBuffer();
		//camera.setPosition(temp);
		camera.setPosition(temp);
		camera.setYawPitchRoll(yaw, pitch, roll);
	}
	
	public Vector3f prepareRenderBackFBO() {
		back.bindFrameBuffer();
		temp2 = protectVectors(camera.getPosition());
		Vector3f cameraYawHyp = Maths.distance(temp2, outpos);
		Vector3f.add(inpos, cameraYawHyp, camera.getPosition());
		yaw=camera.getYaw();
		pitch=camera.getPitch();
		roll=camera.getRoll();
		float angle = (float) Math.toDegrees(Math.atan(cameraYawHyp.x / cameraYawHyp.z));
		if (angle < 0)
			angle = 360 - (1-angle);
		camera.setYawPitchRoll(inrotation.y - angle, inrotation.x, inrotation.z);
		return camera.getPosition();
	}
	
	public void unbindBackFBO() {
		back.unbindFrameBuffer();
		camera.setPosition(temp2);
		camera.setYawPitchRoll(yaw, pitch, roll);
	}
	
	public void render() {
		this.shader.start();
		this.shader.loadViewMatrix(camera);
		MasterRenderer.disableCulling();
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		this.shader.loadTranslationMatrix(inpos, inrotation, inscale);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, front.getColourTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, planePoints.length/3);
		
		this.shader.loadTranslationMatrix(outpos, outrotation, outscale);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, back.getColourTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, planePoints.length/3);
		
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
			0, 0,
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
	
}*/
