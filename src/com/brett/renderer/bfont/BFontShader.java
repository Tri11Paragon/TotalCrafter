package com.brett.renderer.bfont;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.shaders.ShaderProgram;

/**
*
* @author brett
*
*/

public class BFontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bFontVertex.txt";
	private static final String FRAGMENT_FILE = "bFontFragment.txt";
	
	private int location_transformationMatrix;
    private int location_projectionMatrix;
	private int location_color;
    
	public BFontShader(String vertexFile, String fragmentFile) {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_color = super.getUniformLocation("color");
    }
    
    public void loadColor(Vector3f color) {
    	super.loadVector(location_color, color);
    }
    
    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
    	super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
	
}
