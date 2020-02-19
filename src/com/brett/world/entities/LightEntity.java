package com.brett.world.entities;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.TexturedModel;
import com.brett.renderer.lighting.Light;
import com.brett.tools.Maths;

/**
*
* @author brett
*
*/

public class LightEntity extends Entity {

	private Light light;
	
	public LightEntity(TexturedModel model, Light light, Vector3f position, Vector3f posRelative, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.light = light;
		this.light.setPosition(Maths.addVectors(super.position, posRelative));
	}
	
	public LightEntity(LightEntity entityToClone) {
		super(entityToClone);
		this.light = entityToClone.light;
	}
	
	public void attachLight(Light light, Vector3f posRelative) {
		this.light = light;
		this.light.setPosition(Maths.addVectors(super.position, posRelative));
	}

}
