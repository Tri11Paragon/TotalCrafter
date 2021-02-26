package com.brett.voxel.world.chunk;

/**
*
* @author brett
* @date Apr. 22, 2020
*/

public class ChunkBuilder {
	
	/**
	 * Updates the compression of the float[] verts
	 */
	public static float[] updateVertexTranslationCompress(float[] verts, int xTrans, int yTrans, int zTrans) {
		// create a new float array 1/3 in length.
		float[] newVerts = new float[verts.length/3];
		// saves 2 floats!!!! (64 bits)
		for (int i = 0; i < verts.length; i+=3) {
			// put the translation into the vertices
			int x = (int) verts[i] + xTrans;
			int y = (int) verts[i+1] + yTrans;
			int z = (int) verts[i+2] + zTrans;
			
			// since the x and z only goto 15 (0->15), they can be represented with only 4 bits
			// i use 8 bits to give some padding since I can't add them to the other float, might as well make use
			// of the full float here
			// since the y is 0->127 it can be represented in 8 bits
			// Since im pushing it over by 16 it has 16, so 8 bits of padding
			// first 8 bits is x, next 8 bits is z final 16 bits is y.
			// this compression is then decompressed on the GPU through my custom shaders.
			newVerts[i/3] = ((x & 0xFF) | ((y & 0xFF) << 16) | ((z & 0xFF) << 8));
		}
		return newVerts;
	}
	
}
