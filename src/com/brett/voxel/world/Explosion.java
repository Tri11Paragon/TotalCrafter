package com.brett.voxel.world;

/**
*
* @author brett
* @date Mar. 8, 2020
*/

public class Explosion {
	
	private static int RE_MNT = 12;
	
	private float x,y,z,size;
	private VoxelWorld world;
	
	public Explosion(float x, float y , float z, float size, VoxelWorld world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		RE_MNT = (int) (size * 2);
		this.world = world;
	}
	
	public void explode() {
		
	}
	
}
