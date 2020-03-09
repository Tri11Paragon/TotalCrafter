package com.brett.voxel.world;

/**
*
* @author brett
* @date Mar. 8, 2020
*/

public class Explosion {
	
	private float x,y,z,size;
	private VoxelWorld world;
	
	public Explosion(float x, float y , float z, float size, VoxelWorld world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.world = world;
	}
	
	public void explode() {
		
	}
	
}
