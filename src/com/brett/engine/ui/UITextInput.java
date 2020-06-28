package com.brett.engine.ui;

import org.lwjgl.glfw.GLFW;

import com.brett.engine.Utils;
import com.brett.engine.data.IKeyState;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.ui.font.UIText;

/**
 * @author Brett
 * @date Jun. 25, 2020
 */

public class UITextInput extends UIButton implements IKeyState {

	public UIText text;
	public String textBuffer = "";
	public boolean selected = false;
	public int maxCharacters = 32;

	public UITextInput(int t1, UIText text, int maxCharacters, float x, float y, float w, float h) {
		super(t1, -1, x, y, w, h);
		this.text = text;
		this.maxCharacters = maxCharacters;
		InputMaster.keyboard.add(this);
	}

	public UITextInput(int t1, UIText text, int maxCharacters, float x, float y, float w, float h, AnchorPoint ah) {
		super(t1, -1, x, y, w, h, ah);
		this.maxCharacters = maxCharacters;
		this.text = text;
	}
	
	@Override
	public UITextInput setEvent(ButtonEvent e) {
		event = e;
		return this;
	}

	private long startTime = 0;
	private long lastKeyTime = 0;
	
	@Override
	public void update() {
		char[] texs = textBuffer.toCharArray();
		if (selected) {
			if (texs.length == 0) {
				textBuffer = "_";
				text.changeText(textBuffer);
				return;
			}
			if (texs[texs.length-1] != '_') {
				textBuffer += '_';
				text.changeText(textBuffer);
			}
		} else {
			if (texs.length == 0)
				return;
			if (texs[texs.length-1] == '_') {
				textBuffer = textBuffer.substring(0, texs.length-1);
				text.changeText(textBuffer);
			}
		}
		if (InputMaster.keyDown[GLFW.GLFW_KEY_BACKSPACE]) {
			if (System.currentTimeMillis() - startTime > 800) {
				if (System.currentTimeMillis() - lastKeyTime > 40) {
					if (textBuffer.length() < 2)
						return;
					textBuffer = textBuffer.substring(0, textBuffer.length()-2);
					lastKeyTime = System.currentTimeMillis();
					text.changeText(textBuffer);
					if (event != null) {
						if (event.event(getTextNoSel())) {
							textBuffer = "_";
							text.changeText(textBuffer);
						}
					}
				}
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		InputMaster.keyboard.remove(this);
	}

	@Override
	public void onMousePressed(int button) {
		if (isButtonSelected())
			selected = true;
		else
			selected = false;
	}

	@Override
	public void onKeyPressed(int keys) {
		try {
			if (selected) {
				startTime = System.currentTimeMillis();
				if (keys == GLFW.GLFW_KEY_BACKSPACE) {
					startTime = System.currentTimeMillis();
					if (textBuffer.length() < 2)
						return;
					if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
						textBuffer = textBuffer.substring(0, textBuffer.length()-1);
					textBuffer = textBuffer.substring(0, textBuffer.length()-1);
					text.changeText(textBuffer);
					return;
				}
				if (textBuffer.length() >= maxCharacters)
					return;
				if (keys == GLFW.GLFW_KEY_ENTER) {
					if (event != null) {
						if (event.event(getTextNoSel())) {
							textBuffer = "_";
							text.changeText(textBuffer);
						}
					}
					return;
				}
				if (keys < 30 || keys > 100)
					return;
				for (int i = 0; i < Utils.illegalCharacters.size(); i++){
					if (keys == Utils.illegalCharacters.get(i))
						return;
				}
				if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
					textBuffer = textBuffer.substring(0, textBuffer.length()-1);
				if (keys >= 65 && keys <= 90) {
					if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT] || InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT_SHIFT]) 
						textBuffer+=(char)keys;
					else
						textBuffer+=(char)(keys+32);
				} else 
					textBuffer+=(char)keys;
				text.changeText(textBuffer);
				if (event != null) {
					if (event.event(getTextNoSel())) {
						textBuffer = "_";
						text.changeText(textBuffer);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error in the UI Text Input!");
			e.printStackTrace();
		}
	}
	
	public String getTextNoSel() {
		if (textBuffer.length() == 0)
			return textBuffer;
		if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
			return textBuffer.substring(0, textBuffer.length()-1);
		return textBuffer;
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_BACKSPACE) {
			startTime = 0;
		}
	}

}
