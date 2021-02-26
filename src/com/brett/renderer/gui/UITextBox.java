package com.brett.renderer.gui;

import java.io.Serializable;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.brett.tools.IKeyState;
import com.brett.tools.InputMaster;
import com.brett.tools.SettingsLoader;

/**
 *
 * @author brett
 * @date May 30, 2020 allows for text input.
 */

public class UITextBox extends UIButton implements UIElement, Serializable, IKeyState {

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

	public UITextBox(int texture, UIControl event, int maxLength, float x, float y, float width, float height) {
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
		if (InputMaster.mouseDown[0]) {
			if (isButtonSelected()) {
				isSelected = true;
			} else {
				isSelected = false;
			}
		}
	}

	@Override
	public void onKeyPressed(int keys) {
	}

	@Override
	public void onKeyReleased(int keys) {
		if (isSelected) {
			char c = (char) keys;
			// make sure we don't add if we are pressing the console open key.
			if (keys == SettingsLoader.KEY_CONSOLE)
				return;
			// this is backspace. we use it to remove text.
			if (keys == GLFW.GLFW_KEY_BACKSPACE) {
				if (inputTextBuffer.length() > 1)
					inputTextBuffer = inputTextBuffer.substring(0, inputTextBuffer.length() - 1);
			// enters the command when enter is pressed.
			} else if (c == 10 || c == 13 || keys == GLFW.GLFW_KEY_ENTER) {
				isSelected = false;
			// we don't want to write anything if we are pressing a key that isn't a character.
			} else if (c < 32) {
			// add in the character to the input line
			} else {
				char[] dhr = (c + "").replaceAll("[^a-zA-Z0-9\\- ]", "").toCharArray();
				if (dhr == null || dhr.length == 0)
					return;
				char cd = dhr[0];
				if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT] || InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT_SHIFT])
					inputTextBuffer += cd;
				else
					inputTextBuffer += Character.toLowerCase(cd);
			}
			if (event != null)
				event.event(inputTextBuffer);
		}
	}

}
