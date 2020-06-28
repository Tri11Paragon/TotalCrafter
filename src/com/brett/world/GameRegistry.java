package com.brett.world;

import java.util.HashMap;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class GameRegistry {
	
	public static HashMap<Integer, String> blockTextures = new HashMap<Integer, String>();
	public static HashMap<String, Integer> blockTextureIDs = new HashMap<String, Integer>();
	
	public static HashMap<Integer, String> registerTextures() {
		registerTexture(0, "stone");
		registerTexture(1, "dirt");
		return blockTextures;
	}
	
	public static void registerTexture(int id, String texture) {
		blockTextures.put(id, texture);
		blockTextureIDs.put(texture, id);
	}
	
}
