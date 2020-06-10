/** 
*	Brett Terpstra
*	Feb 13, 2020
*	
*/ 

package com.brett.voxel.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.datatypes.BlockModelVAO;
import com.brett.datatypes.SixBoolean;

public class MeshStore {
	
	// used to lock the models' map
	// 0 is no one is using this
	// 1 is main thread.
	// 2 is process thread.
	public static int modelsWritten = 0;
	
	public static List<SixBoolean> booleans = new ArrayList<SixBoolean>();
	
	public static Map<SixBoolean, BlockModelVAO> models = new HashMap<SixBoolean, BlockModelVAO>();
	public static int boolEmpty = 0;
	
	public static final float[] uvFrontComplete = {
			0, 1, 
			1, 1,
			0, 0, 
			
			0, 0,
			1, 1,
			1, 0
	};
	
	public static final float[] uvBackComplete = {
			1, 1, 
			1, 0,
			0, 0, 
		
			0, 0,
			0, 1,
			1, 1,
	};

	public static final float[] uvRightComplete = {
			1, 1,
			1, 0, 
			0, 1,
			
			0, 1,
			1, 0,
			0, 0,
	};
	
	public static final float[] uvLeftComplete = {
			0, 1, 
			1, 1,
			1, 0, 
			
			1, 0,
			0, 0,
			0, 1,
	};
	
	public static final float[] uvTopComplete = {
			1, 0,
			0, 1, 
			0, 0,
			
			1, 0,
			1, 1,
			0, 1,
	};
	
	public static final float[] uvBottomComplete = {
			1, 0, 
			0, 0,
			0, 1, 
			
			1, 0,
			0, 1,
			1, 1
	};
	
	public static final float[] uvFrontCompleteCompress = new float[uvFrontComplete.length/2];;
	public static final float[] uvBackCompleteCompress = new float[uvBackComplete.length/2];
	public static final float[] uvRightCompleteCompress = new float[uvRightComplete.length/2];
	public static final float[] uvLeftCompleteCompress = new float[uvLeftComplete.length/2];
	public static final float[] uvTopCompleteCompress = new float[uvTopComplete.length/2];
	public static final float[] uvBottomCompleteCompress = new float[uvBottomComplete.length/2];
	public static float[] uvFlowerCompleteCompress;
	
	public static void init() {
		for (int i = 0; i < uvFrontComplete.length; i+= 2) {
			float fr = ((int)uvFrontComplete[i] << 1) | (int)uvFrontComplete[i+1];
			uvFrontCompleteCompress[i/2] = fr;
		}
		for (int i = 0; i < uvBackComplete.length; i+= 2) {
			float fr = ((int)uvBackComplete[i] << 1) | (int)uvBackComplete[i+1];
			uvBackCompleteCompress[i/2] = fr;
		}
		for (int i = 0; i < uvRightComplete.length; i+= 2) {
			float fr = ((int)uvRightComplete[i] << 1) | (int)uvRightComplete[i+1];
			uvRightCompleteCompress[i/2] = fr;
		}
		for (int i = 0; i < uvLeftComplete.length; i+= 2) {
			float fr = ((int)uvLeftComplete[i] << 1) | (int)uvLeftComplete[i+1];
			uvLeftCompleteCompress[i/2] = fr;
		}
		for (int i = 0; i < uvTopComplete.length; i+= 2) {
			float fr = ((int)uvTopComplete[i] << 1) | (int)uvTopComplete[i+1];
			uvTopCompleteCompress[i/2] = fr;
		}
		for (int i = 0; i < uvBottomComplete.length; i+= 2) {
			float fr = ((int)uvBottomComplete[i] << 1) | (int)uvBottomComplete[i+1];
			uvBottomCompleteCompress[i/2] = fr;
		}
		uvFlowerCompleteCompress = new float[flowerUVs.length/2];
		for (int i = 0; i < flowerUVs.length; i+= 2) {
			int amt1,amt2;
			if (flowerUVs[i] > 0.5f)
				amt1 = 1;
			else
				amt1 = 0;
			if (flowerUVs[i+1] > 0.5f)
				amt2 = 1;
			else
				amt2 = 0;
			float fr = (amt1 << 1) | amt2;
			uvFlowerCompleteCompress[i/2] = fr;
		}
	}
	
	public static float[] updateCompression(float[] fa, byte light, int layer) {
		float[] tr = new float[fa.length];
		for (int i = 0; i < fa.length; i++) {
			int fai = (int)fa[i];
			int li = light << 2;
			int la = layer << 10;
			fai |= li;
			fai |= la;
			tr[i] = fai;
		}
		return tr;
	}
	
	private static float size = 1.0f;
	
	public static float[] vertsFrontComplete = {
			0, 0, size,
			size, 0, size,
			0, size, size,
			
			0, size, size,
			size, 0, size,
			size, size, size,
	};
	
	public static float[] vertsBackComplete = {
			0, 0, 0,
			0, size, 0,
			size, size, 0,
			
			size, size, 0,
			size, 0, 0,
			0, 0, 0,
	};
	
	public static float[] vertsRightComplete = {
			size, 0, 0,
			size, size, 0,
			size, 0, size,
			
			size, 0, size,
			size, size, 0,
			size, size, size,
	};
	
	public static float[] vertsLeftComplete = {
			0, 0, 0,
			0, 0, size,
			0, size, size,
			
			0, size, size,
			0, size, 0,
			0, 0, 0,
	};
	
	public static float[] vertsTopComplete = {
			size, size, size,
			0, size, 0,
			0, size, size,
			
			size, size, size,
			size, size, 0,
			0, size, 0,
	}; 
	
