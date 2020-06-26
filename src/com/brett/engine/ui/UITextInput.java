package com.brett.engine.ui;

import com.brett.engine.ui.font.UIText;

/**
 * @author Brett
 * @date Jun. 25, 2020
 */

public class UITextInput extends UIButton {

	public UIText text;
	public String textBuffer;
	
	public UITextInput(int t1, int ht, float x, float y, float w, float h) {
		super(t1, ht, x, y, w, h);
	}
	
	public UITextInput(int t1, int ht, float x, float y, float w, float h, AnchorPoint ah) {
		super(t1, ht, x, y, w, h, ah);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void onMousePressed(int button) {

	}

}
