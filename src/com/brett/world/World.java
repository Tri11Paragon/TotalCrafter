package com.brett.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.renderer.Fbo;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.WaterTile;
import com.brett.renderer.illegal.RenderedPortal;
import com.brett.renderer.lighting.Light;
import com.brett.renderer.particles.ParticleMaster;
import com.brett.renderer.postprocessing.PostProcessing;
import com.brett.renderer.shaders.WaterShader;
import com.brett.renderer.world.water.WaterFrameBuffers;
import com.brett.renderer.world.water.WaterRenderer;
import com.brett.tools.TerrainArray;
import com.brett.world.blocks.Block;
import com.brett.world.cameras.Camera;
import com.brett.world.entities.Entity;
import com.brett.world.terrain.Terrain;
import com.tester.Main;

@SuppressWarnings("unused")
public class World {

	private List<Entity> ents;
	private HashMap<String, Entity> sentities;
	private List<WaterTile> waterTiles;
	private List<Entity> normalMapEntities;
	private List<Light> lights;
	
	private TerrainArray terrains;
	private MasterRenderer renderer;
	private WaterRenderer waterRenderer;
	
	private float waterHeight = 0;
	
	private Fbo multisampleFbo;
	private Fbo outputFbo;
	private Fbo brightOutputFbo;
	private WaterFrameBuffers waterFBO;
	
	private RenderedPortal portal;
	
	public World(MasterRenderer renderer, Loader loader, Camera camera) {
		this.renderer = renderer;
		setup(loader, camera);
	}
	
	public World(MasterRenderer renderer, Loader loader, Camera camera, float waterHeight) {
		this.renderer = renderer;
		this.waterHeight = waterHeight;
		setup(loader, camera);
	}
	
	private void setup(Loader loader, Camera camera) {
		Block.registerBlocks(loader);
		sentities = new HashMap<String, Entity>();
		waterFBO = new WaterFrameBuffers();
		ents = new ArrayList<Entity>();
		waterTiles = new ArrayList<WaterTile>();
		normalMapEntities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		terrains = new TerrainArray();
		portal = new RenderedPortal(renderer.getProjectionMatrix(), null, new Vector3f(0, 0, 0), 
				new Vector3f(10, 71, 10), new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(10, 10, 10), new Vector3f(10, 10, 10));
		//multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
		//outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		//brightOutputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		//waterRenderer = new WaterRenderer(loader, new WaterShader(), renderer.getProjectionMatrix(), waterFBO);
	}
	
	public void render(Camera camera, VoxelWorld world, Light sun, boolean renderWaterFBOs) {
		/*if (renderWaterFBOs) {
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			waterFBO.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waterHeight);
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(ents, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, 1, 0, -waterHeight+0.5f)); // offset to add stuff
			camera.invertPitch();
			camera.getPosition().y += distance;
			waterFBO.unbindCurrentFrameBuffer();
			
			waterFBO.bindRefractionFrameBuffer();
			renderer.renderScene(ents, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, -1, 0, waterHeight));
			waterFBO.unbindCurrentFrameBuffer();
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		}*/
		//renderer.renderShadowMap(ents, sun);
		
		portal.prepareRenderFrontFBO(camera);
		renderer.renderScene(ents, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, 0, 0, 0));
		world.render(camera);
		portal.render(camera);
		portal.unbindFrontFBO(camera);
		
		portal.prepareRenderBackFBO(camera);
		renderer.renderScene(ents, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, 0, 0, 0));
		world.render(camera);
		portal.render(camera);
		portal.unbindBackFBO(camera);
		
		/**
		 * the multisample FBO allows for some neat pp effects.
		 */
		//multisampleFbo.bindFrameBuffer();
		renderer.renderScene(ents, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, 0, 0, 0));
		world.render(camera);
		world.update();
		Main.ls.render();
		Main.ls.renderIN(new Vector3f(0,70,0), new Vector3f(0,90,0));
		Main.pt.render();
		portal.render(camera);
		//waterRenderer.render(waterTiles, camera, sun);
		ParticleMaster.renderParticles(camera);
		//multisampleFbo.unbindFrameBuffer();
		//multisampleFbo.resolveToFBO(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
		//multisampleFbo.resolveToFBO(GL30.GL_COLOR_ATTACHMENT1, brightOutputFbo);
		
		//PostProcessing.doPostProcessing(outputFbo.getColourTexture(), brightOutputFbo.getColourTexture());
	}
	
	public void update() {
		for (int i = 0; i < ents.size(); i++) {
			ents.get(i).update(this);
		}
	}
	
	public void cleanup() {
		portal.cleanup();
		//multisampleFbo.cleanUp();
		//outputFbo.cleanUp();
		//brightOutputFbo.cleanUp();
		//waterFBO.cleanUp();
		//waterRenderer.cleanup();
	}
	
	public void spawnEntity(Entity e) {
		ents.add(e);
	}
	
	public void spawnEntity(Entity e, Vector3f pos) {
		ents.add(e.setPosition(pos));
	}
	
	public HashMap<String, Entity> getSpawnableEntities() {
		return sentities;
	}
	
	public TerrainArray getTerrains() {
		return terrains;
	}
	
	public void add(Terrain t) {
		this.terrains.add(t);
	}
	
	public void add(Light l) {
		this.lights.add(l);
	}
	
	public void add(WaterTile t) {
		this.waterTiles.add(t);
	}
	
	public void addSpawnableEntitiy(String s, Entity e) {
		sentities.put(s, e);
	}
	
	public List<Entity> getEntities() {
		return ents;
	}
	
	public Entity getEntity(int index) {
		return ents.get(index);
	}
	
	public Entity getNormalMappedEntity(int index) {
		return ents.get(index);
	}
	
	public List<Entity> getNormalMappedEntities() {
		return normalMapEntities;
	}
	
	public List<WaterTile> getWaterTiles(){
		return waterTiles;
	}
	
	public List<Light> getLights(){
		return lights;
	}
	
	public boolean checkIfEntityExists(Entity e) {
		return ents.contains(e);
	}
	
	public WaterFrameBuffers getWaterFrameBuffers() {
		return waterFBO;
	}
	
}
