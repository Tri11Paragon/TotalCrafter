package com.brett.world;

import java.util.HashMap;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class GameRegistry {
	
	public static HashMap<Integer, String> textures = new HashMap<Integer, String>();
	public static HashMap<String, Integer> textureIDs = new HashMap<String, Integer>();
	
	public static HashMap<Integer, String> registerTextures() {
		registerTexture(0, "stone");
		registerTexture(1, "dirt");
		return textures;
	}
	
	public static void registerTexture(int id, String texture) {
		textures.put(id, texture);
		textureIDs.put(texture, id);
	}
	
}
