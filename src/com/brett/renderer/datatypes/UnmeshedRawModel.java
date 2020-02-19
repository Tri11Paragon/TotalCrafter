package com.brett.renderer.datatypes;

/**
*
* @author brett
*
*/

public class UnmeshedRawModel {
	
	private float[] verts;
	private float[] uvs;
	private int[] indicies;
	
	public UnmeshedRawModel(float[] verts, float[] uvs, int[] indicies) {
		this.verts = verts;
		this.uvs = uvs;
		this.indicies = indicies;
	}

	public float[] getVerts() {
		return verts;
	}

	public float[] getUvs() {
		return uvs;
	}

	public int[] getIndicies() {
		return indicies;
	}
	
}
