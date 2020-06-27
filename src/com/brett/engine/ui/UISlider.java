package com.brett.engine.ui;

import com.brett.engine.Info;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;

/**
 * @author Brett
 * @date Jun. 25, 2020
 */

public class UISlider extends UIButton {

	public double percent;
	public UITexture barTexture;
	public boolean selected = false;
	
	public UISlider(int t, UITexture barTexture, float x, float y, float w, float h, AnchorPoint anchor) {
		super(t, -1, x, y, w, h, anchor);
		this.barTexture = barTexture;
		pos.y = pos.y;	
	}

	public UISlider(int t, UITexture barTexture, float x, float y, float w, float h) {
		super(t, -1, x, y, w, h);
		this.barTexture = barTexture;
		pos.y = pos.y;	
	}

	@Override
	public UISlider setEvent(ButtonEvent e) {
		event = e;
		return this;
	}
	
	@Override
	public void update() {
		if (InputMaster.mouseDown[0]) {
			if (isButtonSelected(1,0)) {
				if (!Info.isSelected) {
					Info.isSelected = true;
					selected = true;
				}
			}
		} else {
			if (selected) {
				selected = false;
				Info.isSelected = false;
			}
		}
		if (selected) {
			float mx = (float) DisplayManager.mouseX;
			float sx = this.width-barTexture.width;
			float centeradjust = 0;
			if (anchorPoint == AnchorPoint.CENTER)
				centeradjust = width/2;
			percent = (mx-pos.x-centeradjust)/(sx);
			if (mx < pos.x) {
				barTexture.setX(pos.x);
				percent = 0;
				if (event != null)
					event.event("0.0");
				return;
			}
			if (mx > pos.x + width) {
				barTexture.setX(pos.x + width-barTexture.width);
				percent = 1;
				if (event != null)
					event.event("1.0");
				return;
			}
			percent = Math.max(Math.min(percent, 1.0), 0.0);
			if (event != null)
				event.event(percent + "");
			barTexture.setHeight(height);
			if (mx <= pos.x + width-barTexture.width)
				barTexture.setX(mx);
			pos.y = pos.y;	
		}
	}
	
	@Override
	public void onMouseReleased(int button) {
		
	}

}
