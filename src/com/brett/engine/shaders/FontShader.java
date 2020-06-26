package com.brett.engine.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author brett pretty standard shader please refer to the shader program for
 *         information about the functions
 */
public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "fontVertex.vert";
	private static final String FRAGMENT_FILE = "fontFragment.frag";

	private int location_color;
	private int location_outline;
	private int location_projectionMatrix;
	private int location_translationMatrix;
	private int location_minpos;
	private int location_maxpos;
	private int location_screenHeight;

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_outline = super.getUniformLocation("outlineColor");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_translationMatrix = super.getUniformLocation("transformationMatrix");
		location_minpos = super.getUniformLocation("minpos");
		location_maxpos = super.getUniformLocation("maxpos");
		location_screenHeight = super.getUniformLocation("screenHeight");
	}

	public void loadPos(float minx, float miny, float maxx, float maxy) {
    	super.load2DVector(location_minpos, minx, miny);
    	super.load2DVector(location_maxpos, maxx, maxy);
    }
    
    public void loadScreenHeight(float height) {
    	super.loadFloat(location_screenHeight, height);
    }
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	public void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}

	public void loadColorOutline(Vector3f color) {
		super.loadVector(location_outline, color);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadTranslationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_translationMatrix, matrix);
	}
}
