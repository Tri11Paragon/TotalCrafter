package com.brett.engine.ui;

import org.joml.Vector3f;

import com.brett.engine.data.IMouseState;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.ui.font.UIText;

/**
 * @author Brett
 * @date Jun. 22, 2020
 */

public class UIButton extends UITexture implements IMouseState {

	public ButtonEvent event;
	public int ht;
	public UIText text; 
	public Vector3f color = new Vector3f(0f, 0f, 0f);
	public Vector3f outlineColor = new Vector3f(1f, 1f, 1f);
	public Vector3f selcolor = new Vector3f(0f, 0f, 0f);
	public Vector3f seloutlineColor = new Vector3f(1f, 0f, 0f);
	
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
	
	public UIButton setText(UIText t) {
		this.text = t;
		return this;
	}

	@Override
	public void update() {
		super.update();
		if (isButtonSelected()) {
			t2 = ht;
			if (this.text != null) {
				this.text.outlineColor = seloutlineColor;
				this.text.color = selcolor;
			}
		} else {
			t2 = -1;
			if (this.text != null) {
				this.text.outlineColor = outlineColor;
				this.text.color = color;
			}
		}
	}

	public boolean isButtonSelected() {
		return isButtonSelected(0, 0);
	}
	
	public boolean isButtonSelected(float boundx, float boundy) {
		float mx = (float) DisplayManager.mouseX;
		float my = (float) DisplayManager.mouseY;
		if (mx > (x-boundx) && mx < (x + width + boundx)) {
			if (my > (y-boundy) && my < (y + height + boundy)) {
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
		if (isButtonSelected())
			if (event != null)
				event.event("button");
	}

	@Override
	public void onMouseReleased(int button) {
		
	}

}
