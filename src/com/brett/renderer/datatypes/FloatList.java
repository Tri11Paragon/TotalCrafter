package com.brett.renderer.datatypes;

/**
*
* @author brett
* @date May 19, 2020
* UNUSED AND USELESS PLEASE IGNORE
*/

public class FloatList  {
	
	private float[] floats;
	private float[][] unfloated;
	private int max = 0;
	private int index = 0;
	
	public FloatList(int max) {
		floats = new float[0];
		unfloated = new float[max][0];
		this.max = max;
	}
	
	public void add(float[] fr) {
		unfloated[index] = fr;
		index++;
	}
	
	public void commit() {
		int expand = 0;
		expand = unfloated.length * unfloated[0].length;
		float[] ftr = new float[floats.length + expand];
		if (ftr.length == 0 || unfloated[0].length == 0)
			return;
		for (int i = 0; i < floats.length; i++)
			ftr[i] = floats[i];
		for (int i = 0; i < unfloated.length; i++) {
			for (int j = 0; j < unfloated[i].length; j++) {
				ftr[floats.length+(i*unfloated[0].length) + j]
						= unfloated[i][j];
			}
		}
		floats = ftr;
		unfloated = new float[max][0];
		index = 0;
		return;
	}
	
	public float[] toFloatArray() {
		return floats;
	}
	
}
