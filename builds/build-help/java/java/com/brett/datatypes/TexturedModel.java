package com.brett.datatypes;

/**
 * @author brett
 * Stores the VAO and Texture of a model
 * created a textured model!
 */
public class TexturedModel {

	private ModelVAO rawModel;
	private Texture texture;

	
	public TexturedModel(ModelVAO model, Texture texture){
		this.rawModel = model;
		this.texture = texture;
	}
	
	public void setRawModel(ModelVAO model) {
		this.rawModel = model;
	}
	
	public ModelVAO getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}
	
	
}
