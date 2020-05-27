package com.brett.renderer.datatypes;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.gui.UIElement;

public class GUITexture implements UIElement {
	
	protected int texture;
	protected int texture2 = -1;
	protected int texture3 = -1;
	public Vector2f position;
	public Vector2f scale;
	public Vector3f color;
	public float textureScaleX = 1;
	public float textureScaleY = 1;
	
	public GUITexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}
	
	public GUITexture(int texture, int texture2, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.texture2 = texture2;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}

	
	public GUITexture(int texture, int texture2, int texture3, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.texture2 = texture2;
		this.texture3 = texture3;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}

	public GUITexture(int texture, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		this.texture = texture;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}
	
	public GUITexture(int texture, int texture2, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		this.texture = texture;
		this.texture2 = texture2;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}

	
	public GUITexture(int texture, int texture2, int texture3, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		this.texture = texture;
		this.texture2 = texture2;
		this.texture3 = texture3;
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}
	
	public GUITexture setColor(Vector3f color) {
		this.color = color;
		return this;
	}
	
	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	@Override
	public int getTexture2() {
		return texture2;
	}

	@Override
	public int getTexture3() {
		return texture3;
	}

	@Override
	public Vector3f getColor() {
		return color;
	}

	@Override
	public float getTextureScaleX() {
		return textureScaleX;
	}

	@Override
	public float getTextureScaleY() {
		return textureScaleY;
	}
	
	public static Vector2f calcVec(float x, float y) {
		return new Vector2f(x / Display.getWidth(), y / Display.getHeight());
	}
	
}
