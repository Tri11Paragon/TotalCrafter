package com.brett.renderer.shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.renderer.lighting.Light;
import com.brett.renderer.shaders.ShaderProgram;
import com.brett.tools.Maths;
import com.brett.world.cameras.Camera;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "yes/vertexShader.txt";
	private static final String FRAGMENT_FILE = "yes/fragmentShader.txt";
	private static final int MAX_LIGHTS = 4;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightAttenuation[];
	private int location_lightColour[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_Color3f;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_fogGradiant;
	private int location_fogDensity;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_modelTexture;
	private int location_specularMap;
	private int location_usesSpecularMap;
	private int location_shadowMap;
	private int location_shadowDistance;
	private int location_shadowMapSize;
	private int location_toShadowMapSpace;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_lightAttenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_lightAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		
		location_fogGradiant = super.getUniformLocation("gradient");
		location_fogDensity = super.getUniformLocation("density");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		
		location_plane = super.getUniformLocation("plane");
		location_specularMap = super.getUniformLocation("specularMap");
		location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
		location_modelTexture = super.getUniformLocation("modelTexture");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_shadowMapSize = super.getUniformLocation("shadowMapSize");
		location_shadowDistance = super.getUniformLocation("shadowDistance");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_specularMap, 1);
		super.loadInt(location_shadowMap, 5);
	}
	
	public void loadShadowInfo(float dis, float mapSize) {
		super.loadFloat(location_shadowDistance, dis);
		super.loadFloat(location_shadowMapSize, mapSize);
	}
	
	public void loadUseSpecularMap(boolean useMap) {
		super.loadBoolean(location_usesSpecularMap, useMap);
	}
	
	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void loadOffset(Vector2f offset) {
		super.load2DVector(location_offset, offset);
	}
	
	public void loadFakeLighting(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.load4DVector(location_plane, plane);
	}
	
	public void loadColorVector3f(Vector3f vec) {
		super.loadVector(location_Color3f, vec);
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void loadFogInfo(float density, float gradiant) {
		super.loadFloat(location_fogDensity, density);
		super.loadFloat(location_fogGradiant, gradiant);
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadLight(List<Light> light) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < light.size()) {
				super.loadVector(location_lightPosition[i], light.get(i).getPosition());
				super.loadVector(location_lightColour[i], light.get(i).getColor());
				super.loadVector(location_lightAttenuation[i], light.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightAttenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	// TODO: test if creating new view matrix each frame is casuing lag.
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
