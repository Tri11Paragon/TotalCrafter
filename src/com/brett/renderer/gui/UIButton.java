package com.brett.renderer.gui;

import org.lwjgl.input.Mouse;

import com.brett.renderer.datatypes.GUITexture;

/**
*
* @author brett
*
* never use this outside ui master.
*
*/

public class UIButton extends GUITexture implements UIElement {
	
	private float px, py, pw, ph;
	private int ht;
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
	
	public void update() {
		float mx = Mouse.getX();
		float my = Mouse.getY();
		if (mx > px && mx < (px + pw)) {
			if (my > py && my < (py + ph)) {
				super.texture2 = ht;
				if (Mouse.isButtonDown(1))
					event.event(null);
			} else {
				super.texture2 = -1;
			}
		} else {
			super.texture2 = -1;
		}
	}
	
}
