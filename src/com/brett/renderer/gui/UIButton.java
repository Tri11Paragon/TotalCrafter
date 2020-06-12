package com.brett.renderer.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import com.brett.datatypes.GUITexture;

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
	protected UIControl event;
	
	public UIButton(int texture, int hovertexture, UIControl event, UIMaster master, float x, float y, float width, float height) {
		// calcVec just changes NDC (Normalized device coords) into pixel coords.
		super(texture, -1, master.calcVec(x, y), master.calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.ht = hovertexture;
		this.event = event;
	}
	
	public UIButton(int texture, int texture2, Vector2f position, Vector2f scale) {
		super(texture, texture2, position, scale);
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
	
	/**
	 * updates the button.
	 */
	public void update() {
		if (isButtonSelected()) {
			// change texture to show that we are hovering
			// over the button.
			super.texture2 = ht;
			// do the button event if the user presses the mouse button.
			if (Mouse.isButtonDown(0)) {
				if (event != null)
					event.event(null);
			}
		} else {
			super.texture2 = -1;
		}
	}
	
	/**
	 * returns true if the mouse is under this button.
	 * if you are getting errors make sure you assign the px,py,pw,ph
	 */
	public boolean isButtonSelected() {
		float mx = Mouse.getX();
		float my = Mouse.getY();
		// OpenGL/LWJGL like to put its mouseY starting from the bottom right corner
		// so to put it in a normal coords we take the height and remove the mouse pos.
		my = Display.getHeight() - my;
		// just a simple boundaries check.
		if (mx > px && mx < (px + pw)) {
			if (my > py && my < (py + ph)) {
				return true;
			}
		}
		return false;
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
	
}
