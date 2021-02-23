package com.brett.voxel.world.player;

import org.joml.Vector3f;

import com.brett.DisplayManager;
import com.brett.cameras.Camera;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.UIMaster;
import com.brett.sound.AudioController;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date May 21, 2020
*/

public class Player extends Camera {
	
	private static final int RECUR_AMT = 1;
	private static final float LOWESTPOINT = -1.75f;
	private static final float LOWESTPOINTCROUCH = -0.75f;
	public static boolean flight = false;
	public static boolean collision = true;
	
	private double speed = 5;
	public static double normalSpeed = 6;
	private double turnSpeed = 10.0;
	private double moveAtX = 0;
	private double moveAtY = 0;
	private double moveatZ = 0;
	private double targetY = 0;
	private boolean onGround = false;
	public volatile boolean crouching = false;
	
	private PlayerInventory pi;
	private VoxelWorld world;
	private Loader loader;
	private UIMaster ui;
	
	private final float size = 0.25f;
	private final Vector3f[] cordsStand = {
									  new Vector3f(-size, LOWESTPOINT, -size), new Vector3f(size, LOWESTPOINT, size), 
									  new Vector3f(size, LOWESTPOINT, -size), new Vector3f(-size, LOWESTPOINT, size),
			
									  new Vector3f(-size, LOWESTPOINT/2, -size), new Vector3f(size, LOWESTPOINT/2, size), 
									  new Vector3f(size, LOWESTPOINT/2, -size), new Vector3f(-size, LOWESTPOINT/2, size),
			
									  new Vector3f(-size, 0.10f, -size), new Vector3f(size, 0.10f, size), 
									  new Vector3f(size, 0.10f, -size), new Vector3f(-size, 0.10f, size)};
	
	private final Vector3f[] cordsCrouch = {
									  new Vector3f(-size, LOWESTPOINTCROUCH, -size), new Vector3f(size, LOWESTPOINTCROUCH, size), 
									  new Vector3f(size, LOWESTPOINTCROUCH, -size), new Vector3f(-size, LOWESTPOINTCROUCH, size),
			
									  new Vector3f(-size, LOWESTPOINTCROUCH/2, -size), new Vector3f(size, LOWESTPOINTCROUCH/2, size), 
									  new Vector3f(size, LOWESTPOINTCROUCH/2, -size), new Vector3f(-size, LOWESTPOINTCROUCH/2, size),
			
									  new Vector3f(-size, 0f, -size), new Vector3f(size, 0f, size), 
									  new Vector3f(size, 0f, -size), new Vector3f(-size, 0f, size)};
	
	private Vector3f[] cords = cordsStand;
	
	
	public Player(Loader loader, UIMaster ui) {
		this.loader = loader;
		this.ui = ui;
		this.position = new Vector3f(0, 120, 0);
	}
	
	public void init() {
		pi = new PlayerInventory(ui);
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	private long last = 0;
	@Override
	public void move() {
		// don't move if the mouse isn't grabbed.
		if (Mouse.isGrabbed()) {
			// set the crouching when pressing control
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				// start crouching if we are not crouching before
				if (!crouching) {
					crouching = true;
					targetY = 0;
					updateCrouching();
				}
			} else {
				if (crouching) {
					int passed = 0;
					// check to make sure we can get up
					for (int i = 0; i < cords.length; i++) {
						if (world.chunk.getBlock(this.position.x + cords[i].x, this.position.y + 1.0f, this.position.z + cords[i].z) == 0)
							passed++;
					}
					// only un crouch if we can
					if (passed == cords.length) {
						this.targetY = 1.0f;
						crouching = false;
						updateCrouching();
					}
				}
			}
			
			// slow down if pressing shift
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speed = normalSpeed/2;
			} else
				speed = normalSpeed;
			
			// can't move if we are getting up
			if (targetY > 0)
				speed = 0;
			
			// change where we are moving based on what keys are pressed.
			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				moveAtX = -speed * DisplayManager.getFrameTimeSeconds();
			else if (Keyboard.isKeyDown(Keyboard.KEY_S))
				moveAtX = speed * DisplayManager.getFrameTimeSeconds();
			else {
				moveAtX = 0;
			}
			
			// change the move ats based on what keys are pressed
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				moveatZ = speed * DisplayManager.getFrameTimeSeconds();
			else
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				moveatZ = -speed * DisplayManager.getFrameTimeSeconds();
			else 
				moveatZ = 0;
	
			if (!flight) {
				// allow for jumping if we are not flying
				if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround) {
					moveAtY += 0.5f/5;
					onGround = false;
				}
			}
		
