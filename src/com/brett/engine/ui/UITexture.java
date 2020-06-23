package com.brett.engine.ui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.engine.managers.DisplayManager;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class UITexture implements UIElement {
	
	public static List<UITexture> textures = new ArrayList<UITexture>();
	
	protected float x,y,width,height;
	public float sx, sy;
	public int t1,t2,t3;
	private Vector2f pos = new Vector2f();
	private Vector2f scale = new Vector2f();
	public Vector3f color = new Vector3f();
	
	public UITexture(int t1, int t2, int t3, float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		recalculateVectors();
	}
	
	public UITexture(int t1, int t2, int t3, float textureScaleX, float textureScaleY, float x, float y, float w, float h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.sx = textureScaleX;
		this.sy = textureScaleY;
		recalculateVectors();
	}
	
	@Override
	public float getTextureScaleX() {
		return sx;
	}

	@Override
	public float getTextureScaleY() {
		return sy;
	}

	@Override
	public int getTexture() {
		return t1;
	}

	@Override
	public int getTexture2() {
		return t2;
	}

	@Override
	public int getTexture3() {
		return t3;
	}

	@Override
	public Vector2f getPosition() {
		return pos;
	}

	@Override
	public Vector2f getScale() {
		return scale;
	}

	@Override
	public Vector3f getColor() {
		return color;
	}

	@Override
	public void update() {
	}
	
	public void recalculateVectors() {
		calculateVector(pos, x, y);
		calculateVector(scale, width, height);
	}
	
	public Vector2f calculateVector(Vector2f vecs, float x, float y) {
		vecs.x = x / DisplayManager.WIDTH;
		vecs.y = y / DisplayManager.HEIGHT;
		return vecs;
	}

	public void setX(float x) {
		this.x = x;
		calculateVector(pos, x, y);
	}

	public void setY(float y) {
		this.y = y;
		calculateVector(pos, x, y);
	}

	public void setWidth(float width) {
		this.width = width;
		calculateVector(scale, width, height);
	}

	public void setHeight(float height) {
		this.height = height;
		calculateVector(scale, width, height);
	}

	@Override
	public void destroy() {
		for (int i = 0; i < textures.size(); i++) {
			if (textures.get(i) == this) {
				textures.remove(i);
				return;
			}
		}
	}
	
	public static void screenSizeChange() {
		for (int i = 0; i < textures.size(); i++)
			textures.get(i).recalculateVectors();
	}
	
}
