package com.brett.engine.ui.font;

/**
 * text mesh data holder
 *
 */
public class TextMeshData {
	
	private float[] vertexPositions;
	private float[] textureCoords;
	private float x_size;
	private float y_size;
	public double totalWidth, maxHeight;
	
	protected TextMeshData(float[] vertexPositions, float[] textureCoords, double totalWidth, double maxHeight){
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
		this.totalWidth = totalWidth;
		this.maxHeight = maxHeight;
	}

	public float[] getVertexPositions() {
		return vertexPositions;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public int getVertexCount() {
		return vertexPositions.length/2;
	}

	public float getX_size() {
		return x_size;
	}

	public void setX_size(float x_size) {
		this.x_size = x_size;
	}

	public float getY_size() {
		return y_size;
	}

	public void setY_size(float y_size) {
		this.y_size = y_size;
	}

}
