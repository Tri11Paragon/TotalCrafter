package com.brett.renderer.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.GUITexture;

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
			renderer.render(guimenus.get(i).render());
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
		return new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height));
	}
	
	public GUITexture addUITexture(int texture, int texture2, int texture3, float x, float y, float width, float height, Vector3f color) {
		return new GUITexture(texture, texture2, texture3, calcVec(x, y), calcVec(width, height)).setColor(color);
	}
	
	public UIButton addUIButton(int texture, int hovertexture, UIControl event, float x, float y, float width, float height) {
		return new UIButton(texture, hovertexture, event, this, x, y, width, height);
	}
	
	public GUITexture addCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height) {
		return new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height));
	}
	
	public GUITexture addCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height, Vector3f color) {
		return new GUITexture(texture, texture2, texture3, calcVec((this.SWIDTH/2 - width/2) + xoff, (this.SHEIGHT/2 - height/2) + yoff), calcVec(width, height)).setColor(color);
	}
	
	public Vector2f calcVec(float x, float y) {
		return new Vector2f(x / SWIDTH, y / SHEIGHT);
	}
	
	public Vector2f calcVec(Vector2f vec, float x, float y) {
		vec.x = x;
		vec.y = y;
		return vec;
	}
	
	public GUIRenderer getRenderer() {
		return renderer;
	}
	
	public void cleanup() {
		renderer.cleanup();
	}
	
}
