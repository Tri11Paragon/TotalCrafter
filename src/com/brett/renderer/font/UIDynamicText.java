package com.brett.renderer.font;

import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.font.fontRendering.StaticText;

/**
*
* @author brett
* just a UIText that can be <b>easily</b> changed.
* 
*/

public class UIDynamicText extends UIText {
	
	private boolean enabled = false;
	
	public UIDynamicText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
		super(text, fontSize, font, position, maxLineLength, centered);
	}
	
	public UIDynamicText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered, int numOflines) {
		super(text, fontSize, font, position, maxLineLength, centered);
	}
	
	public UIDynamicText(String text, float fontSizeX, float fontSizeY, FontType font, Vector2f position, float maxLineLength, boolean centered, int maxNumberOfLines) {
		super(text, fontSizeX, fontSizeY, font, position, maxLineLength, centered, maxNumberOfLines);
		//TextMaster.loadText(this);
	}
	
	public void setPosition(Vector2f pos) {
		this.position = pos;
	}
	
	/**
	 * use this if the text is known to be being drawn on the screen
	 * and you are using static TextMaster rendering
	 * @param text
	 */
	public void changeText(String text) {
		if (enabled)
			StaticText.removeText(this);
		super.textString = text;
		if (enabled)
			StaticText.loadText(this);
	}
	
	public void changeTextNoUpdate(String text) {
		super.textString = text;
	}
	
	public void disableText() {
		enabled = false;
		StaticText.removeText(this);
	}
	
	public void enableText() {
		enabled = true;
		StaticText.loadText(this);
	}
	
	public boolean getEnabled() {
		return enabled;
	}
}
