package com.brett.world.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.tools.TerrainArray;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.terrain.Terrain;

public class CreativeFirstPersonCamera extends Camera {
	
	private VoxelWorld world;
	
	public CreativeFirstPersonCamera(Vector3f pos) {
		this.position = pos;
	}
	
	public void assignWorld(VoxelWorld world) {
		this.world = world;
	}
	
	private float speed = 40f;
	private float turnSpeed = 5.0f;
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	private static int RECUR_AMT = 100;
	
	@Override
	public void move() {
		if (!Mouse.isGrabbed())
			return;
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
			pitch += -Mouse.getDY() * turnSpeed * DisplayManager.getFrameTimeSeconds();
			yaw += Mouse.getDX() * turnSpeed * DisplayManager.getFrameTimeSeconds();
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
		float dx = (float) (Math.round((((-((moveAtX) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) + -((moveatZ) * Math.round(Math.cos(Math.toRadians(yaw))*prez)/prez)))*prez)/prez);
		float dy = (float) ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		float dz = (float) (Math.round((((moveAtX) * Math.round(Math.cos(Math.toRadians(yaw)) * prez)/prez) + -((moveatZ) * Math.round(Math.sin(Math.toRadians(yaw))*prez)/prez))*prez)/prez);
		
		float xStep = (dx)/RECUR_AMT;
		float yStep = (dy)/RECUR_AMT;
		float zStep = (dz)/RECUR_AMT;
		
		float wx = 0, wy = 0, wz = 0;
		float xb = 0, yb = 0, zb = 0;

		for (int i = 0; i < RECUR_AMT; i++) {
			wx += xStep;
			if (world.chunk.getBlock(position.x + (wx), position.y, position.z) == 0) {
				xb = wx;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wy += yStep;
			if (world.chunk.getBlock(position.x, position.y + (wy), position.z) == 0) {
				yb = wy;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wz += zStep;
			if (world.chunk.getBlock(position.x, position.y, position.z + (wz)) == 0) {
				zb = wz;
			} else 
				break;
		}
		
		if (world.chunk.getBlock(position.x + (xb), position.y, position.z) == 0)
			position.x += xb;
		
		if (world.chunk.getBlock(position.x, position.y + (yb), position.z) == 0)
			position.y += yb;
		
		if (world.chunk.getBlock(position.x , position.y, position.z + (zb)) == 0)
			position.z += zb;
	}
	
	public void checkCollision(TerrainArray terrains) {
		Terrain terrain = terrains.get(this.position);
		float offset = 1;
		if (terrain == null) {
			if (this.position.y < offset)
				this.position.y = offset;
			return;
		}
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		if (getPosition().y < terrainHeight + offset) {
			getPosition().y = terrainHeight + offset;
		}
	}
	
}
