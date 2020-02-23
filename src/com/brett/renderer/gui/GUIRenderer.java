package com.brett.renderer.gui;

import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.GUITexture;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.shaders.GUIShader;
import com.brett.tools.Maths;

public class GUIRenderer {
	
	private final RawModel quad;
	private GUIShader shader;
	private int SWIDTH = 800;
	private int SHEIGHT = 600;
	private Vector3f nullvec = new Vector3f(-1, 0, 0);
	
	public GUIRenderer(Loader loader) {
		SWIDTH = Display.getWidth();
		SHEIGHT = Display.getHeight();
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(positions);
		shader = new GUIShader();
	}
	
	public void render(List<UIElement> textures) {
		if (textures == null)
			return;
		shader.start();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for (int i = 0; i < textures.size(); i++) {
			UIElement texture = textures.get(i);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTexture());
			int tex2 = texture.getTexture2();
			if (tex2 > -1) {
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex2);
			}
			int tex3 = texture.getTexture3();
			if (tex3 > -1) {
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex3);
			}
			shader.loadTransformation(Maths.createTransformationMatrix(texture.getPosition(), texture.getScale()));
			Vector3f color = texture.getColor();
			if (color != null)
				shader.loadColor(color);
			else
				shader.loadColor(nullvec);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_CULL_FACE);
		shader.stop();
	}
	
	public void startrender() {
		shader.start();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	/**
	 * !!IMPORTANT!!
	 * startrender() needs to be called before and stoprender() needs to be called after
	 * note this can be after all rendering of dynamic gui is done.
	 */
	public void render(GUITexture textures, float x, float y, float width, float height) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.getTexture());
		int tex2 = textures.getTexture2();
		if (tex2 > -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex2);
		}
		int tex3 = textures.getTexture3();
		if (tex3 > -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex3);
		}
		shader.loadTransformation(Maths.createTransformationMatrix(SWIDTH, SHEIGHT, x, y, width, height));
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
	}
	
	public void stoprender() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_CULL_FACE);
		shader.stop();
	}
	
	public void cleanup() {
		shader.cleanUp();
	}
	
	
	// Thanks to https://en.wikipedia.org/wiki/Orthographic_projection
	// 2019-12-10 - no longer using projection matrix. using screen space calcualtions
	/*private void createProjectionMatrix() {
		pm = new Matrix4f();
		pm.setIdentity();
        this.pm.setIdentity();
        this.pm = Maths.ortho(0, Display.getWidth(), Display.getHeight(), 0, 0.0f, 1.0f);
	}*/
	
}