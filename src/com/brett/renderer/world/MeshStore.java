/** 
*	Brett Terpstra
*	Feb 13, 2020
*	
*/ 

package com.brett.renderer.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;

public class MeshStore {
	
	// used to lock the models' map
	// 0 is no one is using this
	// 1 is main thread.
	// 2 is process thread.
	public static int modelsWritten = 0;
	
	public static List<SixBoolean> booleans = new ArrayList<SixBoolean>();
	
	public static Map<SixBoolean, RawModel> models = new HashMap<SixBoolean, RawModel>();
	// i thought this was better then storing and probing each block.
	//public static Map<SixBoolean, Tuple<UnmeshedRawModel, List<Block>>> unmeshedModels = new HashMap<SixBoolean, Tuple<UnmeshedRawModel, List<Block>>>();
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
	
	public static int[] indicies = {
			//7,3,2, 7,6,2
			0,  1,  2,  0,  2,  3,   //front
			4,  5,  6,  4,  6,  7,   //right
			8,  9,  10, 8,  10, 11,  //back
			12, 13, 14, 12, 14, 15,  //left
			16, 17, 18, 16, 18, 19,  //upper
			20, 21, 22, 20, 22, 23   //bottom
	}; 
	
	public static float[] vertsFront = {
			-1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
	};
	
	public static float[] vertsBack = {
			-1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
	};
	
	public static float[] vertsRight = {
			1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f,
	};
	
	public static float[] vertsLeft = {
			-1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, -1.0f,
	};
	
	public static float[] vertsTop = {
			-1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
	}; 
	
	public static float[] vertsBottom = {
			-1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,
	};
	
	public static float[] verts = {
			// front?
			-1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
			//right?
			1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f,
			// back?
			-1.0f, -1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			// left?
			-1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, -1.0f,
			// top?
			-1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, -1.0f,
			// bottom
			-1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,
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
	
}
