package com.brett.renderer.lighting;

import org.lwjgl.util.vector.Vector3f;

import com.brett.tools.Maths;
import com.brett.world.cameras.Camera;

public class Sun extends Light {

	private Camera player;
	private Vector3f reletaivePosition = new Vector3f(5000, 6000, 5000);
	
	public Sun(Camera player, Vector3f position, Vector3f colour) {
		super(position, colour);
		reletaivePosition = position;
		this.player = player;
	}
	
	public Sun(Camera player, Vector3f position, Vector3f colour, Vector3f attenuation) {
		super(position, colour, attenuation);
		this.player = player;
	}
	
	public void update() {
		this.position = Maths.addVectors(player.getPosition(), reletaivePosition);
	}

}
