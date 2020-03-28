package com.brett.voxel.world.entity;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.RawModel;

/**
*
* @author brett
* @date Mar. 27, 2020
*/

public class VEntity {
	
	private Vector3f pos;
	private float rot;
	private RawModel model;
	
	public VEntity(Vector3f pos, float rot, RawModel model) {
		this.pos = pos;
		this.rot = rot;
		this.model = model;
	}
	
	public void update() {
		
	}
	
	public void onEntitySpawned(float x, float y, float z) {
		
	}

	public Vector3f getPos() {
		return pos;
	}

	public float getRot() {
		return rot;
	}

	public RawModel getModel() {
		return model;
	}
	
}
