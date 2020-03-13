package com.brett.renderer.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.datatypes.GUITexture;

/**
*
* @author brett
*
* never use this outside ui master.
*
*/

public class UIButton extends GUITexture implements UIElement {
	
	protected float px, py, pw, ph;
	protected int ht;
	private UIControl event;
	
	public UIButton(int texture, int hovertexture, UIControl event, UIMaster master, float x, float y, float width, float height) {
		super(texture, -1, master.calcVec(x, y), master.calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.ht = hovertexture;
		this.event = event;
	}
	
	public UIButton(int texture, int hovertexture, UIControl event, float x, float y, float width, float height) {
		super(texture, -1, calcVec(x, y), calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.ht = hovertexture;
		this.event = event;
	}
	
	public void update() {
		float mx = Mouse.getX();
		float my = Mouse.getY();
		my = Display.getHeight() - my;
		if (mx > px && mx < (px + pw)) {
			if (my > py && my < (py + ph)) {
				super.texture2 = ht;
				if (Mouse.isButtonDown(0)) {
					if (event != null)
						event.event(null);
				}
			} else {
				super.texture2 = -1;
			}
		} else {
			super.texture2 = -1;
		}
	}
	
	public void setPos(float x, float y) {
		this.position = calcVec(x, y);
		this.px = x;
		this.py = y;
	}
	
	public void setScale(float width, float height) {
		this.scale = calcVec(width, height);
		this.pw = width;
		this.ph = height;
	}
	
	public Vector2f getPos() {
		return new Vector2f(px, py);
	}
	
	public Vector2f getSc() {
		return new Vector2f(pw, ph);
	}
	
	public float getPx() {
		return px;
	}
	
	public float getPy() {
		return py;
	}
	
	public float getPw() {
		return pw;
	}
	
	public float getPh() {
		return ph;
	}
	
	public static Vector2f calcVec(float x, float y) {
		return new Vector2f(x / Display.getWidth(), y / Display.getHeight());
	}
	
}
