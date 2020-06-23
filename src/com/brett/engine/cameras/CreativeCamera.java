package com.brett.engine.cameras;

import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

/**
 * @author Brett
 * @date Jun. 21, 2020
 */

public class CreativeCamera extends Camera {

	public CreativeCamera(Vector3f pos) {
		this.position = pos;
	}

	// speed of char
	private float speed = 40f;
	private float turnSpeed = 5.0f;

	// vars used to determine where to move
	private double moveAtX = 0;
	private double moveAtY = 0;
	private double moveatZ = 0;

	@Override
	public void move() {
		// we don't want to move if we are not focused.
		if (DisplayManager.isMouseGrabbed) {
	
			/**
			 * These functions just assign the movement directions to their correct values
			 * based on what key is currently being pressed Im not going to write some
			 * repetitive semi sarcastic nonsense for everyone of these. I think we all have
			 * eyeballs here or at least some kind of braille screen?
			 */
			if (InputMaster.keyDown[GLFW_KEY_LEFT_CONTROL]) {
				speed = 5f;
				if (InputMaster.keyDown[GLFW_KEY_LEFT_ALT])
					speed = 1f;
			} else
				speed = 40f;
	
			if (InputMaster.keyDown[GLFW_KEY_W])
				moveAtX = -speed * DisplayManager.getFrameTimeSeconds();
	
			else if (InputMaster.keyDown[GLFW_KEY_S])
				moveAtX = speed * DisplayManager.getFrameTimeSeconds();
			else
				moveAtX = 0;
	
			if (InputMaster.keyDown[GLFW_KEY_A])
				moveatZ = speed * DisplayManager.getFrameTimeSeconds();
			else if (InputMaster.keyDown[GLFW_KEY_D])
				moveatZ = -speed * DisplayManager.getFrameTimeSeconds();
			else
				moveatZ = 0;
	
			if (InputMaster.keyDown[GLFW_KEY_SPACE])
				moveAtY = speed * DisplayManager.getFrameTimeSeconds();
			else
				moveAtY = 0;
	
			if (InputMaster.keyDown[GLFW_KEY_LEFT_SHIFT])
				moveAtY = -speed * DisplayManager.getFrameTimeSeconds();

		}

		if (DisplayManager.isMouseGrabbed) {
			pitch += -DisplayManager.getDY() * turnSpeed / 100;
			yaw += DisplayManager.getDX() * turnSpeed / 100;
		}

		// prevent the player from looping around when looking up or down
		// locks to either directly up or directly down.
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		// prevents the yaw from getting to very very large numbers.
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;

		// something I tried to prevent FPPE
		// doesn't work (duh)
		double prez = 1000000d;
		// Calculates the amount to move in the 3 axis based on the movement values
		// since this is hard to read with all the Math.round, i'd be a good idea to go
		// look at the player class
		// it has the same time but without all the useless rounds.
		float dx = (float) (Math.round((((-((moveAtX) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) + -((moveatZ) * Math.round(Math.cos(Math.toRadians(yaw)) * prez) / prez))) * prez) / prez);
		float dy = (float) ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		float dz = (float) (Math.round((((moveAtX) * Math.round(Math.cos(Math.toRadians(yaw)) * prez) / prez) + -((moveatZ) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) * prez) / prez);

		position.x += dx;

		position.y += dy;

		position.z += dz;
	}

}
