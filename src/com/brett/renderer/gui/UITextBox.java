package com.brett.renderer.gui;

import java.io.Serializable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.KeyMaster;
import com.brett.tools.SettingsLoader;

/**
*
* @author brett
* @date May 30, 2020
* allows for text input.
*/

public class UITextBox extends UIButton implements UIElement,Serializable {

	private static final long serialVersionUID = 6980355171144122220L;

	protected boolean isSelected = false;
	
	public String inputTextBuffer = "";
	public int maxLength = 100000;
	
	public UITextBox(int texture, UIControl event, UIMaster master, float x, float y, float width, float height) {
		super(texture, -1, master.calcVec(x, y), master.calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.event = event;
	}
	
	public UITextBox(int texture, int texture2, Vector2f position, Vector2f scale) {
		super(texture, texture2, position, scale);
	}
	
	public UITextBox(int texture,  UIControl event, int maxLength, float x, float y, float width, float height) {
		super(texture, -1, calcVec(x, y), calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
		this.event = event;
		this.maxLength = maxLength;
	}
	
	public UITextBox(int texture, float x, float y, float width, float height) {
		super(texture, -1, calcVec(x, y), calcVec(width, height));
		this.px = x;
		this.py = y;
		this.pw = width;
		this.ph = height;
	}
	
	@Override
	public void update() {
		// Determine if we are selected
		if (Mouse.isButtonDown(0)) {
			if (isButtonSelected()) {
				isSelected = true;
			} else {
				isSelected = false;
			}
		}
		if (isSelected) {
			if(Keyboard.getEventKeyState() && KeyMaster.state) {
				// this is the same idea as the console
				// actually i just copied the code from the console class
				// the idea is if we are selected and the user has pused a button
				// then update the text with what was pressed.
				char c = Keyboard.getEventCharacter();
				if (c == SettingsLoader.KEY_CONSOLE)
					return;
				if (c == 8) {
					if (inputTextBuffer.length() > 0)
						inputTextBuffer = inputTextBuffer.substring(0, inputTextBuffer.length() - 1);
				} else if (inputTextBuffer.length() >= maxLength) {
					return;
				} else if (c == 10 || c == 13) {
					isSelected = false;
				} else if (c < 32) {
					return;
				} else
					inputTextBuffer += c;
				if (event != null)
					event.event(inputTextBuffer);
			}
		}
	}
	
}
