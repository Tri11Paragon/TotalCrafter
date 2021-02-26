package com.brett.renderer.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing class (2020-6-11)
 *
 */
public class ContrastChanger {

	private ImageRenderer renderer;
	private ContrastShader shader;
	
	public ContrastChanger() {
		shader = new ContrastShader();
		renderer = new ImageRenderer();
	}
	
	public void render(int texture) {
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
	}
	
	public void cleanUp() {
		renderer.cleanUp();
		shader.cleanUp();
	}
	
}
