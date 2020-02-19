package com.brett.world.cameras;

import org.lwjgl.util.vector.Vector3f;

/**
*
* @author brett
*
*/

public class FirstPersonCamera extends Camera {
	
	public void changePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public void setPosition(Vector3f v) {
		this.position = v;
	}
	
	public void setPosition(Vector3f v, Vector3f offset) {
		Vector3f.add(v, offset, this.position);
	}
	
	public void changeView(float dyaw, float dpitch) {
		this.yaw += dyaw;
		this.pitch += dpitch;
	}
	
}
