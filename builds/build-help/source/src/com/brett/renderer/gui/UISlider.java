package com.brett.renderer.gui;

import java.io.Serializable;

import com.brett.DisplayManager;
import com.brett.tools.InputMaster;

/**
*
* @author brett
* @date May 26, 2020
* Just a special button class (its a slider)
*/

public class UISlider extends UIButton implements UIElement, Serializable {

	private static final long serialVersionUID = 916279185766483552L;
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
		float mx = (float) DisplayManager.mouseX;
		if (isButtonSelected()) {
			if (InputMaster.mouseDown[0]) {
				// if the slider is pressed then we need to change where its slider
				// position is at. this is what this does.
				percent = (mx-px) / (pw);
				if (event != null)
					event.event(name + ":" + percent);
			}
		}
		// this is not the proper way of doing this but the UI stuff is a mess
		// also this class is used like 3 times so it does't really matter rn
		// TODO: redo the whole UI system.
		master.getRenderer().startrender();
		// -10 is to prevent some issues with the thingy going over.
		master.getRenderer().render(bartex, (float)(px + ((pw-10)*percent)), py, 10, ph);
		master.getRenderer().stoprender();
	}
	
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	public double getPercent() {
		return this.percent;
	}
	
}
