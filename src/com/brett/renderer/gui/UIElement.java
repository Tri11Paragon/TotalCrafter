package com.brett.renderer.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
*
* @author brett
*
*	TODO: maybe switch to an abstract class.
* UIElement type class
*/

public interface UIElement {
	
	public float getTextureScaleX();
	
	public float getTextureScaleY();
	
	public int getTexture();
	
	public int getTexture2();
	
	public int getTexture3();
	
	public Vector2f getPosition();
	
	public Vector2f getScale();
	
	public Vector3f getColor();
	
}
