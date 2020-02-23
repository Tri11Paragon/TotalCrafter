package com.tester;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import com.brett.DisplayManager;
import com.brett.console.Console;
import com.brett.console.commands.SpawnCommand;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.renderer.datatypes.WaterTile;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.fontRendering.TextMaster;
import com.brett.renderer.gui.UIMaster;
import com.brett.renderer.lighting.Light;
import com.brett.renderer.particles.ParticleMaster;
import com.brett.renderer.postprocessing.PostProcessing;
import com.brett.sound.AudioController;
import com.brett.sound.AudioSource;
import com.brett.tools.MousePicker;
import com.brett.tools.SettingsLoader;
import com.brett.tools.obj.OBJLoader;
import com.brett.world.VoxelWorld;
import com.brett.world.World;
import com.brett.world.cameras.CreativeFirstPersonCamera;
import com.brett.world.entities.Entity;
import com.sun.management.OperatingSystemMXBean;

public class Main {
	
	public static double frameRate = 120;
	public static double  averageFrameTimeMilliseconds = 8;
	public static OperatingSystemMXBean os;
	public static Entity hitent;
	public static boolean isOpen = true;
	public static AudioSource staticSource;
	
	public static void main(String[] args) {
		SettingsLoader.loadSettings();
		DisplayManager.createDisplay("Icon", false);
		// audio stuff
		AudioController.init();
		AudioController.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
		staticSource = new AudioSource();
		
		os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		// MAIN STUFF (REQUIRED FOR GAME TO RUN)
		Loader loader = new Loader();
		CreativeFirstPersonCamera camera = new CreativeFirstPersonCamera(new Vector3f(0, 72, 0));
		TexturedModel box_model = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("box")), new ModelTexture(loader.loadTexture("box")));
		TexturedModel circleModel = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("hitmodel")), new ModelTexture(loader.loadTexture("error")));
		//FirstPersonPlayer player = new FirstPersonPlayer(box_model, new Vector3f(0, 0, 0), new Vector3f(0, 2, 0), 0, 0, 0, 1);
		//Camera camera = player.getCamera();
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		UIMaster ui = new UIMaster(loader);
		ui.addCenteredTexture(loader.loadTexture("crosshair"), -1, -1, 0, 0, 16, 16);
		World world = new World(renderer, loader, -5);
		TextMaster.init(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		HashMap<String, Entity> spawnableEnts = new HashMap<String, Entity>();
		
		// TERRAIN TEXTURES
		//TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		//TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		//TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		//TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		//TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		//TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/blendMap"));
		
		// LIGHTING
		Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1f, 1f, 1f));
		//lights.add(new Light(new Vector3f(15, 7, -15), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.01f, 0.002f)));
		world.add(sun);
		
		// GUI RENDERERING
		//GUITexture gui = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.0f, 0.0f), new Vector2f(0.5f, 0.5f));
		//GUITexture guid = new GUITexture(waterFBOs.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guitextures.add(gui);
		//guitextures.add(guid);
		
		// FONT
		FontType monospaced = new FontType(loader.loadTexture("fonts/monospaced-72", 0), new File("resources/textures/fonts/monospaced-72.fnt"));
		Console console = new Console(loader, monospaced, ui.getRenderer());
		//new GUIText("Hello" + '\n' + "There!", 3, monospaced, new Vector2f(0, 0), 0.5f, false, 0);
		
		// ENTITY MODELS
		//ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"), 2);
		//fernTexture.setShineDamper(1000000.0f);
		//fernTexture.setReflectivity(-1000.5f);
		//fernTexture.setHasTransparentcy(true);
		//RawModel fernModel = loader.loadToVAO(OBJLoader.loadOBJ("fern"));
		//TexturedModel fernModelTexture = new TexturedModel(fernModel, fernTexture);
		
		// TERRAIN
		//world.add(new Terrain(-1, -1, loader, texturePack, blendMap, "height2"));
		//world.add(new Terrain(0, -1, loader, texturePack, blendMap, "height2"));
		//world.add(new Terrain(-1, 0, loader, texturePack, blendMap, "height2"));
		//world.add(new Terrain(0, 0, loader, texturePack, blendMap, new HeightGenerator(0,0, 128, new PerlinNoiseFunction(694))));
		
		// mouse picker for entities
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world.getTerrains());
		// ENTITIES
		//Entity entity = new Entity(TexturedModel.createTexturedModel(loader, "cursed-totem", "cursed-totem", 10, 1), new Vector3f(0, 0, -20), 0, 0, 0, 1);
		//Light l = new Light(new Vector3f(20, 5, -20), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.01f, 0.002f));
		//world.add(l);
		//TexturedModel lamp = TexturedModel.createTexturedModel(loader, "ground_lamp", "ground_lamp");
		//LightEntity ground_lamp = new LightEntity(lamp, l, new Vector3f(20, 5, -20), new Vector3f(0, 2.36f, 0), 0, 0, 0, 1);
		//Entity ent = new Entity(lamp, new Vector3f(45, 25, -45), 0, 0, 0, 1);
		//spawnableEnts.put("weird", entity);
		//spawnableEnts.put("w_lamp", ground_lamp);
		console.registerCommand("spawn", new SpawnCommand(picker, world, loader));
		Entity big_box = new Entity(box_model, new Vector3f(0, 80, 0), 0, 0, 0, 10);
		hitent = new Entity(circleModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		//ThirdPersonPlayer player = new ThirdPersonPlayer(TexturedModel.createTexturedModel(loader, "person", "playerTexture", 10, 1), new Vector3f(0, 0, -5), 0, 0, 0, 1);
		//ThirdPersonCamera rdCamera = new ThirdPersonCamera(player, 10, 80, 8);
		
		//TexturedModel cobblestone_floor_02 = new TexturedModel(NormalMappedObjLoader.loadOBJ("plane", loader), new ModelTexture(loader.loadTexture("terrain/diffuses/cobblestone_floor_02_diff_2k"), loader.loadTexture("terrain/normalmaps/cobblestone_floor_02_nor_2k"), 20.0f, 0.5f));
		
		// Add ferns to world
		System.out.println(694); // <- Will.
		/*Random random = new Random(694); // TODO: replace this with seed
		for (int i = 0; i < 400; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * -800;
			Terrain t = terrains.get(new Vector3f(x, 0, z));
			float y = t.getHeightOfTerrain(x, z);
				
			entities.add(new Entity(fernModelTexture, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, Math.max(0.3f, random.nextFloat())));
		}*/
		
		//world.spawnEntity(entity);
		//world.spawnEntity(ground_lamp);
		//entities.add(player);texturename
		world.spawnEntity(big_box);
		world.spawnEntity(hitent);
		//world.spawnEntity(ent);
		//world.spawnEntity(player);
		
		//normalMapEntities.add(new Entity(cobblestone_floor_02, new Vector3f(75, 5, -75), 0, 0, 0, 1f));
		
		// water
		WaterTile tile = new WaterTile(150, -5, -150, 125);
		world.add(tile);
		WaterTile tile2 = new WaterTile(-150, -5, -150, 125);
		world.add(tile2);
		
		/*ParticleTexture tempTexture = new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4).useAdditiveBlending();
		ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("particles/fire"), 8);
		ParticleTexture smokeTexture = new ParticleTexture(loader.loadTexture("particles/smoke"), 8);
		
		AdvancedParticleSystem advSys = new AdvancedParticleSystem(tempTexture, 50, 55, 1, 3, 1);
		advSys.setDirection(new Vector3f(0, 1, 0), 0.05f);
		
		AdvancedParticleSystem fireSys = new AdvancedParticleSystem(fireTexture, 10, 5, 0, 3, 5);
		fireSys.setDirection(new Vector3f(0, 1, 0), 0.01f);
		fireSys.setScaleError(0.2f);
		
		AdvancedParticleSystem smokeSys = new AdvancedParticleSystem(smokeTexture, 10, 7, 0, 5, 15);
		smokeSys.setDirection(new Vector3f(0, 1, 0), 0.01f);
		smokeSys.setScaleError(0.25f);
		*/
		
		double deltaTime = 0;
		int frames = 0;
		
		PostProcessing.init(loader);
		
		//Server testserver = new Server();
		//testserver.start();
		//Client client = new Client("localhost");
		//client.start();
		//client.sendData("test".getBytes());
		
		VoxelWorld vworld = new VoxelWorld(renderer, loader, camera);
		
		Mouse.setGrabbed(false);
		
		while (!Display.isCloseRequested()) {
			double startTime = Sys.getTime() * 1000 / Sys.getTimerResolution();
			camera.move();
			AudioController.setListenerPosition(camera.getPosition());
			//rdCamera.move();
			//rdCamera.checkCollision(terrain);
			//player.update();
			//player.checkCollision(terrain);
			//renderer.processEntity(player);
			
			//advSys.generateParticles(player.getPosition());
			//fireSys.generateParticles(new Vector3f(50, 0, -50));
			//smokeSys.generateParticles(new Vector3f(150, 3, -150));
			
			ParticleMaster.update(camera);
			picker.update();
			//Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			//if (terrainPoint != null & !Mouse.isGrabbed()) {ng 
				//entity.setPosition(terrainPoint);
			//}
			//System.out.println(Mouse.getX() + " " + Mouse.getY());
			world.update();
			world.render(camera, vworld, sun, true);
			
			//System.out.println(camera.getPosition());
			//sun.update();
			ui.render();
			console.update();
			TextMaster.render();
			DisplayManager.updateDisplay();
			double lastFrame = Sys.getTime() * 1000 / Sys.getTimerResolution();
			deltaTime += lastFrame - startTime;
			frames++;
			if(deltaTime > 1000) {
				frameRate = (double)frames*0.5d + frameRate*0.5d;
				averageFrameTimeMilliseconds  = 1000.0/(frameRate==0?0.001:frameRate);
				//System.out.println("Frames per Seconds: " + (int)frameRate);
				frames = 0;
				deltaTime = 0;
			}
		}
		//testserver.close();
		//client.close();
		staticSource.delete();
		AudioController.cleanup();
		PostProcessing.cleanUp();
		world.cleanup();
		ui.cleanup();
		TextMaster.cleanUp();
		ParticleMaster.cleanUp();
		world.cleanup();
		loader.cleanUp();
		SettingsLoader.saveSettings();
		isOpen = false;
		DisplayManager.closeDisplay();
	}

}

/* TODO List
 * 
 * make lights when they are inside objects work.
 * Background resource loader
 * make FBOs have more then 2 color attachments?
 * add multi-terrain support (check)
 * add settings saver / loader
 * add world loader / saver
 * add world builder
 * merge FBO classes
 * add entity picking
 * add shadows to entities
 * finish water renderer / clean up water renderer
 * add some uniforms for font rendering
 * add bash
 * add console (checkish)
 * ^.*$
 */