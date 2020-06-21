package com.brett.engine.data;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class VAO {
	
	private int vaoID;
	private int vertexCount;
	
	private int[] vbos;
	
	public VAO(int vaoID, int[] vbos, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vbos = vbos;
	}
	
	public VAO(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vbos = new int[0];
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public int[] getVbos() {
		return vbos;
	}
	
}
