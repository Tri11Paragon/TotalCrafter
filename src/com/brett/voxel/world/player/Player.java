package com.brett.voxel.world.player;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date May 21, 2020
*/

public class Player extends Camera {
	
	private static final int RECUR_AMT = 100;
	
	private double speed = 40;
	private double turnSpeed = 5.0;
	private double moveAtX = 0;
	private double moveAtY = 0;
	private double moveatZ = 0;
	
	private PlayerInventory pi;
	private VoxelWorld world;
	private Loader loader;
	private UIMaster ui;
	
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
		if (!Mouse.isGrabbed())
			return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			speed = 5;
			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				speed=1;
		} else
			speed = 40;
		
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
		
		float speed = 30;
		
		if (Mouse.isGrabbed()) {
			pitch += -Mouse.getDY() * turnSpeed/100;
			yaw += Mouse.getDX() * turnSpeed/100;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			yaw += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			yaw += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			pitch += -speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			pitch += speed * turnSpeed * DisplayManager.getFrameTimeSeconds();
		
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;
		
		double prez = 1000000d;
		double dx = (Math.round((((-((moveAtX) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) + -((moveatZ) * Math.round(Math.cos(Math.toRadians(yaw))*prez)/prez)))*prez)/prez);
		double dy = ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		double dz = (Math.round((((moveAtX) * Math.round(Math.cos(Math.toRadians(yaw)) * prez)/prez) + -((moveatZ) * Math.round(Math.sin(Math.toRadians(yaw))*prez)/prez))*prez)/prez);
		
		double xStep = (dx)/RECUR_AMT;
		double yStep = (dy)/RECUR_AMT;
		double zStep = (dz)/RECUR_AMT;
		
		double wx = 0, wy = 0, wz = 0;
		double xb = 0, yb = 0, zb = 0;

		for (int i = 0; i < RECUR_AMT; i++) {
			wx += xStep;
			if (world.chunk.getBlock(position.x + ((float)wx), position.y, position.z) == 0) {
				xb = wx;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wy += yStep;
			if (world.chunk.getBlock(position.x, position.y + ((float)wy), position.z) == 0) {
				yb = wy;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wz += zStep;
			if (world.chunk.getBlock(position.x, position.y, position.z + ((float)wz)) == 0) {
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
