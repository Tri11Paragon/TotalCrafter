package com.brett.world.collision;

import org.lwjgl.util.vector.Vector3f;

/** 
*	Brett Terpstra
*	Mar 6, 2020
*	
*/
public class BoundingBox {
	
	private float x1,y1,z1;
	private float x2,y2,z2;
	
	public BoundingBox(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public BoundingBox(Vector3f pos1, Vector3f size) {
		this.x1 = pos1.x;
		this.y1 = pos1.y;
		this.z1 = pos1.z;
		this.x2 = size.x;
		this.y2 = size.y;
		this.z2 = size.z;
	}
	
	public Vector3f distanceInside(float x, float y, float z) {
		Vector3f dist = new Vector3f(0, 0, 0);
		if (isPointColliding(x,y,z)) {
			dist.x = x - x1;
			dist.y = y - y1;
			dist.z = z - z1;
		}
		return dist;
	}
	
	public Vector3f distanceInside(Vector3f v) {
		Vector3f dist = new Vector3f(0, 0, 0);
		if (isPointColliding(v)) {
			dist.x = v.x - x1;
			dist.y = v.y - y1;
			dist.z = v.z - z1;
		}
		return dist;
	}
	
	public boolean isPointColliding(float x, float y, float z) {
		if (x >= x1 && x <= (x1+x2)) {
			if (y >= y1 && y <= (y1+y2)) {
				if(z >= z1 && z <= (z1 + z2)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isPointColliding(Vector3f v) {
		if (v.x >= x1 && v.x <= (x1+x2)) {
			if (v.y >= y1 && v.y <= (y1+y2)) {
				if(v.z >= z1 && v.z <= (z1 + z2)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
