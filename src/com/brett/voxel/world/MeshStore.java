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

import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.renderer.datatypes.SixBoolean;

public class MeshStore {
	
	// used to lock the models' map
	// 0 is no one is using this
	// 1 is main thread.
	// 2 is process thread.
	public static int modelsWritten = 0;
	
	public static List<SixBoolean> booleans = new ArrayList<SixBoolean>();
	
	public static Map<SixBoolean, RawBlockModel> models = new HashMap<SixBoolean, RawBlockModel>();
	public static int boolEmpty = 0;
	// make this better
	
	public static float[] vertsNONE = {
			// front?
	};
	
	public static float[] uvNONE = {
			// front
	};
	
	public static int[] indiciesNONE = {
			//7,3,2, 7,6,2
	}; 
	
	//not needed as we are currently calculating normals inside the shaders
	public static float[] normalsNONE = {};
	
	public static float[] normals = {
			
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
	
	public static float[] uvFrontComplete = {
			0, 1, 
			1, 1,
			0, 0, 
			
			0, 0,
			1, 1,
			1, 0
	};
	
	public static float[] uvBackComplete = {
			1, 1, 
			1, 0,
			0, 0, 
		
			0, 0,
			0, 1,
			1, 1,
	};

	public static float[] uvRightComplete = {
			1, 1,
			1, 0, 
			0, 1,
			
			0, 1,
			1, 0,
			0, 0,
	};
	
	public static float[] uvLeftComplete = {
			0, 1, 
			1, 1,
			1, 0, 
			
			1, 0,
			0, 0,
			0, 1,
	};
	
	public static float[] uvTopComplete = {
			1, 0,
			0, 1, 
			0, 0,
			
			1, 0,
			1, 1,
			0, 1,
	};
	
	public static float[] uvBottomComplete = {
			1, 0, 
			0, 0,
			0, 1, 
			
			1, 0,
			0, 1,
			1, 1
	};
	
	private static float size = 0.5f;
	
	public static float[] vertsFrontComplete = {
			-size, -size, size,
			size, -size, size,
			-size, size, size,
			
			-size, size, size,
			size, -size, size,
			size, size, size,
	};
	
	public static float[] vertsBackComplete = {
			-size, -size, -size,
			-size, size, -size,
			size, size, -size,
			
			size, size, -size,
			size, -size, -size,
			-size, -size, -size,
	};
	
	public static float[] vertsRightComplete = {
			size, -size, -size,
			size, size, -size,
			size, -size, size,
			
			size, -size, size,
			size, size, -size,
			size, size, size,
	};
	
	public static float[] vertsLeftComplete = {
			-size, -size, -size,
			-size, -size, size,
			-size, size, size,
			
			-size, size, size,
			-size, size, -size,
			-size, -size, -size,
	};
	
	public static float[] vertsTopComplete = {
			size, size, size,
			-size, size, -size,
			-size, size, size,
			
			size, size, size,
			size, size, -size,
			-size, size, -size,
	}; 
	
	public static float[] vertsBottomComplete = {
			size, -size, size,
			-size, -size, size,
			-size, -size, -size,
			
			size, -size, size,
			-size, -size, -size,
			size, -size, -size,
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
	
	public static float[] flowerVerts = {
			0.494975f,-0.5f,0.494975f, 
			-0.494975f,0.5f,-0.494975f, 
			-0.494975f,-0.5f,-0.494975f, 
			-0.494975f,-0.5f,0.494975f, 
			0.494975f,0.5f,-0.494975f, 
			0.494975f,-0.5f,-0.494975f, 
			-0.494975f,-0.5f,-0.494975f, 
			0.494975f,0.5f,0.494975f, 
			0.494975f,-0.5f,0.494975f, 
			0.494975f,-0.5f,-0.494975f, 
			-0.494975f,0.5f,0.494975f, 
			-0.494975f,-0.5f,0.494975f, 
			0.494975f,-0.5f,0.494975f, 
			0.494975f,0.5f,0.494975f, 
			-0.494975f,0.5f,-0.494975f, 
			-0.494975f,-0.5f,0.494975f, 
			-0.494975f,0.5f,0.494975f, 
			0.494975f,0.5f,-0.494975f, 
			-0.494975f,-0.5f,-0.494975f, 
			-0.494975f,0.5f,-0.494975f, 
			0.494975f,0.5f,0.494975f, 
			0.494975f,-0.5f,-0.494975f, 
			0.494975f,0.5f,-0.494975f, 
			-0.494975f,0.5f,0.494975f, 
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
	
}
