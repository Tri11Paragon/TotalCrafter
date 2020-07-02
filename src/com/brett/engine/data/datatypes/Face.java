package com.brett.engine.data.datatypes;

/**
* @author Brett
* @date Jul. 1, 2020
*/

public class Face {
	
	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int FRONT = 4;
	public static final int BACK = 5;
	
	public float[] face;
	public float[] data;
	
	public int texture;
	public int side;
	
	public Face(float[] face, float[] data, int texture, int side) {
		this.face = face;
		this.data = data;
		this.texture = texture;
		this.side = side;
	}
	
	public boolean sameTexture(Face f) {
		return f.texture == texture;
	}
	
	public boolean equals(Face f) {
		return f.texture == texture && f.side == side;
	}
	
}
