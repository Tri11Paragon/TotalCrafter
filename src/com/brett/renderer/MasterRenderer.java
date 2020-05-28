package com.brett.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.renderer.datatypes.TexturedModel;
import com.brett.renderer.lighting.Light;
import com.brett.renderer.shaders.StaticShader;
import com.brett.renderer.shaders.TerrainShader;
import com.brett.renderer.shadows.ShadowBox;
import com.brett.renderer.shadows.ShadowMapMasterRenderer;
import com.brett.renderer.world.EntityRenderer;
import com.brett.renderer.world.NormalMappingRenderer;
import com.brett.renderer.world.SkyboxRenderer;
import com.brett.renderer.world.TerrainRenderer;
import com.brett.world.cameras.Camera;
import com.brett.world.cameras.ICamera;
import com.brett.world.entities.Entity;
import com.brett.world.terrain.Terrain;

public class MasterRenderer {

	public static float FOV = 90;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	private static final float DENSITY = 0.004f;
	private static final float GRADIANT = 2.5f;
	public static final int DRAWMODE = GL11.GL_TRIANGLES;
	public static Loader global_loader;
	
	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public static Matrix4f projectionMatrix;
	
	private StaticShader shader;
	private EntityRenderer renderer;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private SkyboxRenderer skyboxRenderer;
	private NormalMappingRenderer normalMapRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private Map<TexturedModel,List<Entity>> normalEntities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	public MasterRenderer(Loader loader, Camera cam){
		MasterRenderer.global_loader = loader;
		enableCulling();
		createProjectionMatrix();
		shader = new StaticShader();
		terrainShader = new TerrainShader();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, RED, GREEN, BLUE);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(cam);
		shader.start();
		shader.loadFogInfo(DENSITY, GRADIANT);
		shader.loadShadowInfo(ShadowBox.SHADOW_DISTANCE, ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		shader.stop();
		terrainShader.start();
		terrainShader.loadFogInfo(DENSITY, GRADIANT);
		terrainShader.loadShadowInfo(ShadowBox.SHADOW_DISTANCE, ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		terrainShader.stop();
	}
	
	public void render(List<Light> lights,Camera camera, Vector4f clipPlane){
		prepare();
		enableShadows();
		shader.start();
		shader.loadLight(lights);
		shader.loadViewMatrix(camera);
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(RED, GREEN, BLUE);
		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		shader.stop();
		normalMapRenderer.render(normalEntities, clipPlane, lights, camera);
		terrainShader.start();
		terrainShader.loadLight(lights);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadSkyColor(RED, GREEN, BLUE);
		terrainShader.loadClipPlane(clipPlane);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		skyboxRenderer.render(camera);
		entities.clear();
		terrains.clear();
		normalEntities.clear();
	}
	
	public void renderSky(ICamera camera) {
		prepare();
		skyboxRenderer.render(camera);
	}
	
	public void renderSkyNone(ICamera camera) {
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		skyboxRenderer.render(camera);
	}
	
	public void renderScene(List<Entity> entities, List<Entity> normalEnts, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane) {
		for (Terrain terrain : terrains) {
			processTerrain(terrain);
		}
		for (Entity entity : entities) {
			processEntity(entity);
		}
		for (Entity entity : normalEnts) {
			processNormalMappedEntity(entity);
		}
		render(lights, camera, clipPlane);
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void enableTransparentcy() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableTransparentcy() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);		
		}
	}
	
	public void processNormalMappedEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalEntities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalEntities.put(entityModel, newBatch);		
		}
	}
	
	public void renderShadowMap(List<Entity> entityList, Light sun) {
		for (Entity e : entityList) {
			processEntity(e);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	public void enableShadows() {
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	
	private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
	
	// old projection matrix code.
	/*private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}*/
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
}
