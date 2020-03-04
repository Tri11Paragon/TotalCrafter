package com.brett.renderer.datatypes;

/**
*
* @author brett
* @date Mar. 3, 2020
*/

public class RawBlockModel {
	
	private byte vaoID;
	private byte vertexCount;
	
	public RawBlockModel(byte vaoID, byte vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public byte getVaoID() {
		return vaoID;
	}

	public byte getVertexCount() {
		return vertexCount;
	}
	
	public static RawBlockModel convertRawModel(RawModel m) {
		return new RawBlockModel((byte)m.getVaoID(), (byte)m.getVertexCount());
	}
	
}
