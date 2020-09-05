package com.brett.engine.cameras;

import com.brett.engine.data.collision.AxisAlignedBB;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;
import com.brett.networking.ServerConnection;
import com.brett.world.World;

import static org.lwjgl.glfw.GLFW.*;

import java.util.List;

import org.joml.Vector3d;

/**
 * @author Brett
 * @date Jun. 21, 2020
 */

public class CreativeCamera extends Camera {

	public World world;
	public AxisAlignedBB cbb = new AxisAlignedBB(-0.25,-0.75,-0.25, 0.25,0.25,0.25);
	public ServerConnection sc;
	
	public CreativeCamera(Vector3d pos, World world) {
		this.position = pos;
		this.world = world;
	}
	
	public CreativeCamera(Vector3d pos, World world, ServerConnection sc) {
		this.position = pos;
		this.world = world;
		this.sc = sc;
	}

	// speed of char
	private float speed = 40f;
	private float turnSpeed = 3.0f;

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
			pitch += DisplayManager.getDY() * turnSpeed / 100;
			yaw += DisplayManager.getDX() * turnSpeed / 100;
		}

		// prevent the player from looping around when looking up or down
		// locks to either directly up or directly down.
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		// prevents the yaw from getting to very very large numbers.
		if (this.yaw < 0)
			this.yaw = 360;
		if (this.yaw > 360)
			this.yaw = 0;

		// Calculates the amount to move in the 3 axis based on the movement values
		// since this is hard to read with all the Math.round, i'd be a good idea to go
		// look at the player class
		// it has the same time but without all the useless rounds.
		double dx = ((((-((moveAtX) * Math.sin(Math.toRadians(yaw)))) + -((moveatZ) * Math.cos(Math.toRadians(yaw))))));
		double dy = ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		double dz = ((((moveAtX) * Math.cos(Math.toRadians(yaw))) + -((moveatZ) * Math.sin(Math.toRadians(yaw)))));
		
		int intpx = (int) position.x;
		int intpy = (int) position.y;
		int intpz = (int) position.z;
		
		if (dx != 0 || dy != 0 || dz != 0) {
			List<AxisAlignedBB> bbs = world.getBoundsInRange(intpx-2, intpy-2, intpz-2, intpx+2, intpy+2, intpz+2);
			
			if (dx != 0) {
				AxisAlignedBB ctb = cbb.translate(position.x + dx, position.y, position.z);
				boolean broken = false;
				for (int i = 0; i < bbs.size(); i++) {
					if (bbs.get(i) == null)
						continue;
					
					if (ctb.intersectsWith(bbs.get(i))) {
						broken = true;
						break;
					}
					
				}
				if (!broken) {
					position.x += dx;
					if (sc != null)
						sc.sendPlayerSync(position.x, position.y, position.z);
				}
			}
			
			if (dy != 0) {
				AxisAlignedBB ctb = cbb.translate(position.x, position.y + dy, position.z);
				boolean broken = false;
				for (int i = 0; i < bbs.size(); i++) {
					if (bbs.get(i) == null)
						continue;
					
					if (ctb.intersectsWith(bbs.get(i))) {
						broken = true;
						break;
					}
					
				}
				if (!broken) {
					position.y += dy;
					if (sc != null)
						sc.sendPlayerSync(position.x, position.y, position.z);
				}
			}
			
			if (dz != 0) {
				AxisAlignedBB ctb = cbb.translate(position.x, position.y, position.z+dz);
				boolean broken = false;
				for (int i = 0; i < bbs.size(); i++) {
					if (bbs.get(i) == null)
						continue;
					
					if (ctb.intersectsWith(bbs.get(i))) {
						broken = true;
						break;
					}
					
				}
				if (!broken) {
					position.z += dz;
					if (sc != null)
						sc.sendPlayerSync(position.x, position.y, position.z);
				}
			}
		}
	}

}
