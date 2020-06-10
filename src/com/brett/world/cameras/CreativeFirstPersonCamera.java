package com.brett.world.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
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

public class CreativeFirstPersonCamera extends Camera {
	
	private VoxelWorld world;
	
	public CreativeFirstPersonCamera(Vector3f pos) {
		this.position = pos;
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
		if (!Mouse.isGrabbed())
			return;
		
		/**
		 * These functions just assign the movement directions to their correct values
		 * based on what key is currently being pressed
		 * Im not going to write some repetitive semi sarcastic nonsense for everyone of
		 * these. I think we all have eyeballs here or at least some kind of braille screen?
		 */
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			speed = 5f;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				speed=1f;
		} else
			speed = 40f;

		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = -speed * DisplayManager.getFrameTimeSeconds();
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = speed * DisplayManager.getFrameTimeSeconds();
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveatZ = speed * DisplayManager.getFrameTimeSeconds();
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveatZ = -speed * DisplayManager.getFrameTimeSeconds();
		else 
			moveatZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			moveAtY = speed * DisplayManager.getFrameTimeSeconds();
		else
			moveAtY = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			moveAtY = -speed * DisplayManager.getFrameTimeSeconds();
		
		float speed = 30f;
		
		if (Mouse.isGrabbed()) {
			pitch += -Mouse.getDY() * turnSpeed/100;
			yaw += Mouse.getDX() * turnSpeed/100;
		}
		
		/**
		 * This allows you to turn without having a mouse
		 */
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			yaw += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			yaw += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			pitch += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
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
			if (world.chunk.getBlock(position.x + (wx), position.y, position.z) == 0) {
				// if allowed to move then update the amount we can move
				xb = wx;
			} else // if we can't move any more then no need to loop with higher values
				break;
		}
		// same thing as ^ but in the y direction
		for (int i = 0; i < RECUR_AMT; i++) {
			wy += yStep;
			if (world.chunk.getBlock(position.x, position.y + (wy), position.z) == 0) {
				yb = wy;
			} else
				break;
		}
		// same thinf as ^^ but in the z direction
		for (int i = 0; i < RECUR_AMT; i++) {
			wz += zStep;
			if (world.chunk.getBlock(position.x, position.y, position.z + (wz)) == 0) {
				zb = wz;
			} else 
				break;
		}
		
		// apply the translations after one final check
		if (world.chunk.getBlock(position.x + (xb), position.y, position.z) == 0)
			position.x += xb;
		
		if (world.chunk.getBlock(position.x, position.y + (yb), position.z) == 0)
			position.y += yb;
		
		if (world.chunk.getBlock(position.x , position.y, position.z + (zb)) == 0)
			position.z += zb;
	}
	
}
