package com.brett.voxel.world.chunk;

/**
*
* @author brett
* @date Apr. 22, 2020
*/

public class ChunkBuilder {
	
	public static float[] updateVertexTranslation(float[] verts, int xTrans, int yTrans, int zTrans) {
		float[] newVerts = new float[verts.length];
		for (int i = 0; i < verts.length; i+=3) {
			newVerts[i] = verts[i] + xTrans;
			newVerts[i+1] = verts[i+1] + yTrans;
			newVerts[i+2] = verts[i+2] + zTrans;
		}
		return newVerts;
	}
	
}
