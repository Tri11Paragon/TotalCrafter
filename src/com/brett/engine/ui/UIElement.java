package com.brett.engine.ui;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public interface UIElement {
	
	public float getTextureScaleX();
	
	public float getTextureScaleY();
	
	public int getTexture();
	
	public int getTexture2();
	
	public int getTexture3();
	
	public Vector3f getPosition();
	
	public Vector2f getScale();
	
	public Vector3f getColor();
	
	public void update();
	
	public void destroy();
	
}
