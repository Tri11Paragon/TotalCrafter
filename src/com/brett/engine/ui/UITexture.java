package com.brett.engine.ui;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.engine.managers.DisplayManager;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class UITexture implements UIElement, RescaleEvent {
	
	protected float x,y,width,height;
	public float minx,miny,maxx,maxy;
	private float rminx, rminy, rmaxx, rmaxy;
	public float sx = 1, sy = 1;
	public int t1,t2,t3, z;
	protected Vector3f pos = new Vector3f();
	private Vector2f scale = new Vector2f(1,1);
	public Vector3f color = new Vector3f(-1,0,0);
	public AnchorPoint anchorPoint = AnchorPoint.TOPLEFT;
	
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
	
	public UITexture(int t1, int t2, int t3, float x, float y, float w, float h, AnchorPoint anchor) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.anchorPoint = anchor;
		DisplayManager.rescales.add(this);
		recalculateVectors();
	}
	
	public UIElement setBoundingBox(float minx, float miny, float maxx, float maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
		this.rminx = minx;
		this.rminy = miny;
		this.rmaxx = maxx;
		this.rmaxy = maxy;
		rescale();
		return this;
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
		rescale();
	}

	public void setX(float x) {
		this.x = x;
		pos.x = x;
		rescale();
	}

	public void setY(float y) {
		this.y = y;
		pos.y = y;
		rescale();
	}
	
	public void setZ(float z) {
		this.z = (int) z;
		pos.z = z;
	}

	public void setWidth(float width) {
		this.width = width;
		scale.x = width;
		rescale();
	}

	public void setHeight(float height) {
		this.height = height;
		scale.y = height;
		rescale();
	}

	@Override
	public void destroy() {
		if (anchorPoint != AnchorPoint.TOPLEFT)
			DisplayManager.rescales.remove(this);
	}

	@Override
	public void rescale() {
		switch (anchorPoint) {
			case CENTER:
				pos.x = DisplayManager.WIDTH/2 + x;
				pos.y = DisplayManager.HEIGHT/2 + y;
				if (minx == 0 && maxx == 0)
					return;
				rminx = DisplayManager.WIDTH/2 - minx;
				rminy = DisplayManager.HEIGHT/2 - miny;
				rmaxx = DisplayManager.WIDTH/2 + maxx;
				rmaxy = DisplayManager.HEIGHT/2 + maxy;
				break;
			case BOTTOMCENTER:
				pos.x = DisplayManager.WIDTH/2 + x;
				pos.y = DisplayManager.HEIGHT-height + y;
				break;
			case TOPCENTER:
				pos.x = DisplayManager.WIDTH/2 + x;
				pos.y = y;
				break;
			case LEFTCENTER:
				pos.x = x;
				pos.y = DisplayManager.HEIGHT/2 + y;
				break;
			case RIGHTCENTER:
				pos.x = DisplayManager.WIDTH-width + x;
				pos.y = DisplayManager.HEIGHT/2 + y;
				break;
			case TOPRIGHT:
				pos.x = DisplayManager.WIDTH-width + x;
				break;
			case BOTTOMLEFT:
				pos.y = DisplayManager.HEIGHT-height + y;
				break;
			case BOTTOMRIGHT:
				pos.x = DisplayManager.WIDTH-width + x;
				pos.y = DisplayManager.HEIGHT-height + y;
				break;
			default:
				break;
		}
	}

	@Override
	public float getMinX() {
		return rminx;
	}

	@Override
	public float getMaxX() {
		return rmaxx;
	}

	@Override
	public float getMinY() {
		return rminy;
	}

	@Override
	public float getMaxY() {
		return rmaxy;
	}
	
}
