package com.brett.renderer.datatypes;

import com.brett.renderer.Loader;
import com.brett.tools.obj.OBJLoader;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;

	
	public TexturedModel(RawModel model, ModelTexture texture){
		this.rawModel = model;
		this.texture = texture;
	}
	
	public void setRawModel(RawModel model) {
		this.rawModel = model;
	}
	
	public RawModel getRawModel() {
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
