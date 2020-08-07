package com.brett.engine.data.datatypes;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class SmallModelData {
	
	private float[] verts;
	private float[] uvs;
	
	public SmallModelData(float[] verts, float[] uvs) {
		this.verts = verts;
		this.uvs = uvs;
	}

	public float[] getVerts() {
		return verts;
	}

	public void setVerts(float[] verts) {
		this.verts = verts;
	}

	public float[] getUvs() {
		return uvs;
	}

	public void setUvs(float[] uvs) {
		this.uvs = uvs;
	}
	
}
