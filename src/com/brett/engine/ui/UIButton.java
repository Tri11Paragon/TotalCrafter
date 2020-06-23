package com.brett.engine.ui;

import com.brett.engine.data.IMouseState;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;

/**
 * @author Brett
 * @date Jun. 22, 2020
 */

public class UIButton extends UITexture implements IMouseState {

	public ButtonEvent event;
	
	public UIButton(int t1, int t2, int t3, float x, float y, float w, float h) {
		super(t1, t2, t3, x, y, w, h);
		InputMaster.mouse.add(this);
	}
	
	public UIButton setEvent(ButtonEvent e) {
		event = e;
		return this;
	}

	@Override
	public void update() {
		super.update();
		if (isButtonSelected()) {
			
		} else
			t2 = -1;
	}

	public boolean isButtonSelected() {
		float mx = (float) DisplayManager.mouseX;
		float my = (float) DisplayManager.mouseY;
		if (mx > x && mx < (x + width)) {
			if (my > y && my < (y + height)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		super.destroy();
		for (int i = 0; i < InputMaster.mouse.size(); i++) {
			if (InputMaster.mouse.get(i) == this) {
				InputMaster.mouse.remove(i);
				return;
			}
		}
	}

	@Override
	public void onMousePressed(int button) {
		
	}

	@Override
	public void onMouseReleased(int button) {
		if (isButtonSelected())
			event.event("button");
	}

}
