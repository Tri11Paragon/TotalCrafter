package com.brett.datatypes;

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
	
	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 */
	public GUITexture(int texture, Vector2f position, Vector2f scale) {
		assignTextures(texture, -1, -1);
		this.scale = scale;
		// im not sure why I have it scaled like this but I think it
		// has something to do with the fact that normalized device coords
		// goes from -1 to 1 instead of 0 to 1
		// so it has a magnitude of 2 instead of one and since im dividing by
		// the screen width and height, I have to get the magnitude to 1.
		// thats just a guess and like I said the whole UI renderer is a complete inconsistent mess.
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}
	
	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param texture2 the second texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 */
	public GUITexture(int texture, int texture2, Vector2f position, Vector2f scale) {
		assignTextures(texture, texture2, -1);
		// see ^
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}
	
	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param texture2 the second texture id
	 * @param texture3 the third texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 */
	public GUITexture(int texture, int texture2, int texture3, Vector2f position, Vector2f scale) {
		assignTextures(texture, texture2, texture3);
		// see ^
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
	}

	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 * @param textureScaleX scales the image by this # (2 would produce the image twice in the x direction)
	 * @param textureScaleY scales the image by this # (2 would produce the image twice in the y direction)
	 */
	public GUITexture(int texture, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		assignTextures(texture, -1, -1);
		// see ^
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}
	
	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param texture2 the second texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 * @param textureScaleX scales the image by this # (2 would produce the image twice in the x direction)
	 * @param textureScaleY scales the image by this # (2 would produce the image twice in the y direction)
	 */
	public GUITexture(int texture, int texture2, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		assignTextures(texture, texture2, -1);
		// see ^
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}

	/**
	 * Creates a GUITexture
	 * @param texture the texture id
	 * @param texture2 the second texture id
	 * @param texture3 the third texture id
	 * @param position the position this texture will be (screen space)
	 * @param scale width and height of the image (screen space)
	 * @param textureScaleX scales the image by this # (2 would produce the image twice in the x direction)
	 * @param textureScaleY scales the image by this # (2 would produce the image twice in the y direction)
	 */
	public GUITexture(int texture, int texture2, int texture3, Vector2f position, Vector2f scale, float textureScaleX, float textureScaleY) {
		assignTextures(texture, texture2, texture3);
		// see ^
		this.scale = scale;
		this.scale.x = (scale.x / 2);
		this.scale.y = (scale.y / 2);
		this.position = position;
		this.position.x = (position.x + (scale.x));
		this.position.y = (position.y + (scale.y));
		this.textureScaleX = textureScaleX;
		this.textureScaleY = textureScaleY;
	}
	
	/**
	 * assigns the textures in this class
	 */
	private void assignTextures(int texture, int texture2, int texture3) {
		this.texture = texture;
		this.texture2 = texture2;
		this.texture3 = texture3;
	}
	
	/**
	 * converts pixel coords into screen space coords.
	 */
	public static Vector2f calcVec(float x, float y) {
		return new Vector2f(x / Display.getWidth(), y / Display.getHeight());
	}
	
	/**
	 * getter and setter methods below.
	 */
	
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
	
}
