package com.brett.world.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.world.terrain.Terrain;

public class ThirdPersonPlayer extends Entity {

	protected static final float RUN_SPEED = 20;
	protected static final float TURN_SPEED = 160;
	protected static final float JUMP_POWER = 30;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	public ThirdPersonPlayer(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	@Override
	public void update() {
		checkInputs();
		float time = DisplayManager.getFrameTimeSeconds();
		super.rotate(0, currentTurnSpeed * time, 0);
		float distance = currentSpeed * time;
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.translate(dx, 0, dz);
		upwardsSpeed += GRAVITY * time;
		super.translate(0, upwardsSpeed * time, 0);
	}
	
	public void checkCollision(Terrain terrain) {
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		float offset = 0;
		if (getPosition().y < terrainHeight + offset) {
			getPosition().y = terrainHeight + offset;
			upwardsSpeed = 0;
		}
	}
	
	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentSpeed = -RUN_SPEED;
		} else {
			currentSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentTurnSpeed = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed = -TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) & upwardsSpeed == 0) {
			this.upwardsSpeed = JUMP_POWER;
		}
	}
	
}