	public static float[] vertsBottomComplete = {
			size, 0, size,
			0, 0, size,
			0, 0, 0,
			
			size, 0, size,
			0, 0, 0,
			size, 0, 0,
	};
	
	public static float[] vertsBig = {
			// front?
			-0.505f, -0.505f, 0.505f,
			0.505f, -0.505f, 0.505f,
			0.505f, 0.505f, 0.505f,
			-0.505f, 0.505f, 0.505f,
			//right?
			0.505f, 0.505f, -0.505f,
			0.505f, 0.505f, 0.505f,
			0.505f, -0.505f, 0.505f,
			0.505f, -0.505f, -0.505f,
			// back?
			-0.505f, -0.505f, -0.505f,
			-0.505f, 0.505f, -0.505f,
			0.505f, 0.505f, -0.505f,
			0.505f, -0.505f, -0.505f,
			// left?
			-0.505f, -0.505f, -0.505f,
			-0.505f, -0.505f, 0.505f,
			-0.505f, 0.505f, 0.505f,
			-0.505f, 0.505f, -0.505f,
			// top?
			-0.505f, 0.505f, 0.505f,
			0.505f, 0.505f, 0.505f,
			0.505f, 0.505f, -0.505f,
			-0.505f, 0.505f, -0.505f,
			// bottom
			-0.505f, -0.505f, -0.505f,
			0.505f, -0.505f, -0.505f,
			0.505f, -0.505f, 0.505f,
			-0.505f, -0.505f, 0.505f,
	};
	
	public static float[] verts = {
			// front?
			-0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			//right?
			0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, -0.5f,
			// back?
			-0.5f, -0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			0.5f, 0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			// left?
			-0.5f, -0.5f, -0.5f,
			-0.5f, -0.5f, 0.5f,
			-0.5f, 0.5f, 0.5f,
			-0.5f, 0.5f, -0.5f,
			// top?
			-0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, 0.5f,
			0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, -0.5f,
			// bottom
			-0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, -0.5f,
			0.5f, -0.5f, 0.5f,
			-0.5f, -0.5f, 0.5f,
	};
	
	public static float[] uv = {
			// front
			1, 1, 
			0, 1,
			0, 0, 
			1, 0,
			// right
			0, 0,
			1, 0,
			1, 1,
			0, 1,
			// back
			0, 0,
			0, 1,
			1, 1,
			1, 0,
			// left
			1, 0,
			0, 0,
			0, 1,
			1, 1,
			// top
			1, 0, 
			0, 0,
			0, 1,
			1, 1,
			// bottom
			1, 1,
			0, 1,
			0, 0,
			1, 0,
	};
	
	/**
	 * IMPORTED FROM A MODEL I MADE IN BLENDER
	 */
	public static float[] flowerVerts = {
			1,0,1, 
			0,1,0, 
			0,0,0, 
			0,0,1, 
			1,1,0, 
			1,0,0, 
			0,0,0, 
			1,1,1, 
			1,0,1, 
			1,0,0, 
			0,1,1, 
			0,0,1, 
			1,0,1, 
			1,1,1, 
			0,1,0, 
			0,0,1, 
			0,1,1, 
			1,1,0, 
			0,0,0, 
			0,1,0, 
			1,1,1, 
			1,0,0, 
			1,1,0, 
			0,1,1, 
	};
	public static float[] flowerUVs = {
			1.0E-4f,0.9999f, 
			0.9999f,1.0E-4f, 
			0.9999f,0.9999f, 
			1.0E-4f,0.9999f, 
			0.9999f,1.0E-4f, 
			0.9999f,0.9999f, 
			1.0E-4f,0.9999f, 
			0.9999f,1.0E-4f, 
			0.9999f,0.9999f, 
			1.0E-4f,0.9999f, 
			0.9999f,1.0E-4f, 
			0.9999f,0.9999f, 
			1.0E-4f,0.9999f, 
			1.0E-4f,1.0E-4f, 
			0.9999f,1.0E-4f, 
			1.0E-4f,0.9999f, 
			1.0E-4f,1.0E-4f, 
			0.9999f,1.0E-4f, 
			1.0E-4f,0.9999f, 
			1.0E-4f,1.0E-4f, 
			0.9999f,1.0E-4f, 
			1.0E-4f,0.9999f, 
			1.0E-4f,1.0E-4f, 
			0.9999f,1.0E-4f, 
	};
	
	public static int[] indicies = {
			//7,3,2, 7,6,2
			0,  1,  2,  0,  2,  3,   //front
			4,  5,  6,  4,  6,  7,   //right
			8,  9,  10, 8,  10, 11,  //back
			12, 13, 14, 12, 14, 15,  //left
			16, 17, 18, 16, 18, 19,  //upper
			20, 21, 22, 20, 22, 23   //bottom
	}; 
	
	public static int[] indiciesFRONT = {
			//7,3,2, 7,6,2
			0,  1,  2,  0,  2,  3,   //front
	}; 
	
	public static int[] indiciesRIGHT = {
			//7,3,2, 7,6,2
			4,  5,  6,  4,  6,  7,   //right
	}; 
	public static int[] indiciesBACK = {
			//7,3,2, 7,6,2
			8,  9,  10, 8,  10, 11,  //back
	}; 
	public static int[] indiciesLEFT = {
			//7,3,2, 7,6,2
			12, 13, 14, 12, 14, 15,  //left
	}; 
	public static int[] indiciesTOP = {
			//7,3,2, 7,6,2
			16, 17, 18, 16, 18, 19,  //upper
	}; 
	public static int[] indiciesBOTTOM = {
			//7,3,2, 7,6,2
			20, 21, 22, 20, 22, 23   //bottom
	};
	
}
