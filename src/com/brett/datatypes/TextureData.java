package com.brett.datatypes;

import java.nio.ByteBuffer;

/**
 * @author brett
 * holds data loaded from a texture file
 */
public class TextureData {
	
	private int width;
	private int height;
	// byte buffer containing all the bytes of an image.
	private ByteBuffer buffer;
	
	public TextureData(ByteBuffer buffer, int width, int height){
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public ByteBuffer getBuffer(){
		return buffer;
	}

}