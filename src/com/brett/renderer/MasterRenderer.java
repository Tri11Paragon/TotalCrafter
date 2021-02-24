package com.brett.renderer;

import org.lwjgl.opengl.GL11;
import com.brett.cameras.Camera;

/**
 * @author brett
 * used to be the master renderer
 * now it just handles projection matrix.
 */
public class MasterRenderer {

	public static float FOV = 90;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	public static final int DRAWMODE = GL11.GL_TRIANGLES;
	public static Loader global_loader;
	
	// fog color
	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public MasterRenderer(Loader loader, Camera cam){
		MasterRenderer.global_loader = loader;
		enableCulling();
		ProjectionMatrix.updateProjectionMatrix();
	}
	
	/**
	 * creates a neat projection matrix
	 */
	
	// old projection matrix code.
	/*private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}*/
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void enableTransparentcy() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableTransparentcy() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
}
