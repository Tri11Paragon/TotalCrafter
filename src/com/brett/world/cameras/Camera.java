package com.brett.world.cameras;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	protected Vector3f position = new Vector3f(0, 0, 0);
	protected float pitch;
	protected float yaw;
	protected float roll;
	
	public Camera() {
		
	}
	
	public void move() {
		
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	public void invertPitch() {
		pitch = -pitch;
	}
	
}
