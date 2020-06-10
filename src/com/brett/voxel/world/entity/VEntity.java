package com.brett.voxel.world.entity;

import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Mar. 27, 2020
*/

public class VEntity {
	
	private Vector3f posision;
	private Vector3f velocity;
	private Vector3f acceleration;
	/**
	 * Note: Drag must be under 1 or else they will cause the entity
	 * to accelerate faster
	 * 
	 * They also need to be positive.
	 */
	private Vector3f drag;
	private float rot, airResistance, mass;
	private VoxelWorld world;
	private TexturedModel model;
	
	public VEntity(Vector3f pos, float rot, TexturedModel model) {
		this.posision = pos;
		this.rot = rot;
		initVectors();
		this.model = model;
	}
	
	private void initVectors(){
		this.velocity = new Vector3f();
		this.acceleration = new Vector3f();
		this.drag = new Vector3f(1,1,1);
		this.airResistance = 1;
	}
	
	// yes it is wrong but its fine.
	public void update() {
		// apply gravity
		this.acceleration.y -= VoxelWorld.GRAVITY;
		// apply air resistance
		this.acceleration.x /= (airResistance);
		this.acceleration.y /= (airResistance);
		this.acceleration.z /= (airResistance);
		// apply drag
		this.acceleration.x *= drag.x;
		this.acceleration.y *= drag.y;
		this.acceleration.z *= drag.z;
		// apply acceleration
		this.velocity.x += acceleration.x * DisplayManager.getFrameTimeSeconds();
		this.velocity.y += acceleration.y * DisplayManager.getFrameTimeSeconds();
		this.velocity.z += acceleration.z * DisplayManager.getFrameTimeSeconds();
		// apply position and check collision.
		if (world.chunk.getBlock(this.posision.x + (velocity.x * DisplayManager.getFrameTimeSeconds()), this.posision.y, this.posision.z) == 0)
			this.posision.x += velocity.x * DisplayManager.getFrameTimeSeconds();
		else {
			this.velocity.x = 0;
			this.acceleration.x = 0;
		}
		if (world.chunk.getBlock(this.posision.x, this.posision.y + (velocity.y * DisplayManager.getFrameTimeSeconds()), this.posision.z) == 0)
			this.posision.y += velocity.y * DisplayManager.getFrameTimeSeconds();
		else {
			this.velocity.y = 0;
			this.acceleration.y = 0;
		}
		if (world.chunk.getBlock(this.posision.x, this.posision.y, this.posision.z + (velocity.z * DisplayManager.getFrameTimeSeconds())) == 0)
			this.posision.z += velocity.z * DisplayManager.getFrameTimeSeconds();
		else {
			this.velocity.z = 0;
			this.acceleration.z = 0;
		}
	}
	
	/**
	 * Entity Functions below
	 */
	
	/**
	 * Called when entity is spawned
	 */
	public void onEntitySpawned(VoxelWorld world) {
		this.world = world;
	}
	
	
	
	/**
	 * Getters and Setters below.
	 */

	public Vector3f getPos() {
		return posision;
	}

	public float getRot() {
		return rot;
	}
	
	public Vector3f getAcceleration() {
		return acceleration;
	}

	public VEntity setAcceleration(Vector3f acceleration) {
		this.acceleration = acceleration;
		return this;
	}
	
	public VEntity increaseAcceleration(Vector3f acceleration) {
		Vector3f.add(this.acceleration, acceleration, this.acceleration);
		return this;
	}
	
	public VEntity increaseAcceleration(float x, float y, float z) {
		this.acceleration.x += x;
		this.acceleration.y += y;
		this.acceleration.z += z;
		return this;
	}
	
	public TexturedModel getModel() {
		return this.model;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public VEntity setVelocity(Vector3f velocity) {
		this.velocity = velocity;
		return this;
	}
	
	public void increaseVelocity(Vector3f velocity) {
		Vector3f.add(this.velocity, velocity, this.velocity);
	}
	
	public VEntity increaseVelocity(float x, float y, float z) {
		this.velocity.x += x;
		this.velocity.y += y;
		this.velocity.z += z;
		return this;
	}

	public Vector3f getDrag() {
		return drag;
	}

	public VEntity setDrag(Vector3f drag) {
		this.drag = drag;
		return this;
	}

	public float getAirResistance() {
		return airResistance;
	}

	public VEntity setAirResistance(float airResistance) {
		this.airResistance = airResistance;
		return this;
	}

	public float getMass() {
		return mass;
	}

	public VEntity setMass(float mass) {
		this.mass = mass;
		return this;
	}
	
}
