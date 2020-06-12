package com.brett.renderer.font;

import java.io.File;

/**
 * 
 * font data type
 * 
 */
public class FontType {

	private int textureAtlas;
	private TextMeshCreator loader;

	public FontType(int textureAtlas, File fontFile) {
		this.textureAtlas = textureAtlas;
		this.loader = new TextMeshCreator(fontFile);
	}

	public int getTextureAtlas() {
		return textureAtlas;
	}

	public TextMeshData loadText(UIText text) {
		return loader.createTextMesh(text);
	}

}
