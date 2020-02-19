package com.brett.renderer.particles;

public class ParticleTexture {

	private int textureID;
	private int numberOfRows;
	private boolean useAdditive = false;
	
	public ParticleTexture(int textureID, int numberOfRows) {
		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
	}
	
	public ParticleTexture(int textureID, int numberOfRows, boolean useAdditive) {
		this.textureID = textureID;
		this.numberOfRows = numberOfRows;
		this.useAdditive = useAdditive;
	}

	public int getTextureID() {
		return textureID;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}
	
	public ParticleTexture useAdditiveBlending() {
		this.useAdditive = true;
		return this;
	}
	
	public ParticleTexture useSourceBlending() {
		this.useAdditive = false;
		return this;
	}
	
	public boolean getUsingAdditive() {
		return useAdditive;
	}
	
}
