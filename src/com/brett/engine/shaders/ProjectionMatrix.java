package com.brett.engine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ProjectionMatrix {
	
	public static float FOV = 90;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	public static Matrix4f projectionMatrix;
	
	public static void updateProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
	
}
