package com.brett.world.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.world.World;
import com.brett.world.cameras.Camera;
import com.brett.world.cameras.FirstPersonCamera;

/**
*
* @author brett
*
*/

public class FirstPersonPlayer extends Entity {
	
	private FirstPersonCamera camera;
	private static final float DRAG_EFFECT = 0.991348f;
	private static final float MAX_SIDE_SPEED = 0.4f;
	private static final float MAX_FORWARD_SPEED = 0.4f;
	
	private Vector3f offset = new Vector3f(0,0,0);
	private float speed = 40f;
	private float turnSpeed = 5.0f;
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float dpitch = 0;
	private float dyaw = 0;
	
	public FirstPersonPlayer(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.camera = new FirstPersonCamera();
	}
	public FirstPersonPlayer(TexturedModel model, Vector3f position, Vector3f offset, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.camera = new FirstPersonCamera();
		this.offset = offset;
	}
	
	@Override
	public void update(World world) {
		float time = DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) & this.upwardsSpeed == 0) {
			this.upwardsSpeed = 60;
		}
		this.upwardsSpeed += GRAVITY * time;
		this.translate(0, (this.upwardsSpeed * time), 0);
		if (Mouse.isGrabbed()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				speed = 5f;
			} else
				speed = 40f;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W))
				moveAtX = -speed * time;
			else if (Keyboard.isKeyDown(Keyboard.KEY_S))
				moveAtX = speed * time;
				
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				moveAtY += speed * time;
				if (moveAtY > MAX_SIDE_SPEED) {
					moveAtY = MAX_SIDE_SPEED;
				}
			} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				moveAtY += -speed * time;
				if (moveAtY < MAX_SIDE_SPEED) {
					moveAtY = -MAX_SIDE_SPEED;
				}
			}
				
			float speed = 30f;
			
			dpitch += -Mouse.getDY() * turnSpeed * time;
			dyaw += Mouse.getDX() * turnSpeed * time;
			
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
				dyaw += -speed * turnSpeed * time;
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
				dyaw += speed * turnSpeed * time;
			if (Keyboard.isKeyDown(Keyboard.KEY_UP))
				dpitch += -speed * turnSpeed * time;
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
				dpitch += speed * turnSpeed * time;
		}
		
		float dx = (float) (-((moveAtX) * Math.sin(Math.toRadians(camera.getYaw())))) + (float)-((moveAtY) * Math.cos(Math.toRadians(camera.getYaw())));
		float dy = (float) (moveAtX * Math.sin(Math.toRadians(camera.getRoll())));
		float dz = (float) ((moveAtX) * Math.cos(Math.toRadians(camera.getYaw()))) + (float) -((moveAtY) * Math.sin(Math.toRadians(camera.getYaw())));
			
		this.translate(dx, dy, dz);
		camera.changeView(dyaw, dpitch);
		this.rotY = -camera.getYaw();
			
		dpitch = 0;
		dyaw = 0;
		camera.setPosition(this.position, offset);
		if(this.checkCollision(world.getTerrains())) {
			moveAtY = 0;
			moveAtX = 0;
		} else {
			moveAtY *= DRAG_EFFECT;
			moveAtX *= DRAG_EFFECT;
		}
	}
	
	public Camera getCamera() {
		return camera;
	}
	
}
