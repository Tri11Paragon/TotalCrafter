package com.brett.engine.ui;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class UITexture implements UIElement {
	
	protected float x,y,width,height;
	public float sx = 1, sy = 1;
	public int t1,t2,t3, z;
	private Vector3f pos = new Vector3f();
	private Vector2f scale = new Vector2f(1,1);
	public Vector3f color = new Vector3f(-1,0,0);
	
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

	public UITexture setTextureScaleX(float sx) {
		this.sx = sx;
		return this;
	}

	public UITexture setTextureScaleY(float sy) {
		this.sy = sy;
		return this;
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
	public Vector3f getPosition() {
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
		pos.z = z;
		pos.x = x;
		pos.y = y;
		scale.x = width;
		scale.y = height;
	}

	public void setX(float x) {
		this.x = x;
		pos.x = x;
	}

	public void setY(float y) {
		this.y = y;
		pos.y = y;
	}
	
	public void setZ(float z) {
		this.z = (int) z;
		pos.z = z;
	}

	public void setWidth(float width) {
		this.width = width;
		scale.x = width;
	}

	public void setHeight(float height) {
		this.height = height;
		scale.y = height;
	}

	@Override
	public void destroy() {
	}
	
}
