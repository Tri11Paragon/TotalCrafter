package com.brett.world.mesh;

/**
* @author Brett
* @date Jun. 21, 2020
*/

public class MeshStore {
	
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
	
	/**
	 * updates the specified float array with the values specified.
	 * light being split 4 bits for sun level and 4 bits for block light
	 * layer being the later index. (you get 22 bits for this)
	 */
	public static float[] updateCompression(float[] fa, byte light, int layer) {
		/*
		 * im not sure of a simple way of explaing this stuff other
		 * then moving numbers into regions that are going to be zeros as the number sizes
		 * are explicitly defined.
		 * 
		 * I hope you understand this stuff. I don't think its that hard
		 * but I've been working with it for months.
		 */
		float[] tr = new float[fa.length];
		for (int i = 0; i < fa.length; i++) {
			int fai = (int)fa[i];
			// move the layer over by 10 bits so that its xxxx xxxx xxxx xxxx 0000 0000 00
			int la = layer << 10;
			int lla = light << 2;
			// or them in so that way no values are changed.
			// since we moved them over into 0s we are not replacing any information.
			fai |= la;
			fai |= lla;
			tr[i] = fai;
		}
		return tr;
	}
	
	/*
	 * Below are self describing float[] defining verts and uvs.
	 */
	
	private static float size = 1.0f;
	
	public static float[] vertsFrontComplete = {
			0, 0, size,
			size, 0, size,
			0, size, size,
			
			0, size, size,
			size, 0, size,
			size, size, size,
	};
	
	public static float[] createFrontComplete(float z, float miny, float maxy, float minx, float maxx) {
		return new float[] {
				minx, miny, z,
				maxx, miny, z,
				minx, maxy, z,
				
				minx, maxy, z,
				maxx, miny, z,
				maxx, maxy, z,
		};
	}
	
	public static float[] vertsBackComplete = {
			0, 0, 0,
			0, size, 0,
			size, size, 0,
			
			size, size, 0,
			size, 0, 0,
			0, 0, 0,
	};
	
	public static float[] createBackComplete (float z, float miny, float maxy, float minx, float maxx) {
		return new float[] {
				minx, miny, z,
				minx, maxy, z,
				maxx, maxy, z,
				
				maxx, maxy, z,
				maxx, miny, z,
				minx, miny, z,
		};
	}
	
	public static float[] vertsRightComplete = {
			size, 0, 0,
			size, size, 0,
			size, 0, size,
			
			size, 0, size,
			size, size, 0,
			size, size, size,
	};
	
	public static float[] createRightComplete(float x, float miny, float maxy, float minz, float maxz) {
		return new float[] {
				x, miny, minz,
				x, maxy, minz,
				x, miny, maxz,
				
				x, miny, maxz,
				x, maxy, minz,
				x, maxy, maxz,	
		};
	}
	
	public static float[] vertsLeftComplete = {
			0, 0, 0,
			0, 0, size,
			0, size, size,
			
			0, size, size,
			0, size, 0,
			0, 0, 0,
	};
	
	public static float[] createLeftComplete(float x, float miny, float maxy, float minz, float maxz) {
		return new float[] {
				x, miny, minz,
				x, miny, maxz,
				x, maxy, maxz,
				
				x, maxy, maxz,
				x, maxy, minz,
				x, miny, minz,
		};
	}
	
	public static float[] vertsTopComplete = {
			size, size, size,
			0, size, 0,
			0, size, size,
			
			size, size, size,
			size, size, 0,
			0, size, 0,
	}; 
	
	public static float[] createTopComplete(float y, float minx, float maxx, float minz, float maxz) {
		return new float[] {
				maxx, y, maxz,
				minx, y, minz,
				minx, y, maxz,
				
				maxx, y, maxz,
				maxx, y, minz,
				minx, y, minz,
		};
	}
	
	public static float[] vertsBottomComplete = {
			size, 0, size,
			0, 0, size,
			0, 0, 0,
			
			size, 0, size,
			0, 0, 0,
			size, 0, 0,
	};
	
	public static float[] createBottomComplete(float y, float minx, float maxx, float minz, float maxz) {
		return new float[] {
				maxx, y, maxz,
				minx, y, maxz,
				minx, y, minz,
				
				maxx, y, maxz,
				minx, y, minz,
				maxx, y, minz,
		};
	}
	
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
	
	static {
		// a better example is below.
		// compress all the UVs from 2 floats into 2 bits.
		// thats like 62 bits of data saving!!?!?!
		// do it for each side.
		for (int i = 0; i < uvFrontComplete.length; i+= 2) {
			// this puts it into 2 bits.
			// via bit shifting and or operators.
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
		// compress the flower but like due to the way it was defined
		// we need to make sure we got the right value
		// (some are defined like: "1.0E-4f,0.9999f")
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
			// this is eaiser to see then ^
			// so its defined as the x being x0
			// and the y being 0y
			// or them together and you get xy
			// since the UVs are always going to be either 1 or 0.
			// we can get away with this level of amazing data compression
			// and resource saving.
			float fr = (amt1 << 1) | amt2;
			uvFlowerCompleteCompress[i/2] = fr;
		}
	}
	
}
