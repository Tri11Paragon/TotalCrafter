package com.brett.renderer.gui;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.datatypes.GUITexture;
import com.brett.renderer.Loader;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.UIDynamicText;

/**
*
* @author brett
* Passing vectors are in screen space
* float x & y is for pixel size
*/

public class UIMaster {
	
	public static EscapeMenu menu;
	
	public static int inventoryTexture = 0;
	
	private GUIRenderer renderer;
	// static textures
	private List<UIElement> guitextures = new ArrayList<UIElement>();
	// menus
	private List<IMenu> guimenus = new ArrayList<IMenu>();
	
	public UIMaster(Loader loader) {
		// create the rednerer
		renderer = new GUIRenderer(loader);
		menu = new EscapeMenu(this, loader);
		guimenus.add(menu);
		// load textures we will need.
		inventoryTexture = loader.loadSpecialTexture("gui/background");
	}
	
	public void render() {
		// renders all the menus
		for (int i = 0; i < guimenus.size(); i++) {
			// get the menu
			IMenu m = guimenus.get(i);
			// render the first layer
			renderer.render(m.render(this));
			// update it
			m.update();
			// render the second layer.
			renderer.render(m.secondardRender(this));
		}
		// render all static textures.
		renderer.render(guitextures);
	}
	
	/*
	 * this is a bad way of doing things but its how we are doing things today.
	 * I know I go on about optimization, and yes this (and the whole UI system) goes against my stance on optimization.
	 * Since the UI system is really only used for a small part of the game and doesn't add much load to the system 
	 * (compared to the chunks, which use lots of ram, cpu and gpu). Its on my TODO list to make this better. 
	 */
	
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
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((renderer.SWIDTH/2 - width/2) + xoff, (renderer.SHEIGHT/2 - height/2) + yoff), calcVec(width, height));
		guitextures.add(t);
		return t;
	}
	
	public GUITexture addCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((renderer.SWIDTH/2 - width/2) + xoff, (renderer.SHEIGHT/2 - height/2) + yoff), calcVec(width, height)).setColor(color);
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
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((renderer.SWIDTH/2 - width/2) + xoff, (renderer.SHEIGHT/2 - height/2) + yoff), calcVec(width, height));
		return t;
	}
	
	public GUITexture createCenteredTexture(int texture, int texture2, int texture3, float xoff, float yoff, float width, float height, Vector3f color) {
		GUITexture t = new GUITexture(texture, texture2, texture3, calcVec((renderer.SWIDTH/2 - width/2) + xoff, (renderer.SHEIGHT/2 - height/2) + yoff), calcVec(width, height)).setColor(color);
		return t;
	}
	
	public UIDynamicText createDynamicText(String text, float size, FontType type, float x, float y, float maxWidth, boolean centered) {
		return new UIDynamicText(text, size, type, calcVec(x, y), maxWidth/renderer.SWIDTH, centered);
	}
	
	public UIDynamicText createDynamicText(String text, float sizeX, float sizeY, FontType type, float x, float y, float maxWidth, boolean centered) {
		return new UIDynamicText(text, sizeX, sizeY, type, calcVec(x, y), maxWidth/renderer.SWIDTH, centered, 1);
	}
	
	public UIDynamicText createDynamicText(String text, float sizeX, float sizeY, FontType type, float x, float y, float maxWidth, boolean centered, int maxNumberOfLines) {
		return new UIDynamicText(text, sizeX, sizeY, type, calcVec(x, y), maxWidth/renderer.SWIDTH, centered, maxNumberOfLines);
	}
	
	public Vector2f calcVec(float x, float y) {
		return new Vector2f(x / renderer.SWIDTH, y / renderer.SHEIGHT);
	}
	
	public void addMenu(IMenu menu) {
		this.guimenus.add(menu);
	}
	
	public void removeMenu(IMenu menu) {
		this.guimenus.remove(menu);
	}
	
	public GUIRenderer getRenderer() {
		return renderer;
	}
	
	public void cleanup() {
		renderer.cleanup();
	}
	
}
