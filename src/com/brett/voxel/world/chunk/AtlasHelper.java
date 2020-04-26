package com.brett.voxel.world.chunk;

/**
*
* @author brett
* @date Apr. 22, 2020
*/

public class AtlasHelper {
	
	private static int textureSize = 32;
	
	public static void init() {
		
	}
	
	public static float[] convertToIndex(float[] oldCoords, int i) {
		boolean b = false;
		if (b)
			return oldCoords;
		float column = i % textureSize;
		float xOff = column / textureSize;
		float row = (float)Math.floor(i / textureSize);
		float yOff = row /textureSize;
		float[] newCoords = new float[oldCoords.length];
		for (int j = 0; j < oldCoords.length; j+=2) {
			newCoords[j] = oldCoords[j] / textureSize + xOff;
			newCoords[j+1] = oldCoords[j+1] / textureSize + yOff;
		}
		return newCoords;
	}
	
	public static float[] createUVTop(int b, int i) {
		float[] uvTopComplete = {
				i, b,
				b, i, 
				b, b,
				
				i, b,
				i, i,
				b, i,
		};
		return uvTopComplete;
	}
	
	public static float[] createUVBottom(int b, int i) {
		float[] uvBottomComplete = {
				i, b, 
				b, b,
				b, i, 
				
				i, b,
				b, i,
				i, i
		};
		return uvBottomComplete;
	}
	
	public static float[] createUVLeft(int b, int i) {
		float[] uvLeftComplete = {
				b, i, 
				i, i,
				i, b, 
				
				i, b,
				b, b,
				b, i,
		};
		return uvLeftComplete;
	}
	
	public static float[] createUVFront(int b, int i) {
		float[] uvFrontComplete = {
				b, i, 
				i, i,
				b, b, 
				
				b, b,
				i, i,
				i, b
		};
		return uvFrontComplete;
	}
	
	public static float[] createUVBack(int b, int i) {
		float[] uvBackComplete = {
				i, i, 
				i, b,
				b, b, 
			
				b, b,
				b, i,
				i, i,
		};
		return uvBackComplete;
	}
	
	public static float[] createUVRight(int b, int i) {
		float[] uvRightComplete = {
				i, i,
				i, b, 
				b, i,
				
				b, i,
				i, b,
				b, b,
		};
		return uvRightComplete;
	}
	
}
