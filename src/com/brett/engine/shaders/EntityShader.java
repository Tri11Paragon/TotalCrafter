package com.brett.engine.shaders;

/**
* @author Brett
* @date Jun. 26, 2020
*/

public class EntityShader extends WorldShader {

	public EntityShader() {
		super("entity.vert", "entity.frag");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "uvs");
		bindAttribute(2, "normals");
	}

}
