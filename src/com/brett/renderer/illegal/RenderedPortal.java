package com.brett.renderer.illegal;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Fbo;
import com.brett.renderer.Loader;
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
	
	@SuppressWarnings("unused")
	private Loader loader;
	private Camera camera;
	private RawModel model;
	private Vector3f inrotation;
	private Vector3f outrotation;
	private Vector3f inscale;
	private Vector3f outscale;
	private Vector3f inpos;
	private Vector3f outpos;
	private Fbo front;
	private Fbo back;
	private RenderedShader shader;
	
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
	public RenderedPortal(Loader loader, Camera camera, Matrix4f perspective, RawModel model, Vector3f inpos, Vector3f outpos,  
			Vector3f inrotation, Vector3f outrotation, Vector3f inscale, Vector3f outscale) {
		this.loader = loader;
		shader = new RenderedShader();
		shader.start();
		shader.loadProjectionMatrix(perspective);
		shader.stop();
		this.model = loader.loadToVAO(planePoints, 3);
		temp = new Vector3f(0.0f,0.0f,0.0f);
		this.inrotation = inrotation;
		this.outrotation = outrotation;
		this.inscale = inscale;
		this.outscale = outscale;
		this.inpos = inpos;
		this.outpos = outpos;
		this.camera = camera;
		this.front = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		this.back = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		Main.ui.addUITexture(back.getColourTexture(), -1, -1, 0, 0, 500, 500);
	}
	
	public void prepareRenderFrontFBO() {
		front.bindFrameBuffer();
		temp = camera.getPosition();
		camera.setPosition(outpos);
	}
	
	public void unbindFrontFBO() {
		front.unbindFrameBuffer();
		camera.setPosition(temp);
	}
	
	public void prepareRenderBackFBO() {
		back.bindFrameBuffer();
		temp = camera.getPosition();
		camera.setPosition(inpos);
	}
	
	public void unbindBackFBO() {
		back.unbindFrameBuffer();
		camera.setPosition(temp);
	}
	
	public void render() {
		this.shader.start();
		this.shader.loadViewMatrix(camera);
		MasterRenderer.disableCulling();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, front.getColourTexture());
		this.shader.loadTranslationMatrix(inpos, inrotation, inscale);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, back.getColourTexture());
		this.shader.loadTranslationMatrix(outpos, outrotation, outscale);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		MasterRenderer.enableCulling();
		this.shader.stop();
	}
	
	public void cleanup() {
		this.shader.cleanUp();
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
}
