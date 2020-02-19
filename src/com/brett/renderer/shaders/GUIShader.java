package com.brett.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class GUIShader extends ShaderProgram{
    
    private static final String VERTEX_FILE = "guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "guiFragmentShader.txt";
     
    private int location_transformationMatrix;
    private int location_color;
 
    public GUIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    
    public void loadColor(Vector3f color) {
    	super.loadVector(location_color, color);
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_color = super.getUniformLocation("color");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}
