package com.brett.voxel.world.player;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
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
	private static final Vector3f MIN = new Vector3f(-0.25f, -1.75f, -0.25f);
	private static final Vector3f MAX = new Vector3f(0.25f, 0.0f, 0.25f);
	public static boolean flight = false;
	
	private double speed = 5;
	public static double normalSpeed = 6;
	private double turnSpeed = 10.0;
	private double moveAtX = 0;
	private double moveAtY = 0;
	private double moveatZ = 0;
	private double target = 0;
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
			
									  new Vector3f(-size, 0f, -size), new Vector3f(size, 0f, size), 
									  new Vector3f(size, 0f, -size), new Vector3f(-size, 0f, size)};
	
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
		if (Mouse.isGrabbed()) {
			
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				if (!crouching) {
					crouching = true;
					target = 0;
					updateCrouching();
				}
			} else {
				if (crouching) {
					int passed = 0;
					for (int i = 0; i < cords.length; i++) {
						if (world.chunk.getBlock(this.position.x + cords[i].x, this.position.y + 1.0f, this.position.z + cords[i].z) == 0)
							passed++;
					}
					if (passed == cords.length) {
						this.target = 1.0f;
						crouching = false;
						updateCrouching();
					}
				}
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speed = normalSpeed/2;
			} else
				speed = normalSpeed;
			
			if (target > 0)
				speed = 0;
			else 
				speed = normalSpeed;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				moveAtX = -speed * DisplayManager.getFrameTimeSeconds();
			else if (Keyboard.isKeyDown(Keyboard.KEY_S))
				moveAtX = speed * DisplayManager.getFrameTimeSeconds();
			else {
				moveAtX = 0;
				
			}
				
			if (Keyboard.isKeyDown(Keyboard.KEY_A))
				moveatZ = speed * DisplayManager.getFrameTimeSeconds();
			else
			if (Keyboard.isKeyDown(Keyboard.KEY_D))
				moveatZ = -speed * DisplayManager.getFrameTimeSeconds();
			else 
				moveatZ = 0;
	
			if (!flight) {
				if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround) {
					moveAtY += 0.5f/5;
					onGround = false;
				}
			}
		
			pitch += -Mouse.getDY() * (turnSpeed*SettingsLoader.SENSITIVITY)/100;
			yaw += Mouse.getDX() * (turnSpeed*SettingsLoader.SENSITIVITY)/100;
			
			float speed = 30;
			
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
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				moveAtY = speed * DisplayManager.getFrameTimeSeconds();
			else
				moveAtY = 0;
				
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				moveAtY = -speed * DisplayManager.getFrameTimeSeconds();
		} else {
			moveAtY -= (VoxelWorld.GRAVITY/24) * DisplayManager.getFrameTimeSeconds();
			
			if (moveAtY < -VoxelWorld.GRAVITY * 3)
				moveAtY = -VoxelWorld.GRAVITY * 3;
		}
		
		
		if (target > 0) {
			double amount = (VoxelWorld.GRAVITY) * DisplayManager.getFrameTimeSeconds();
			this.position.y += amount;
			this.target -= amount;
		}
		
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;
		
		double dx = (-((moveAtX) * Math.sin(Math.toRadians(yaw))) + -((moveatZ) * Math.cos(Math.toRadians(yaw))) );
		double dy = (moveAtY);
		double dz = ((((moveAtX) * Math.cos(Math.toRadians(yaw))) + -((moveatZ) * Math.sin(Math.toRadians(yaw)))));
		
		double xStep = (dx)/RECUR_AMT;
		double yStep = (dy)/RECUR_AMT;
		double zStep = (dz)/RECUR_AMT;
		
		double wx = 0, wy = 0, wz = 0;
		double xb = 0, yb = 0, zb = 0;
		
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
		
		x1: for (int d = 0; d < RECUR_AMT; d++) {
			wx += xStep;
			if (world.chunk.getBlockCollision(position.x + ((float)wx), position.y, position.z) != COLLISIONTYPE.SOLID) {
				int amt = 0;
				for (int i = 0; i < cords.length; i++) {
					if (world.chunk.getBlockCollision(position.x + cords[i].x + ((float)wx), position.y + cords[i].y, position.z + cords[i].z) != COLLISIONTYPE.SOLID)
						amt++;
					else
						break x1;
				}
				if (amt == cords.length)
					xb = wx;
			} else
				break;
		}
		
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
		
		//if (world.chunk.getBlock(position.x + ((float)xb), position.y, position.z) == 0)
			position.x += xb;
		
		//if (world.chunk.getBlock(position.x, position.y + ((float)yb), position.z) == 0)
			position.y += yb;
		
		//if (world.chunk.getBlock(position.x , position.y, position.z + ((float)zb)) == 0)
			position.z += zb;
		
			
		/**
		 * Sends the server an update of the position of this player
		 * if they have changed positions and if it has been greater then 50 milliseconds
		 * since last update.
		 */
		if (VoxelWorld.isRemote) {
			long current = System.currentTimeMillis();
			if (dx > 0 || dy > 0 || dz > 0 || current - last > 100) {
				if (current - last > 50) {
					VoxelWorld.localClient.updatePosition(this);
					last = current;
				}
			}
		}
		
		AudioController.setListenerPosition(this.position, MouseBlockPicker.currentRay.x, MouseBlockPicker.currentRay.y, MouseBlockPicker.currentRay.z);
	}
	
	/**
	 * returns true if there is an intersection between a box and this player
	 * @param bx - box minimum x
	 * @param by - box minimum y
	 * @param bz - box minimum z
	 * @param bxm - box maximum x
	 * @param bym - box maximum y
	 * @param bzm - box maximum z
	 */
	private boolean checkCollision(float bx, float by, float bz, float bxm, float bym, float bzm) {
		return ((MAX.x + this.position.x) > bx) && ((MIN.x + this.position.x) < bxm) && 
				((MAX.y + this.position.y) > by) && ((MIN.y + this.position.y) < bym) &&
				((MAX.z + this.position.z) > bz) && ((MIN.z + this.position.z) < bzm);
	}
	
	/**
	 * checks collision with blocks around the player.
	 * returns true if they collided with any block.
	 */
	public boolean checkCollisionWithBlocksAroundPlayer() {
		int mx = (int)this.position.x;
		int my = (int)this.position.y;
		int mz = (int)this.position.z;
		boolean b = false;
		
		// check base collision
		if (checkCollision(mx, my, mz, mx+1, my+1, mz+1))
			b = true;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (checkCollision(mx, my, mz, mx + i, my + j, mz + k));
						b = true;
					if (checkCollision(mx + i, my + j, mz + k, mx, my, mz))
						b = true;
				}
			}
		}
		
		return b;
	}
	
	/**
	 * returns true if there is any solid voxels in an area.
	 */
	public boolean solidVoxelsInArea(int minx, int miny, int minz, int maxx, int maxy, int maxz) {
		for (int i = minx; i <= maxx; i++) {
			for (int j = miny; j <= maxy; j++) {
				for (int k = minz; k <= maxz; k++) {
					if (world.chunk.getBlockCollision(i, j, k) == COLLISIONTYPE.SOLID)
						return true;
				}
			}
		}
		return false;
	}
	
	public void updateCrouching() {
		if (crouching)
			cords = cordsCrouch;
		else 
			cords = cordsStand;
	}
	
	public float round(double r) {
		return Math.round(r*1000d)/1000f;
	}
	
	public boolean hasCollision(Vector3f pos) {
		boolean hadColid = false;
		for (int i = 0; i < cords.length; i++) {
			if (vectorC_I(position.x + cords[i].x, position.y + cords[i].y, position.z + cords[i].z, pos))
				hadColid = true;
		}
		if (vectorC_I(position.x, position.y, position.z, pos))
			return true;
		return hadColid;
	}
	
	public boolean vectorC_I(float x, float y, float z, Vector3f v2) {
		return ((int)x == (int)v2.x) && ((int)y == (int)v2.y) && ((int)z == (int)v2.z);
	}
	
	public boolean vectorC_I(Vector3f v1, Vector3f v2) {
		return ((int)v1.x == (int)v2.x) && ((int)v1.y == (int)v2.y) && ((int)v1.z == (int)v2.z);
	}
	
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
