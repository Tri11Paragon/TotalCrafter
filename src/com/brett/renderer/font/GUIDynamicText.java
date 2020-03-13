package com.brett.renderer.font;

import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.font.fontRendering.TextMaster;

/**
*
* @author brett
*
*/

public class GUIDynamicText extends GUIText {
	
	private boolean enabled = false;
	
	public GUIDynamicText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
		super(text, fontSize, font, position, maxLineLength, centered);
	}
	
	public GUIDynamicText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered, int numOflines) {
		super(text, fontSize, font, position, maxLineLength, centered);
	}
	
	/**
	 * use this if the text is known to be being drawn on the screen
	 * and you are using static TextMaster rendering
	 * @param text
	 */
	public void changeText(String text) {
		if (enabled)
			TextMaster.removeText(this);
		super.textString = text;
		if (enabled)
			TextMaster.loadText(this);
	}
	
	public void changeTextNoUpdate(String text) {
		super.textString = text;
	}
	
	public void disableText() {
		enabled = false;
		TextMaster.removeText(this);
	}
	
	public void enableText() {
		enabled = true;
		TextMaster.loadText(this);
	}
}
