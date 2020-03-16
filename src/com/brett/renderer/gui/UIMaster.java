package com.brett.renderer.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.GUITexture;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.GUIDynamicText;

/**
*
* @author brett
* Passing vectors are in screen space
* float x & y is for pixel size
*/

public class UIMaster {
	
	private int SWIDTH = 800;
	private int SHEIGHT = 600;
	private GUIRenderer renderer;
	private List<UIElement> guitextures = new ArrayList<UIElement>();
	private List<IMenu> guimenus = new ArrayList<IMenu>();
	private GUITexture tmpText = new GUITexture(0, new Vector2f(0,0), new Vector2f(0,0));
	
	public UIMaster(Loader loader) {
		SWIDTH = Display.getWidth();
		SHEIGHT = Display.getHeight();
		renderer = new GUIRenderer(loader);
		guimenus.add(new EscapeMenu(this, loader));
	}
	
	public void render() {
		renderer.render(guitextures);
		for (int i = 0; i < guimenus.size(); i++) {
			IMenu m = guimenus.get(i);
			renderer.render(m.render(this));
			m.update();
			renderer.render(m.secondardRender(this));
		}
	}
	
	/**
	 * Do not use unless the texture needs to move
	 * (floats are in pixels)
	 */
	public void drender(float x, float y, float width, float height) {
		renderer.startrender();
		renderer.render(tmpText, x, y, width, height);
		renderer.stoprender();
	}
	
	public GUITexture addUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height));
		guitextures.add(t);
		return t;
	}
	
	public GUITexture addUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height)).setColor(color);
		guitextures.add(t);
		return t;
	}
	
	public UIButton addUIButton(int texture, int hovertexture, UIControl event, float x, float y, float width, float height) {
		UIButton b = new UIButton(texture, hovertexture, event, this, x, y, width, height);
		guitextures.add(b);
		return b;
	}
	
	public GUITexture addCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height));
		guitextures.add(t);
		return t;
	}
	
	public GUITexture addCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height)).setColor(color);
		guitextures.add(t);
		return t;
	}
	
	public GUITexture createUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height));
		return t;
	}
	
	public GUITexture createUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height)).setColor(color);
		return t;
	}
	
	public GUITexture createUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height, Vector3f color, float textureScaleX, float textureScaleY) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height), textureScaleX, textureScaleY).setColor(color);
		return t;
	}
	
	public GUITexture createUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height, float textureScaleX, float textureScaleY) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height), textureScaleX, textureScaleY);
		return t;
	}
	
	public UIButton createUIButton(int texture, int hovertexture, UIControl event, float x, float y, float width, float height) {
		UIButton b = new UIButton(texture, hovertexture, event, this, x, y, width, height);
		return b;
	}
	
	public GUITexture createCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height));
		return t;
	}
	
	public GUITexture createCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height)).setColor(color);
		return t;
	}
	
	public GUIDynamicText createDynamicText(String text, float size, FontType type, float x, float y, float maxWidth, boolean centered) {
		return new GUIDynamicText(text, size, type, calcVec(x, y), maxWidth/this.SWIDTH, centered);
	}
	
	public Vector2f calcVec(float x, float y) {
		return new Vector2f(x / SWIDTH, y / SHEIGHT);
	}
	
	public void addMenu(IMenu menu) {
		this.guimenus.add(menu);
	}
	
	public GUIRenderer getRenderer() {
		return renderer;
	}
	
	public void cleanup() {
		renderer.cleanup();
	}
	
}
