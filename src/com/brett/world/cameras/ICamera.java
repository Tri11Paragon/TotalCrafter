package com.brett.world.cameras;

import org.lwjgl.util.vector.Vector3f;

/**
*
* @author brett
* @date Mar. 30, 2020
* The base camera class
*/

public abstract class ICamera {
	
	// camera data required for the game to run
	protected Vector3f position = new Vector3f(0, 0, 0);
	protected float pitch;
	protected float yaw;
	protected float roll;
	
	public abstract void move();
	
	/**
	 * Getters and Setters below --------------------------
	 * *They explain themselves*
	 */

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
