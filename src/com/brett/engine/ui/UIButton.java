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
	public int ht;
	
	public UIButton(int t1, int ht, float x, float y, float w, float h) {
		super(t1, -1, -1, x, y, w, h);
		this.ht = ht;
		InputMaster.mouse.add(this);
	}
	
	public UIButton(int t1, int ht, float x, float y, float w, float h, AnchorPoint an) {
		super(t1, -1, -1, x, y, w, h, an);
		this.ht = ht;
		InputMaster.mouse.add(this);
	}
	
	public UIButton setEvent(ButtonEvent e) {
		event = e;
		return this;
	}

	@Override
	public void update() {
		super.update();
		if (isButtonSelected())
			t2 = ht;
		else
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
			if (event != null)
				event.event("button");
	}

}
