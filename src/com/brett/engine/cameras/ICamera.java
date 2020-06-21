package com.brett.engine.cameras;

import org.joml.Vector3f;

/**
 * @author Brett
 * @date Jun. 20, 2020
 */

public abstract class ICamera {

	protected Vector3f position = new Vector3f(0, 0, 0);
	protected float pitch;
	protected float yaw;
	protected float roll;

	public abstract void move();
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setYawPitchRoll(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
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
