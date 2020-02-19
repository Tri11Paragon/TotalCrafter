package com.brett.renderer.datatypes;

public class ModelTexture {

	private int textureID;
	private int normalMap;
	private int specularMap;

	private float shineDamper = 10;
	private float reflectivity = 0;
	
	private boolean hasTransparentcy = false;
	private boolean useFakeLighting = false;
	private boolean hasSpecularMap = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int texture){
		this.textureID = texture;
	}
	
	public ModelTexture(int texture, int normalTexture, float shineDamper, float reflectivity){
		this.textureID = texture;
		this.normalMap = normalTexture;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}
	public ModelTexture(int texture, float shineDamper, float reflectivity){
		this.textureID = texture;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}
	
	public ModelTexture(int texture, int normalTexture, int specTexture, float shineDamper, float reflectivity){
		this.textureID = texture;
		this.normalMap = normalTexture;
		this.specularMap = specTexture;
		this.hasSpecularMap = true;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}
	
	public ModelTexture(int texture, int specTexture, float reflectivity){
		this.textureID = texture;
		this.specularMap = specTexture;
		this.hasSpecularMap = true;
		this.reflectivity = reflectivity;
	}
	
	public ModelTexture setSpecularMap(int specMap) {
		this.specularMap = specMap;
		this.hasSpecularMap = true;
		return this;
	}
	
	public boolean hasSpecularMap() {
		return hasSpecularMap;
	}
	
	public int getSpecularMap() {
		return specularMap;
	}
	
	public ModelTexture(int texture, int numberOfRows){
		this.textureID = texture;
		this.numberOfRows = numberOfRows;
	}
	
	public ModelTexture(int texture, boolean hasTransparentcy){
		this.textureID = texture;
		this.hasTransparentcy = hasTransparentcy;
	}
	
	public boolean isHasTransparentcy() {
		return hasTransparentcy;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public ModelTexture setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
		return this;
	}

	public ModelTexture setHasTransparentcy(boolean hasTransparentcy) {
		this.hasTransparentcy = hasTransparentcy;
		return this;
	}
	
	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public ModelTexture setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
		return this;
	}

	public int getID(){
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public ModelTexture setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
		return this;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public ModelTexture setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}
	
	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
}
