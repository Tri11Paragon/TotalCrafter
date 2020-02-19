package com.brett.world.terrain;

/**
*
* @author brett
*
*/

public abstract class HeightData {

	protected int size;
	
	public abstract float getHeight(int x, int z);
	
	public int getSize() {
		return size;
	}
	
}
