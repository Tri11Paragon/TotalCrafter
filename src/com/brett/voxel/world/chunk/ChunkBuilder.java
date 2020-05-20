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
	
	/**
	 * Creates Z value errors and I don't know why. It is unused but is a neat idea
	 * for reducing 2 floats it doesn't really show on the profiler.
	 */
	public static float[] updateVertexTranslationCompress(float[] verts, int xTrans, int yTrans, int zTrans) {
		float[] newVerts = new float[verts.length/3];
		for (int i = 0; i < verts.length; i+=3) {
			int x = (int) verts[i] + xTrans;
			int y = (int) verts[i+1] + yTrans;
			int z = (int) verts[i+2] + zTrans;
			
			newVerts[i/3] = ((x & 0xFF) | ((y & 0xFF) << 16) | ((z & 0xFF) << 8));
		}
		return newVerts;
	}
	
}
