package com.brett.renderer.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author brett
 * just a standard shader for rendering UIs
 * everything kinda just explains itself.
 * refer to the shader program class for help.
 */
public class GUIShader extends ShaderProgram{
    
    private static final String VERTEX_FILE = "guiVertexShader.vert";
    private static final String FRAGMENT_FILE = "guiFragmentShader.frag";
     
    private int location_transformationMatrix;
    private int location_color;
    private int location_using_textures;
    private int location_textureScaleX;
    private int location_textureScaleY;
    private int location_texture1;
    private int location_texture2;
    private int location_texture3;
 
    public GUIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    
    public void loadColor(Vector3f color) {
    	super.loadVector(location_color, color);
    }
    
    public void connectTextureUnits() {
		super.loadInt(location_texture1, 0);
		super.loadInt(location_texture2, 1);
		super.loadInt(location_texture3, 2);
	}
    
    public void loadTextureAmount(int amount) {
    	super.loadInt(location_using_textures, amount);
    }
    
    public void loadTextureScale(float scaleX, float scaleY) {
    	super.loadFloat(location_textureScaleX, scaleX);
    	super.loadFloat(location_textureScaleY, scaleY);
    }
    
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_color = super.getUniformLocation("color");
        location_using_textures = super.getUniformLocation("using_textures");
        location_textureScaleX = super.getUniformLocation("textureScaleX");
        location_textureScaleY = super.getUniformLocation("textureScaleY");
        location_texture1 = super.getUniformLocation("guiTexture");
        location_texture2 = super.getUniformLocation("guiTexture2");
        location_texture3 = super.getUniformLocation("guiTexture3");
    }
  
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}
