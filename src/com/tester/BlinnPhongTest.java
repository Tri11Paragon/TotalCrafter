package com.tester;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.DisplayManager;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.renderer.datatypes.WaterTile;
import com.brett.renderer.font.fontRendering.TextMaster;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.lighting.Light;
import com.brett.renderer.particles.ParticleMaster;
import com.brett.renderer.shaders.WaterShader;
import com.brett.renderer.world.water.WaterFrameBuffers;
import com.brett.renderer.world.water.WaterRenderer;
import com.brett.tools.SettingsLoader;
import com.brett.tools.TerrainArray;
import com.brett.tools.obj.normalMappingObjConverter.NormalMappedObjLoader;
import com.brett.world.cameras.CreativeFirstPersonCamera;
import com.brett.world.entities.Entity;

/**
*
* @author brett
*	Used to test stuff
*
*/

public class BlinnPhongTest {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		DisplayManager.createDisplay(false);
		SettingsLoader.loadSettings();
		
		// MAIN STUFF (REQUIRED FOR GAME TO RUN)
		Loader loader = new Loader();
		CreativeFirstPersonCamera camera = new CreativeFirstPersonCamera(new Vector3f(0,0,0));
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		GUIRenderer GUIRenderer = new GUIRenderer(loader);
		WaterFrameBuffers waterFBOs = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(loader, new WaterShader(), renderer.getProjectionMatrix(), waterFBOs);
		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		List<Entity> entities = new ArrayList<Entity>();
		TerrainArray terrains = new TerrainArray();
		List<WaterTile> waterTiles = new ArrayList<WaterTile>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		List<Light> lights = new ArrayList<Light>();
		List<UIElement> guitextures = new ArrayList<UIElement>();
		camera.getPosition().y = 10;
		
		//Sun sun = new Sun(camera, new Vector3f(500, 10000, 500), new Vector3f(0.1f, 0.1f, 0.1f));
		lights.add(new Light(new Vector3f(0, 7, 0), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.01f, 0.002f)));
		//lights.add(sun);
		
		TexturedModel cobblestone_floor_02 = new TexturedModel(NormalMappedObjLoader.loadOBJ("plane", loader), new ModelTexture(loader.loadTexture("terrain/diffuses/cobblestone_floor_02_diff_2k"), loader.loadTexture("terrain/normalmaps/cobblestone_floor_02_nor_2k"), 20.0f, 0.5f));
		normalMapEntities.add(new Entity(cobblestone_floor_02, new Vector3f(0,-5, 0), 0, 0, 0, 5f));
		
		while (!Display.isCloseRequested()) {
			camera.move();
			
			renderer.renderScene(entities, normalMapEntities, terrains.getAll(), lights, camera, new Vector4f(0, 0, 0, 0));
			GUIRenderer.render(guitextures);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		
		waterFBOs.cleanUp();
		waterRenderer.cleanup();
		GUIRenderer.cleanup();
		TextMaster.cleanUp();
		ParticleMaster.cleanUp();
		loader.cleanUp();
		SettingsLoader.saveSettings();
		DisplayManager.closeDisplay();
	}

}
