package com.brett.world.cameras;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.tools.TerrainArray;
import com.brett.world.terrain.Terrain;

public class CreativeFirstPersonCamera extends Camera {
	
	public CreativeFirstPersonCamera(Vector3f pos) {
		this.position = pos;
	}
	
	private float speed = 40f;
	private float turnSpeed = 5.0f;
	private float moveAtX = 0;
	private float moveAtY = 0;
	
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
			moveAtY = speed * DisplayManager.getFrameTimeSeconds();
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveAtY = -speed * DisplayManager.getFrameTimeSeconds();
		else 
			moveAtY = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			position.y += speed * DisplayManager.getFrameTimeSeconds();
			
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			position.y -= speed * DisplayManager.getFrameTimeSeconds();
		
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
		
		float dx = (float) (-((moveAtX) * Math.sin(Math.toRadians(yaw)))) + (float)-((moveAtY) * Math.cos(Math.toRadians(yaw)));
		float dy = (float) (moveAtX * Math.sin(Math.toRadians(roll)));
		float dz = (float) ((moveAtX) * Math.cos(Math.toRadians(yaw))) + (float) -((moveAtY) * Math.sin(Math.toRadians(yaw)));
		
		position.x += dx;
		position.y += dy;
		position.z += dz;
		
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
