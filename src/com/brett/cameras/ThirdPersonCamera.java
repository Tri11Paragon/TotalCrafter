package com.brett.cameras;
/*package com.brett.world.cameras;

import org.lwjgl.input.Mouse;

import com.brett.world.entities.Entity;
import com.brett.world.terrain.Terrain;

UNUSED CLASS

public class ThirdPersonCamera extends Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	private float heightOffset = 6;
	private float minDistanceFromPlayer = 3;
	private float maxDistanceFromPlayer = 3;
	
	private Entity player;
	
	public ThirdPersonCamera(Entity player, float minDistanceFromPlayer, float maxDistanceFromPlayer, float heightOffset) {
		this.player = player;
		super.pitch = 20; // set inital pitch
		this.heightOffset = heightOffset;
		this.minDistanceFromPlayer = minDistanceFromPlayer;
		this.maxDistanceFromPlayer = maxDistanceFromPlayer;
	}
	
	
	// all this was written while tired. it may not be the best of code.
	@Override
	public void move() {
		distanceFromPlayer -= Mouse.getDWheel() * 0.1f; // 0.1 is to make slower (TODO: Add setting for this)
		if(Mouse.isButtonDown(2)) {
			super.pitch -= Mouse.getDY() * 0.1f; // 0.1 is to make slower (TODO: Add setting for this)
		}
		if(Mouse.isButtonDown(1))
			angleAroundPlayer -= Mouse.getDX() * 0.3f;  // 0.1 is to make slower (TODO: Add setting for this)
		if (Mouse.isButtonDown(0) & Mouse.isButtonDown(1))
			angleAroundPlayer = 0;
		if (distanceFromPlayer <= minDistanceFromPlayer)
			distanceFromPlayer = minDistanceFromPlayer;
		if (distanceFromPlayer >= maxDistanceFromPlayer)
			distanceFromPlayer = maxDistanceFromPlayer;
		if(super.pitch < -50)
			super.pitch = -50;
		else if(super.pitch > 90)
			super.pitch = 90;
		float horizDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(super.pitch)));
		float vertDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(super.pitch)));
		
		float totalTheta = player.getRotY() + angleAroundPlayer;
		float cameraOffsetX = (float) (horizDistance * Math.sin(Math.toRadians(totalTheta)));
		float cameraOffsetZ = (float) (horizDistance * Math.cos(Math.toRadians(totalTheta)));
		
		super.position.x = player.getPosition().x - cameraOffsetX;
		super.position.z = player.getPosition().z - cameraOffsetZ;
		super.position.y = player.getPosition().y + vertDistance + heightOffset;
		
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
	
	public void checkCollision(Terrain terrain) {
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		float offset = 1;
		if ((getPosition().y) < terrainHeight + offset) {
			getPosition().y = terrainHeight + offset;
			super.pitch += 1;
		}
	}
	
}*/