			// change where you are looking based on how much the mouse has moved.
			pitch += -Mouse.getDY() * (turnSpeed*SettingsLoader.SENSITIVITY)/100;
			yaw += Mouse.getDX() * (turnSpeed*SettingsLoader.SENSITIVITY)/100;
			
			// turn speed
			float speed = 30;
			
			// allows for keypad control of the where you are looking
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
				yaw += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
				yaw += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
			if (Keyboard.isKeyDown(Keyboard.KEY_UP))
				pitch += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
				pitch += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		} else {
			moveAtX = 0;
			moveatZ = 0;
		}
		
		if (flight) {
			// enables the ability to do creative fly (no gravity)
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				moveAtY = speed * DisplayManager.getFrameTimeSeconds();
			else
				moveAtY = 0;
				
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				moveAtY = -speed * DisplayManager.getFrameTimeSeconds();
		} else {
			// apply gravity.
			moveAtY -= (VoxelWorld.GRAVITY/24) * DisplayManager.getFrameTimeSeconds();
			
			// make sure we can't do to much gravity.
			if (moveAtY < -VoxelWorld.GRAVITY * 3)
				moveAtY = -VoxelWorld.GRAVITY * 3;
		}
		
		// increase our position to the target.
		if (targetY > 0) {
			double amount = (VoxelWorld.GRAVITY) * DisplayManager.getFrameTimeSeconds();
			this.position.y += amount;
			this.targetY -= amount;
		}
		
		// make sure we can't go more then completely vertical or down
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;
		
		// calculate the changes in x,y,z
		// i drew a picture explaining the math behind this
		// but i can't import images here
		// its pretty neat when you think about it
		// but this doesn't really work at large distances.
		double dx = (-((moveAtX) * Math.sin(Math.toRadians(yaw))) + -((moveatZ) * Math.cos(Math.toRadians(yaw))) );
		double dy = (moveAtY);
		double dz = ((((moveAtX) * Math.cos(Math.toRadians(yaw))) + -((moveatZ) * Math.sin(Math.toRadians(yaw)))));
		
		// calculate the step per for
		double xStep = (dx)/RECUR_AMT;
		double yStep = (dy)/RECUR_AMT;
		double zStep = (dz)/RECUR_AMT;
		
		double wx = 0, wy = 0, wz = 0;
		double xb = 0, yb = 0, zb = 0;
		
		if (collision) {
			// each of these check each direction to make sure that can move
			x1: for (int d = 0; d < RECUR_AMT; d++) {
				// add to the step amount
				wx += xStep;
				// check it first
				if (world.chunk.getBlockCollision(position.x + ((float)wx), position.y, position.z) != COLLISIONTYPE.SOLID) {
					int amt = 0;
					// check all position in our local position array
					for (int i = 0; i < cords.length; i++) {
						if (world.chunk.getBlockCollision(position.x + cords[i].x + ((float)wx), position.y + cords[i].y, position.z + cords[i].z) != COLLISIONTYPE.SOLID)
							amt++;
						else // no neep to loop more if any failed.
							break x1;
					}
					// if all of them passed then apply the translation
					if (amt == cords.length)
						xb = wx;
				} else
					break;
			}
			
			// same as ^ but for x
			z1: for (int d = 0; d < RECUR_AMT; d++) {
				wz += zStep;
				if (world.chunk.getBlockCollision(position.x, position.y, position.z + ((float)wz)) != COLLISIONTYPE.SOLID) {
					int amt = 0;
					for (int i = 0; i < cords.length; i++) {
						if (world.chunk.getBlockCollision(position.x + cords[i].x, position.y + cords[i].y, position.z + cords[i].z + ((float)wz)) != COLLISIONTYPE.SOLID)
							amt++;
						else
							break z1;
					}
					if (amt == cords.length)
						zb = wz;
				} else 
					break;
			}
			
			// same as ^ but for y
			y1: for (int d = 0; d < RECUR_AMT; d++) {
				wy += yStep;
				if (world.chunk.getBlockCollision(position.x, position.y + ((float)wy), position.z) != COLLISIONTYPE.SOLID) {
					int amty = 0;
					for (int i = 0; i < cords.length; i++) {
						if (world.chunk.getBlockCollision((position.x + cords[i].x),(position.y + cords[i].y + ((float)wy)),(position.z + cords[i].z)) != COLLISIONTYPE.SOLID)
							amty++;
						else
							break y1;
					}
					if (amty == cords.length)
						yb = wy;
				} else
					break;
			}
			if (yb == 0) {
				onGround = true;
				moveAtY = 0;
			}
			
			
			//if (world.chunk.getBlock(position.x + ((float)xb), position.y, position.z) == 0)
				position.x += xb;
			
			//if (world.chunk.getBlock(position.x, position.y + ((float)yb), position.z) == 0)
				position.y += yb;
			
			//if (world.chunk.getBlock(position.x , position.y, position.z + ((float)zb)) == 0)
				position.z += zb;
		} else {
			position.x += dx;
			position.y += dy;
			position.z += dz;
		}

		/**
		 * Sends the server an update of the position of this player
		 * if they have changed positions and if it has been greater then 50 milliseconds
		 * since last update.
		 */
		if (VoxelWorld.isRemote) {
			long current = System.currentTimeMillis();
			// send position update to the server if its been more then 50 ms since last while moving or 100 ms and not moving
			if (dx > 0 || dy > 0 || dz > 0 || current - last > 100) {
				if (current - last > 50) {
					VoxelWorld.localClient.updatePosition(this);
					last = current;
				}
			}
		}
		
		// update the listener position
		AudioController.setListenerPosition(this.position, MouseBlockPicker.currentRay.x, MouseBlockPicker.currentRay.y, MouseBlockPicker.currentRay.z);
	}
	
	/**
	 * updates the coords state based on if we are crouching
	 */
	public void updateCrouching() {
		if (crouching)
			cords = cordsCrouch;
		else 
			cords = cordsStand;
	}
	
	/**
	 * does the player collide with a position?
	 * returns true if so
	 */
	public boolean hasCollision(Vector3f pos) {
		boolean hadColid = false;
		// check if the normal pos is colliding.
		if (vectorC_I(position.x, position.y, position.z, pos))
			return true;
		// check all the cords in the position array
		for (int i = 0; i < cords.length; i++) {
			if (vectorC_I(position.x + cords[i].x, position.y + cords[i].y, position.z + cords[i].z, pos))
				hadColid = true;
		}
		return hadColid;
	}
	
	/**
	 * returns true if the vectors are the same in integer space.
	 */
	public boolean vectorC_I(float x, float y, float z, Vector3f v2) {
		return ((int)x == (int)v2.x) && ((int)y == (int)v2.y) && ((int)z == (int)v2.z);
	}
	
	/**
	 * update the player
	 */
	public void update() {
		if (pi != null)
			pi.update();
	}
	
	public void cleanup() {
		if (pi != null)
			pi.cleanup();
	}
	
	public PlayerInventory getInventory() {
		return pi;
	}

	public VoxelWorld getWorld() {
		return world;
	}

	public Loader getLoader() {
		return loader;
	}

	public UIMaster getUi() {
		return ui;
	}
	
}
