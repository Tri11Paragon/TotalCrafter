package com.brett.renderer.lighting;

import org.lwjgl.util.vector.Vector3f;

// light superclass
public class Light {

	protected Vector3f position;
	protected Vector3f colour;
	// MAIN ATTENUATION:
	// AttenFactor = attenuation1 + (attenuation2 * d) + (attenuation3 * d^2)
	
	protected Vector3f attenuation = new Vector3f(1,0,0);
	
	public Light(Vector3f position, Vector3f colour) {
		this.position = position;
		this.colour = colour;
	}
	public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
		this.position = position;
		this.colour = colour;
		this.attenuation = attenuation;				
	}
	public Vector3f getAttenuation() {
		return attenuation;
	}
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return colour;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	
}
