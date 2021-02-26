package com.brett.datatypes;

/**
*
* @author brett
* @date Mar. 3, 2020
* Just a ModelVAO that stores the VBOs in an int[]
*/

public class BlockModelVAO extends ModelVAO {
	
	private int[] vbos;
	
	public BlockModelVAO(int vaoID, int[] vbos, int vertexCount) {
		super(vaoID, vertexCount);
		this.vbos = vbos;
	}

	public int[] getVbos() {
		return vbos;
	}
}
