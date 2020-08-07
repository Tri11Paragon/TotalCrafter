package com.brett.engine.data.datatypes;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ModelData {
	
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private Material material;

	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	
}
