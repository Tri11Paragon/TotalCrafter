package com.brett.voxel.world.player;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.UIMaster;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date May 21, 2020
*/

public class Player extends Camera {
	
	private static final int RECUR_AMT = 100;
	
	private double speed = 5;
	private double turnSpeed = 10.0;
	private double moveAtX = 0;
	private double moveAtY = 0;
	private double moveatZ = 0;
	private boolean onGround = false;
	
	private PlayerInventory pi;
	private VoxelWorld world;
	private Loader loader;
	private UIMaster ui;
	
	private float lowestPoint = -1.75f;
	private Vector3f[] cords = {new Vector3f(-0.25f, lowestPoint, -0.25f), new Vector3f(0.25f, lowestPoint, 0.25f), 
								new Vector3f(0.25f, lowestPoint, -0.25f), new Vector3f(-0.25f, lowestPoint, 0.25f),
								
								new Vector3f(0, lowestPoint/2, 0), new Vector3f(0, lowestPoint/4, 0),
								new Vector3f(0, lowestPoint/2 + lowestPoint/4, 0), new Vector3f(0, lowestPoint/2 + lowestPoint/8, 0),
								
								new Vector3f(-0.25f, 0f, -0.25f), new Vector3f(0.25f, 0f, 0.25f), 
								new Vector3f(0.25f, 0f, -0.25f), new Vector3f(-0.25f, 0f, 0.25f)};
	
	
	public Player(Loader loader, UIMaster ui) {
		pi = new PlayerInventory(ui);
		this.loader = loader;
		this.ui = ui;
		this.position = new Vector3f(0, 80, 0);
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	@Override
	public void move() {
		if (Mouse.isGrabbed()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speed = 2.5f;
			} else
				speed = 5;
			
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
	
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && onGround) {
				moveAtY += 10.25f;
				onGround = false;
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
		}
		
		moveAtY -= (VoxelWorld.GRAVITY*4) * DisplayManager.getFrameTimeSeconds();
		
		if (moveAtY < -VoxelWorld.GRAVITY * 4)
			moveAtY = -VoxelWorld.GRAVITY * 4;
		
		
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;
		
		double dx = (-((moveAtX) * Math.sin(Math.toRadians(yaw))) + -((moveatZ) * Math.cos(Math.toRadians(yaw))) );
		double dy = ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		double dz = ((((moveAtX) * Math.cos(Math.toRadians(yaw))) + -((moveatZ) * Math.sin(Math.toRadians(yaw)))));
		
		double xStep = (dx)/RECUR_AMT;
		double yStep = (dy)/RECUR_AMT;
		double zStep = (dz)/RECUR_AMT;
		
		double wx = 0, wy = 0, wz = 0;
		double xb = 0, yb = 0, zb = 0;

		l1: for (int d = 0; d < RECUR_AMT; d++) {
			wx += xStep;
			if (world.chunk.getBlockCollision(position.x + ((float)wx), position.y, position.z) != COLLISIONTYPE.SOLID) {
				int amt = 0;
				for (int i = 0; i < cords.length; i++) {
					if (world.chunk.getBlockCollision(position.x + cords[i].x + ((float)wx), position.y + cords[i].y, position.z + cords[i].z) != COLLISIONTYPE.SOLID)
						amt++;
					else
						break l1;
				}
				if (amt == cords.length)
					xb = wx;
			} else
				break;
		}
		
		int amty = 0;
		l1: for (int d = 0; d < RECUR_AMT; d++) {
			wy += yStep;
			if (world.chunk.getBlockCollision(position.x, position.y + ((float)wy), position.z) != COLLISIONTYPE.SOLID) {
				for (int i = 0; i < cords.length; i++) {
					if (world.chunk.getBlockCollision(position.x + cords[i].x, position.y + cords[i].y + ((float)wy), position.z + cords[i].z) != COLLISIONTYPE.SOLID)
						amty++;
					else
						break l1;
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
		l1: for (int d = 0; d < RECUR_AMT; d++) {
			wz += zStep;
			if (world.chunk.getBlockCollision(position.x, position.y, position.z + ((float)wz)) != COLLISIONTYPE.SOLID) {
				int amt = 0;
				for (int i = 0; i < cords.length; i++) {
					if (world.chunk.getBlockCollision(position.x + cords[i].x, position.y + cords[i].y, position.z + cords[i].z + ((float)wz)) != COLLISIONTYPE.SOLID)
						amt++;
					else
						break l1;
				}
				if (amt == cords.length)
					zb = wz;
			} else 
				break;
		}
		
		if (world.chunk.getBlock(position.x + ((float)xb), position.y, position.z) == 0)
			position.x += xb;
		
		if (world.chunk.getBlock(position.x, position.y + ((float)yb), position.z) == 0)
			position.y += yb;
		
		if (world.chunk.getBlock(position.x , position.y, position.z + ((float)zb)) == 0)
			position.z += zb;
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
		pi.update();
	}
	
	public void cleanup() {
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
