package com.brett.renderer.datatypes;

import com.brett.renderer.Loader;
import com.brett.tools.obj.OBJLoader;

public class TexturedModel {

	private ModelVAO rawModel;
	private ModelTexture texture;

	
	public TexturedModel(ModelVAO model, ModelTexture texture){
		this.rawModel = model;
		this.texture = texture;
	}
	
	public void setRawModel(ModelVAO model) {
		this.rawModel = model;
	}
	
	public ModelVAO getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	public static TexturedModel createTexturedModel(Loader loader, String modelname, String texturename) {
		return new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ(modelname)), new ModelTexture(loader.loadTexture(texturename)));
	}
	
	public static TexturedModel createTexturedModel(Loader loader, String modelname, String texturename, float shineDamper, float reflectivity) {
		return new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ(modelname)), new ModelTexture(loader.loadTexture(texturename)).setReflectivity(reflectivity).setShineDamper(shineDamper));
	}
	
}
