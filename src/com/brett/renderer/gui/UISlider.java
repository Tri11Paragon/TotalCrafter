package com.brett.renderer.gui;

import org.lwjgl.input.Mouse;

/**
*
* @author brett
* @date May 26, 2020
* Just a special button class (its a slider)
*/

public class UISlider extends UIButton implements UIElement {

	private int bartex;
	private double percent = 0;
	private UIMaster master;
	private String name;
	
	public UISlider(String name, int texture, int bartex, UIControl event, UIMaster master, float x, float y, float width, float height) {
		super(texture, -1, calcVec(x, y), calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.bartex = bartex;
		this.event = event;
		this.master = master;
		this.name = name;
	}
	
	@Override
	public void update() {
		float mx = Mouse.getX();
		if (isButtonSelected()) {
			if (Mouse.isButtonDown(0)) {
				// if the slider is pressed then we need to change where its slider
				// position is at. this is what this does.
				percent = (mx-px) / (pw+10);
				if (event != null)
					event.event(name + ":" + percent);
			}
		}
		// this is not the proper way of doing this but the UI stuff is a mess
		// also this class is used like 3 times so it does't really matter rn
		// TODO: redo the whole UI system.
		master.getRenderer().startrender();
		master.getRenderer().render(bartex, (float)(px + (pw*percent)), py, 10, ph);
		master.getRenderer().stoprender();
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	public double getPercent() {
		return this.percent;
	}
	
}
