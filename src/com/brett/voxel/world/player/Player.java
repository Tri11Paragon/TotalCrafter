package com.brett.voxel.world.player;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.UIMaster;
import com.brett.sound.AudioController;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date May 21, 2020
*/

public class Player extends Camera {
	
	private static final int RECUR_AMT = 5;
	public static boolean flight = false;
	
	private double speed = 5;
	public static double normalSpeed = 6;
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
	private float size = 0.25f;
	private Vector3f[] cords = {new Vector3f(-size, lowestPoint, -size), new Vector3f(size, lowestPoint, size), 
								new Vector3f(size, lowestPoint, -size), new Vector3f(-size, lowestPoint, size),
								
								new Vector3f(-size, lowestPoint/2, -size), new Vector3f(size, lowestPoint/2, size), 
								new Vector3f(size, lowestPoint/2, -size), new Vector3f(-size, lowestPoint/2, size),
								
								new Vector3f(-size, lowestPoint/4, -size), new Vector3f(size, lowestPoint/4, size), 
								new Vector3f(size, lowestPoint/4, -size), new Vector3f(-size, lowestPoint/4, size),
								
								/*new Vector3f(-0.25f, lowestPoint/2 + lowestPoint/4, -0.25f), new Vector3f(0.25f, lowestPoint/2 + lowestPoint/4, 0.25f), 
								new Vector3f(0.25f, lowestPoint/2 + lowestPoint/4, -0.25f), new Vector3f(-0.25f, lowestPoint/2 + lowestPoint/4, 0.25f),
								
								new Vector3f(-0.25f, lowestPoint/2 - lowestPoint/4, -0.25f), new Vector3f(0.25f, lowestPoint/2 - lowestPoint/4, 0.25f), 
								new Vector3f(0.25f, lowestPoint/2 - lowestPoint/4, -0.25f), new Vector3f(-0.25f, lowestPoint/2 - lowestPoint/4, 0.25f),
								
								new Vector3f(-0.25f, lowestPoint/2 + lowestPoint/8, -0.25f), new Vector3f(0.25f, lowestPoint/2 + lowestPoint/8, 0.25f), 
								new Vector3f(0.25f, lowestPoint/2 + lowestPoint/8, -0.25f), new Vector3f(-0.25f, lowestPoint/2 + lowestPoint/8, 0.25f),
								
								new Vector3f(-0.25f, lowestPoint/2 - lowestPoint/8, -0.25f), new Vector3f(0.25f, lowestPoint/2 - lowestPoint/8, 0.25f), 
								new Vector3f(0.25f, lowestPoint/2 - lowestPoint/8, -0.25f), new Vector3f(-0.25f, lowestPoint/2 - lowestPoint/8, 0.25f),*/
								
								new Vector3f(0, lowestPoint/2, 0), new Vector3f(0, lowestPoint/4, 0),
								new Vector3f(0, lowestPoint/2 + lowestPoint/4, 0), new Vector3f(0, lowestPoint/2 + lowestPoint/8, 0),
								
								new Vector3f(-size, 0f, -size), new Vector3f(size, 0f, size), 
								new Vector3f(size, 0f, -size), new Vector3f(-size, 0f, size)};
	
	
	public Player(Loader loader, UIMaster ui) {
		pi = new PlayerInventory(ui);
		this.loader = loader;
		this.ui = ui;
		this.position = new Vector3f(0, 120, 0);
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	@Override
	public void move() {
		if (Mouse.isGrabbed()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speed = normalSpeed/2;
			} else
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
					if (world.chunk.getBlockCollision(position.x + cords[i].x, position.y + cords[i].y + ((float)wy), position.z + cords[i].z) != COLLISIONTYPE.SOLID)
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
		
		if (world.chunk.getBlock(position.x + ((float)xb), position.y, position.z) == 0)
			position.x += xb;
		
		if (world.chunk.getBlock(position.x, position.y + ((float)yb), position.z) == 0)
			position.y += yb;
		
		if (world.chunk.getBlock(position.x , position.y, position.z + ((float)zb)) == 0)
			position.z += zb;
		
		AudioController.setListenerPosition(this.position, MouseBlockPicker.currentRay.x, MouseBlockPicker.currentRay.y, MouseBlockPicker.currentRay.z);
	}
	
	public void generateCollisionVectors() {
		/*float forwardX = round(size * Math.sin(Math.toRadians(yaw)));
		float backwardX = round(size * Math.sin(Math.toRadians(yaw+180)));
		float leftX = round(size * Math.sin(Math.toRadians(yaw-90)));
		float rightX = round(size * Math.sin(Math.toRadians(yaw+90)));
		
		float forwardZ = round(size * -Math.cos(Math.toRadians(yaw)));
		float backwardZ = round(size * -Math.cos(Math.toRadians(yaw+180)));
		float leftZ = round(size * -Math.cos(Math.toRadians(yaw-90)));
		float rightZ = round(size * -Math.cos(Math.toRadians(yaw+90)));
		
		cords[0].x = forwardX;
		cords[0].z = forwardZ;
		
		cords[1].x = backwardX;
		cords[1].z = backwardZ;
		
		cords[2].x = rightX;
		cords[2].z = rightZ;
		
		cords[3].x = leftX;
		cords[3].z = leftZ;
		
		
		cords[4].x = forwardX;
		cords[4].z = forwardZ;
		
		cords[5].x = backwardX;
		cords[5].z = backwardZ;
		
		cords[6].x = rightX;
		cords[6].z = rightZ;
		
		cords[7].x = leftX;
		cords[7].z = leftZ;
		
		
		cords[8].x = forwardX;
		cords[8].z = forwardZ;
		
		cords[9].x = backwardX;
		cords[9].z = backwardZ;
		
		cords[10].x = rightX;
		cords[10].z = rightZ;
		
		cords[11].x = leftX;
		cords[11].z = leftZ;
		
		
		cords[12].x = forwardX;
		cords[12].z = forwardZ;
		
		cords[13].x = backwardX;
		cords[13].z = backwardZ;
		
		cords[14].x = rightX;
		cords[14].z = rightZ;
		
		cords[15].x = leftX;
		cords[15].z = leftZ;*/
		
		
		//double sx = Math.round(Math.abs(-(size * Math.sin(Math.toRadians(yaw))) + -(size * Math.cos(Math.toRadians(yaw)))) * 1000d)/1000d;
		//double sz = Math.round(Math.abs((size) * Math.cos(Math.toRadians(yaw)) + -(size * Math.sin(Math.toRadians(yaw)))) * 1000d)/1000d;
		/*System.out.println("SX: " + sx + " :: SZ: " + sz);
		for (int i = 0; i < cords.length; i++) {
			if (cords[i].x < 0)
				cords[i].x = (float) -sx;
			else
				cords[i].x = (float) sx;
			if (cords[i].z < 0)
				cords[i].z = (float) -sz;
			else
				cords[i].z = (float) sz;
		}*/
		//VoxelScreenManager.pt.addStaticPoint(new Vector3f(position.x + (float)round(size * Math.sin(Math.toRadians(yaw))), position.y+1, position.z + (float) round(size * -Math.cos(Math.toRadians(yaw)))));
		//VoxelScreenManager.ls.renderIN(position, new Vector3f(position.x + (float)round( Math.sin(Math.toRadians(yaw))), position.y-1, position.z + (float) round(-Math.cos(Math.toRadians(yaw)))));
		//VoxelScreenManager.ls.renderIN(position, new Vector3f(position.x+1, position.y-2, position.z + 5));
		/*VoxelScreenManager.ls.renderIN(new Vector3f(position.x,70,position.z), 
				new Vector3f((float) (position.x+(cords[0].x*10)),90,(float) (position.z+(cords[0].z*10))));
		VoxelScreenManager.ls.renderIN(new Vector3f(position.x,70,position.z), 
				new Vector3f((float) (position.x+round(10 * Math.sin(Math.toRadians(yaw+90)))),90,(float) (position.z+round(10 * -Math.cos(Math.toRadians(yaw+90))))));
		VoxelScreenManager.ls.renderIN(new Vector3f(position.x,70,position.z), 
				new Vector3f((float) (position.x+round(10 * Math.sin(Math.toRadians(yaw-90)))),90,(float) (position.z+round(10 * -Math.cos(Math.toRadians(yaw-90))))));*/
		//System.out.println("LP: " + round(size * Math.sin(Math.toRadians(yaw))) + " :: " + round(size * -Math.cos(Math.toRadians(yaw))));
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
