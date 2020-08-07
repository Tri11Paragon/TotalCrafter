package com.brett.engine.data.datatypes;

import org.joml.Vector4f;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class Material {
	
	public static Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	
	private Vector4f ambient, diffuse, specular;
	private int texture = -1, normalmap = -1;
	
	public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, int texture) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.texture = texture;
	}

	public Vector4f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector4f ambient) {
		this.ambient = ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public void setDiffuse(Vector4f diffuse) {
		this.diffuse = diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}

	public void setSpecular(Vector4f specular) {
		this.specular = specular;
	}

	public int getTexture() {
		return texture;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public int getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(int normalmap) {
		this.normalmap = normalmap;
	}
	
}
