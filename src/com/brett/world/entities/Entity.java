package com.brett.world.entities;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.tools.TerrainArray;
import com.brett.world.World;
import com.brett.world.terrain.Terrain;

public class Entity {

	public static float GRAVITY = -50;
	
	protected TexturedModel model;
	protected Vector3f position;
	protected Vector3f velocity;
	protected float rotX, rotY, rotZ;
	protected float scale;
	protected float upwardsSpeed = 0;
	
	protected int textureIndex = 0;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}
	
	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex = index;
	}
	
	public Entity(Entity entityToClone) {
		this.model = entityToClone.model;
		this.position = entityToClone.position;
		this.rotX = entityToClone.rotX;
		this.rotY = entityToClone.rotY;
		this.rotZ = entityToClone.rotZ;
		this.scale = entityToClone.scale;
		this.textureIndex = entityToClone.textureIndex;
	}
	
	public Entity(Entity entityToClone, Vector3f pos) {
		this.model = entityToClone.model;
		this.position = pos;
		this.rotX = entityToClone.rotX;
		this.rotY = entityToClone.rotY;
		this.rotZ = entityToClone.rotZ;
		this.scale = entityToClone.scale;
		this.textureIndex = entityToClone.textureIndex;
	}
	
	public Entity(Entity entityToClone, Vector3f pos, float rotX, float rotY, float rotZ, float scale) {
		this.model = entityToClone.model;
		this.position = pos;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex = entityToClone.textureIndex;
	}
	
	public void update() {
		
	}
	
	/**
	 * Do not call update() if you are calling this function.
	 * @param world
	 */
	public void update(World world) {
		update();
	}

	public void translate(float x, float y, float z) {
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;
	}
	
	public void rotate(float x, float y, float z) {
		this.rotX += x;
		this.rotY += y;
		this.rotZ += z;
	}
	
	public void accelerate(float x, float y, float z) {
		this.velocity.x += x;
		this.velocity.y += y;
		this.velocity.z += z;
	}
	
	public void translate(Vector3f delta) {
		Vector3f.add(delta, position, position);
	}
	
	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	public void setRawModel(RawModel model) {
		this.model.setRawModel(model);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Entity setPosition(Vector3f position) {
		this.position = position;
		return this;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public int getTextureIndex() {
		return textureIndex;
	}
	
	public float getTextureXOffset() {
		return (float)(textureIndex % model.getTexture().getNumberOfRows()) / (float) model.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset() {
		return (float)(textureIndex/model.getTexture().getNumberOfRows()) / (float) model.getTexture().getNumberOfRows();
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public boolean checkCollision(TerrainArray terrains) {
		Terrain terrain = terrains.get(this.position);
		float offset = 1;
		if (terrain == null) {
			if (this.position.y < offset) {
				this.position.y = offset;
				this.upwardsSpeed = 0;
				return true;
			}
			return false;
		}
		float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		
		if (getPosition().y < terrainHeight + offset) {
			getPosition().y = terrainHeight + offset;
			this.upwardsSpeed = 0;
			return true;
		}
		return false;
	}

}
