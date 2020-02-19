package com.brett.renderer.world;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.shaders.SkyboxShader;
import com.brett.world.cameras.Camera;

public class SkyboxRenderer {

	private static final float SIZE = 512f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,
	    
	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,
	    
	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private HashMap<String, String[]> cubemaps = new HashMap<String, String[]>();
	
	private static float ROTATE_SPEED = 0.1f;
	private float rotation = 0;
	
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix, float r, float g, float b) {
		registerCubeMaps();
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(cubemaps.get("day"));
		nightTexture = loader.loadCubeMap(cubemaps.get("night"));
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadFogColor(r, g, b);
		shader.stop();
	}
	
	public void render(Camera camera) {
		GL11.glDepthMask(false);
		//GL11.glDepthRange(1f, 1f);
		shader.start();
		rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
		shader.loadViewMatrix(camera, rotation);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
		//GL11.glDepthRange(0f, 1f);
		GL11.glDepthMask(true);
	}
	
	private void bindTextures() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexture);
		shader.loadBlendFactor(0f);
	}
	
	// this keeps tracks of all the cube maps (allows for the user to add atop of the default game cubemaps)
	private void registerCubeMaps() {
		cubemaps.put("day", new String[]{"right", "left", "top", "bottom", "back", "front"});
		cubemaps.put("night", new String[]{"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"});
		//cubemaps.put("will1", new String[]{"W1right", "W1left", "W1top", "W1bottom", "W1back", "W1front"});
		cubemaps.put("will2", new String[]{"W2right", "W2left", "W2top", "W2bottom", "W2front", "W2back"});
		//cubemaps.put("will3", new String[]{"W3right", "W3left", "W3top", "W3bottom", "W3back", "W3front"});
	}
	
}
