package com.brett.renderer.datatypes;

/**
*
* @author brett
* @date Mar. 3, 2020
*/

public class RawBlockModel extends RawModel {
	
	private int[] vbos;
	
	public RawBlockModel(int vaoID, int[] vbos, int vertexCount) {
		super(vaoID, vertexCount);
		this.vbos = vbos;
	}

	public int[] getVbos() {
		return vbos;
	}
}
