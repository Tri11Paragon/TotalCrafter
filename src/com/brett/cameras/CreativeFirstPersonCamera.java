package com.brett.cameras;

import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

import com.brett.DisplayManager;
import com.brett.tools.IKeyState;
import com.brett.tools.InputMaster;
import com.brett.voxel.world.VoxelWorld;


/**
 * @author brett
 *	
 * This class was the first Camera. Before ICamera and Camera.
 * Its not used anymore as we have the player class
 * Im not going to remove it though.
 * 
 * Yes the movement code is pretty much the same as the player class.
 *
 */

public class CreativeFirstPersonCamera extends Camera implements IKeyState {
	
	private VoxelWorld world;
	
	public CreativeFirstPersonCamera(Vector3d pos) {
		this.position = pos;
		InputMaster.keyboard.add(this);
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	// speed of char
	private float speed = 40f;
	private float turnSpeed = 5.0f;
	
	
	// vars used to determine where to move
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	private static int RECUR_AMT = 100;
	
	@Override
	public void move() {
		// we don't want to move if we are not focused.
		if (!DisplayManager.isMouseGrabbed)
			return;
		
		/**
		 * These functions just assign the movement directions to their correct values
		 * based on what key is currently being pressed
		 * Im not going to write some repetitive semi sarcastic nonsense for everyone of
		 * these. I think we all have eyeballs here or at least some kind of braille screen?
		 */
		if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_ALT]) {
			speed = 5f;
			if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_CONTROL])
				speed=1f;
		} else
			speed = 40f;

		if (InputMaster.keyDown[GLFW.GLFW_KEY_W])
			moveAtX = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		
		else if (InputMaster.keyDown[GLFW.GLFW_KEY_S])
			moveAtX = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
			moveAtX = 0;
			
		if (InputMaster.keyDown[GLFW.GLFW_KEY_A])
			moveatZ = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
		if (InputMaster.keyDown[GLFW.GLFW_KEY_D])
			moveatZ = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		else 
			moveatZ = 0;

		if (InputMaster.keyDown[GLFW.GLFW_KEY_SPACE])
			moveAtY = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
			moveAtY = 0;
			
		if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT])
			moveAtY = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		
		float speed = 30f;
		
		if (DisplayManager.isMouseGrabbed) {
			pitch += -DisplayManager.getDX() * turnSpeed/100;
			yaw += DisplayManager.getDX() * turnSpeed/100;
		}
		
		/**
		 * This allows you to turn without having a mouse
		 */
		if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT])
			yaw += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT])
			yaw += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (InputMaster.keyDown[GLFW.GLFW_KEY_UP])
			pitch += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (InputMaster.keyDown[GLFW.GLFW_KEY_DOWN])
			pitch += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		
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
		// since this is hard to read with all the Math.round, i'd be a good idea to go look at the player class
		// it has the same time but without all the useless rounds.
		float dx = (float) (Math.round((((-((moveAtX) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) + -((moveatZ) * Math.round(Math.cos(Math.toRadians(yaw))*prez)/prez)))*prez)/prez);
		float dy = (float) ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		float dz = (float) (Math.round((((moveAtX) * Math.round(Math.cos(Math.toRadians(yaw)) * prez)/prez) + -((moveatZ) * Math.round(Math.sin(Math.toRadians(yaw))*prez)/prez))*prez)/prez);
		
		// calulcates how much we can step per loop of recursive collision.
		float xStep = (dx)/RECUR_AMT;
		float yStep = (dy)/RECUR_AMT;
		float zStep = (dz)/RECUR_AMT;
		
		// amount that we have stepped in each dir
		float wx = 0, wy = 0, wz = 0;
		// these are set to the max step we can move without colliding.
		float xb = 0, yb = 0, zb = 0;

		// these guys just check to make sure that even if we are moving at multiple blocks per
		// second that the player is unable to move though blocks
		for (int i = 0; i < RECUR_AMT; i++) {
			// add to the stepped amount
			wx += xStep;
			// check to make sure that this new step position has an open air block
			if (world.chunk.getBlock((float)(position.x + (wx)), (float)position.y, (float)position.z) == 0) {
				// if allowed to move then update the amount we can move
				xb = wx;
			} else // if we can't move any more then no need to loop with higher values
				break;
		}
		// same thing as ^ but in the y direction
		for (int i = 0; i < RECUR_AMT; i++) {
			wy += yStep;
			if (world.chunk.getBlock((float)position.x, (float)(position.y + (wy)), (float)position.z) == 0) {
				yb = wy;
			} else
				break;
		}
		// same thinf as ^^ but in the z direction
		for (int i = 0; i < RECUR_AMT; i++) {
			wz += zStep;
			if (world.chunk.getBlock((float)position.x, (float)position.y, (float)(position.z + (wz))) == 0) {
				zb = wz;
			} else 
				break;
		}
		
		// apply the translations after one final check
		if (world.chunk.getBlock((float)(position.x + (xb)), (float)position.y, (float)position.z) == 0)
			position.x += xb;
		
		if (world.chunk.getBlock((float)position.x, (float)(position.y + (yb)), (float)position.z) == 0)
			position.y += yb;
		
		if (world.chunk.getBlock((float)position.x , (float)position.y, (float)(position.z + (zb))) == 0)
			position.z += zb;
	}

	@Override
	public void onKeyPressed(int keys) {
	}

	@Override
	public void onKeyReleased(int keys) {
	}
	
}
