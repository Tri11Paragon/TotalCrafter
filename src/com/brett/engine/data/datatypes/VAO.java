package com.brett.engine.data.datatypes;

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

	public void setVertexCount(int count) {
		this.vertexCount = count;
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
