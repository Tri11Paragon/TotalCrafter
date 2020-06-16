package com.brett.renderer.font;

import java.io.File;
import java.io.Serializable;

/**
 * 
 * font data type
 * 
 */
public class FontType implements Serializable {

	private static final long serialVersionUID = 4910588667907555697L;
	private int textureAtlas;
	private transient TextMeshCreator loader;

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
