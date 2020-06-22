package com.brett.engine.shaders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Matrix4f;

import com.brett.engine.managers.DisplayManager;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ProjectionMatrix {
	
	public static float FOV = 90;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	public static Matrix4f projectionMatrix;
	
	public static HashMap<Integer, WorldShader> shaders = new HashMap<Integer, WorldShader>();
	private static int lastIndex = 0;
	
	public static void updateProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
    	// TODO: this
		float aspectRatio = (float) DisplayManager.WIDTH / (float) DisplayManager.HEIGHT;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		
		Iterator<Entry<Integer, WorldShader>> shaderIt = shaders.entrySet().iterator();
		
		while (shaderIt.hasNext()) {
			WorldShader localShader = shaderIt.next().getValue(); 
			localShader.start();
			localShader.loadProjectionMatrox(projectionMatrix);
			localShader.stop();
		}
		
    }
	
	public static void addShader(WorldShader shader) {
		shaders.put(lastIndex, shader);
		lastIndex++;
	}
	
}
