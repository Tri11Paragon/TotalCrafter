package com.brett.datatypes;

/**
 * @author brett
 * Stores the texture id for a texture object
 * this used to have a lot of stuff, but I removed it as it wasn't useful.
 */
public class Texture {

	// texture ids
	private int textureID;
	
	public Texture(int texture){
		this.textureID = texture;
	}

	public int getID(){
		return textureID;
	}

}
