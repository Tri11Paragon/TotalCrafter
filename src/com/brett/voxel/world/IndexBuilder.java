package com.brett.voxel.world;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;

/**
*
* @author brett
* @date Mar. 29, 2020
* lol this is actually the big brain stuff here
* 
* bigger lol. despite REDUCDING the amount of data stored in each
* VAO, this causes a lot of lag.
* ignore this class
* 
* it is big brain tho
* 
* 2020-04-23: even more useless with the new chunk builder
* so double ignore this class
* TODO: delete this
*/

public class IndexBuilder {
	
	public static RawModel buildMesh(SixBoolean b, Loader loader) {
		int[] ind = {};
		float[] verts = {};
		float[] uvs = {};
		int indexPos = 0;
		if (b.isLeft()) {
			verts = addArrays(verts, vertsLeft);
			uvs = addArrays(uvs, uvLeft);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		if (b.isRight()) {
			verts = addArrays(verts, vertsRight);
			uvs = addArrays(uvs, uvRight);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		if (b.isFront()) {
			verts = addArrays(verts, vertsFront);
			uvs = addArrays(uvs, uvFront);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		if (b.isBack()) {
			verts = addArrays(verts, vertsBack);
			uvs = addArrays(uvs, uvBack);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		if (b.isTop()) {
			verts = addArrays(verts, vertsTop);
			uvs = addArrays(uvs, uvTop);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		if (b.isBottom()) {
			verts = addArrays(verts, vertsBottom);
			uvs = addArrays(uvs, uvBottom);
			ind = addIndicies(ind, indicies, indexPos);
			indexPos += 4;
		}
		return loader.loadToVAO(verts, uvs, ind);
	}
	
	private static int[] addIndicies(int[] array1, int[] array2, int indexOffset) {
		int[] ria = new int[array1.length + array2.length];
		
		for (int i = 0; i < array1.length; i++) {
			ria[i] = array1[i];
		}
		
		for (int i = 0; i < array2.length; i++) {
			ria[i + array1.length] = array2[i] + indexOffset;
		}
		
		return ria;
	}
	
	/*
	 * Adds 2 float arrays. 
	 * nothing special needs to be done here.
	 */
	private static float[] addArrays(float[] array1, float[] array2) {
		float[] rtv = new float[array1.length + array2.length];
		
		for (int i = 0; i<array1.length;i++) {
			rtv[i] = array1[i];
		}
		
		for (int i = 0; i<array2.length; i++) {
			rtv[i + array1.length] = array2[i];
		}
		
		return rtv;
	}
	
	// appears like pre-offet all indicies are 0, 1, 2, 0, 2, 3
	// which makes sense.
	public static int[] indicies = {
			//7,3,2, 7,6,2
			0,  1,  2,  0,  2,  3,   //front
	}; 
	
	public static float[] vertsFront = {
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
	};
	
	public static float[] vertsBack = {
			-0.5f, -0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
	};
	
	public static float[] vertsRight = {
			0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, -0.5f,
	};
	
	public static float[] vertsLeft = {
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, -0.5f,
	};
	
	public static float[] vertsTop = {
			-0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
	}; 
	
	public static float[] vertsBottom = {
			-0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
	};
	
	public static float[] uvFront = {
			1, 1, 
			0, 1,
			0, 0, 
			1, 0,
	};
	
	public static float[] uvRight = {
			0, 0,
			1, 0,
			1, 1,
			0, 1,
	};
	
	public static float[] uvBack = {
			0, 0,
			0, 1,
			1, 1,
			1, 0,
	};

	public static float[] uvLeft = {
			1, 0,
			0, 0,
			0, 1,
			1, 1,
	};
	
	public static float[] uvTop = {
			1, 0, 
			0, 0,
			0, 1,
			1, 1,
	};
	
	public static float[] uvBottom = {
			1, 1,
			0, 1,
			0, 0,
			1, 0,
	};
	
}
