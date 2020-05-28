package com.brett.renderer.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import com.brett.renderer.lighting.Light;
import com.brett.tools.Maths;
import com.brett.world.cameras.Camera;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "waterVertex.vert";
    private final static String FRAGMENT_FILE = "waterFragment.frag";
 
    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_moveFactor;
    private int location_cameraPostion;
    private int location_normalMap;
    private int location_lightColor;
    private int location_lightPosition;
    private int location_depthMap;
 
    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
 
    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }
 
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        location_reflectionTexture = getUniformLocation("reflectionTexture");
        location_refractionTexture = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_moveFactor = getUniformLocation("moveFactor");
        location_cameraPostion = getUniformLocation("cameraPosition");
        location_normalMap = getUniformLocation("normalMap");
        location_lightColor = getUniformLocation("lightColor");
        location_lightPosition = getUniformLocation("lightPosition");
        location_depthMap = getUniformLocation("depthMap");
    }
    
    public void conenctTextures() {
    	super.loadInt(location_reflectionTexture, 0);
    	super.loadInt(location_refractionTexture, 1);
    	super.loadInt(location_dudvMap, 2);
    	super.loadInt(location_normalMap, 3);
    	super.loadInt(location_depthMap, 4);
    }
 
    public void loadLights(Light sun) {
    	super.loadVector(location_lightPosition, sun.getPosition());
    	super.loadVector(location_lightColor, sun.getColor());
    }
    
    public void loadMoveFactor(Vector2f vec) {
    	super.load2DVector(location_moveFactor, vec);
    }
    
    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(location_projectionMatrix, projection);
    }
     
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPostion, camera.getPosition());
    }
 
    public void loadModelMatrix(Matrix4f modelMatrix){
        loadMatrix(location_modelMatrix, modelMatrix);
    }
	
}
