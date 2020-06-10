package com.brett.renderer.datatypes;

public class ModelVAO {
	
	private int vaoID;
	private int vertexCount;
	
	public ModelVAO(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
}
