package com.brett.voxel;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.DisplayManager;
import com.brett.KeyMaster;
import com.brett.console.Console;
import com.brett.console.commands.CloseServerCommand;
import com.brett.console.commands.CollisionCommand;
import com.brett.console.commands.FlightCommand;
import com.brett.console.commands.GiveCommand;
import com.brett.console.commands.TeleportCommand;
import com.brett.renderer.DisplaySource;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.font.fontRendering.StaticText;
import com.brett.renderer.gui.EscapeMenu;
import com.brett.renderer.gui.UIMaster;
import com.brett.renderer.shaders.LineShader;
import com.brett.renderer.shaders.PointShader;
import com.brett.sound.AudioController;
import com.brett.sound.AudioSource;
import com.brett.sound.MusicMaster;
import com.brett.tools.SettingsLoader;
import com.brett.voxel.gui.MainMenu;
import com.brett.voxel.inventory.InventoryMaster;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.BlockCrafting;
import com.brett.voxel.world.chunk.AtlasHelper;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.player.Player;
import com.sun.management.OperatingSystemMXBean;

/** 
*	Brett Terpstra
*	Mar 9, 2020
*	
*/
public class VoxelScreenManager {
	
	public static double frameRate = 120;
	public static double  averageFrameTimeMilliseconds = 8;
	public static OperatingSystemMXBean os;
	public static FontType monospaced;
	public static boolean isOpen = true;
	public static AudioSource staticSource;
	public static LineShader ls;
	public static PointShader pt;
	public static UIMaster ui;
	public static MasterRenderer renderer;
	public static VoxelWorld world;
	public static MainMenu mainmenu;
	
	private static DisplaySource scene;
	
	public static void init() {
		MeshStore.init();
		SettingsLoader.loadSettings();
		DisplayManager.createDisplay(false);
		// audio stuff
		AudioController.init();
		AudioController.setListenerData(0, 0, 0, 0, 0, 0);
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
		staticSource = new AudioSource();
		staticSource.setPosition(0, 0, 0);
		
		os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		// MAIN STUFF (REQUIRED FOR GAME TO RUN)
		Loader loader = new Loader();
		AtlasHelper.init();
		//CreativeFirstPersonCamera camera = new CreativeFirstPersonCamera(new Vector3f(0, 90, 0));
		ls = new LineShader();
		pt = new PointShader();
		//TexturedModel box_model = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("box")), new ModelTexture(loader.loadTexture("box")));
		//TexturedModel circleModel = new TexturedModel(loader.loadToVAO(OBJLoader.loadOBJ("hitmodel")), new ModelTexture(loader.loadTexture("error")));
		//FirstPersonPlayer player = new FirstPersonPlayer(box_model, new Vector3f(0, 0, 0), new Vector3f(0, 2, 0), 0, 0, 0, 1);
		//Camera camera = player.getCamera();
		StaticText.init(loader);
		monospaced = new FontType(loader.loadTexture("fonts/monospaced-72", 0), new File("resources/textures/fonts/monospaced-72.fnt"));
		ui = new UIMaster(loader);
		
		InventoryMaster.init(loader);
		GameRegistry.init(loader);
		
		Player player = new Player(loader, ui);
		LevelLoader.ply = player;
		
		renderer = new MasterRenderer(loader, player);
		ls.loadProjectionMatrix(renderer.getProjectionMatrix());
		pt.loadProjectionMatrix(renderer.getProjectionMatrix());
		//World world = new World(renderer, loader, camera, -5);
		
		// TERRAIN TEXTURES
		//TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		//TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		//TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		//TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		//TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		//TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain/blendMap"));
		
		// LIGHTING
		//Light sun = new Light(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1f, 1f, 1f));
		//lights.add(new Light(new Vector3f(15, 7, -15), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.01f, 0.002f)));
		//world.add(sun);
		
		// GUI RENDERERING
		//GUITexture gui = new GUITexture(renderer.getShadowMapTexture(), new Vector2f(0.0f, 0.0f), new Vector2f(0.5f, 0.5f));
		//GUITexture guid = new GUITexture(waterFBOs.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guitextures.add(gui);
		//guitextures.add(guid);
		
		// FONT
		Console console = new Console(loader, monospaced, ui.getRenderer());
		KeyMaster.registerKeyRequester(console);
		console.registerCommand(new String[] {"tp", "teleport"}, new TeleportCommand(player));
		console.registerCommand(new String[] {"flight", "fly", "f", "toggle_flight", "togglecollision"}, new FlightCommand());
		console.registerCommand(new String[] {"collision", "col", "c", "toggle_collision", "togglecollision"}, new CollisionCommand());
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
		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world.getTerrains());
		// ENTITIES
		//Entity entity = new Entity(TexturedModel.createTexturedModel(loader, "cursed-totem", "cursed-totem", 10, 1), new Vector3f(0, 0, -20), 0, 0, 0, 1);
		//Light l = new Light(new Vector3f(20, 5, -20), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.01f, 0.002f));
		//world.add(l);
		//TexturedModel lamp = TexturedModel.createTexturedModel(loader, "ground_lamp", "ground_lamp");
		//LightEntity ground_lamp = new LightEntity(lamp, l, new Vector3f(20, 5, -20), new Vector3f(0, 2.36f, 0), 0, 0, 0, 1);
		//Entity ent = new Entity(lamp, new Vector3f(45, 25, -45), 0, 0, 0, 1);
		//spawnableEnts.put("weird", entity);
		//spawnableEnts.put("w_lamp", ground_lamp);
		//console.registerCommand("spawn", new SpawnCommand(picker, world, loader));
		//Entity big_box = new Entity(box_model, new Vector3f(15, 80, 0), 0, 0, 0, 10);
		//hitent = new Entity(circleModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		//outlineEnt = new Entity(new TexturedModel(circleModel.getRawModel(), circleModel.getTexture()), new Vector3f(0, 0, 0), 0, 0, 0, 1);
		//ThirdPersonPlayer player = new ThirdPersonPlayer(TexturedModel.createTexturedModel(loader, "person", "playerTexture", 10, 1), new Vector3f(0, 0, -5), 0, 0, 0, 1);
		//ThirdPersonCamera rdCamera = new ThirdPersonCamera(player, 10, 80, 8);
		//loadingScreen.render(5);
		
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
		//world.spawnEntity(big_box);
		//world.spawnEntity(hitent);
		//world.spawnEntity(ent);
		//world.spawnEntity(player);
		
		//normalMapEntities.add(new Entity(cobblestone_floor_02, new Vector3f(75, 5, -75), 0, 0, 0, 1f));
		
		// water
		//WaterTile tile = new WaterTile(150, -5, -150, 125);
		//world.add(tile);
		//WaterTile tile2 = new WaterTile(-150, -5, -150, 125);
		//world.add(tile2);
		//loadingScreen.render(5);
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
		
		//PostProcessing.init(loader);
		
		//Server testserver = new Server();
		//testserver.start();
		//Client client = new Client("localhost");
		//client.start();
		//client.sendData("test".getBytes());
		
		console.registerCommand("give", new GiveCommand(player.getInventory()));
		
		world = new VoxelWorld(renderer, loader, player);
		EscapeMenu.world = world;
		console.registerCommand("exit", new CloseServerCommand());
		
		Mouse.setGrabbed(false);
		
		mainmenu = new MainMenu(ui, renderer, player, world, loader);
		scene = mainmenu;
		//scene = new VoxelRenderer(renderer, camera, world);
		
		//System.out.println(MeshStore.models.get(VoxelWorld.createSixBooleans(true, true, true, true, true, true)) == MeshStore.models.get(VoxelWorld.createSixBooleans(true, true, true, true, true, true)));
		UIDynamicText badjava = new UIDynamicText("", 0.9f, VoxelScreenManager.monospaced, new Vector2f(0.0f, 0.0f), 400, false);
		badjava.enableText();
		
		Chunk.init();

		MusicMaster.init();
		
		System.gc();
		while (!Display.isCloseRequested()) {
			double startTime = Sys.getTime() * 1000 / Sys.getTimerResolution();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			scene.render();
			KeyMaster.update();
			
			if (Mouse.isButtonDown(2)) {
				
			}
			
			//System.out.println(camera.getPosition());
			//sun.update();
			
			/**
			 * Everything below here must be rendered
			 */
			player.update();
			ui.render();
			InventoryMaster.render(ui.getRenderer());
			console.update();
			StaticText.render();
			
			//for (int i = 0; i < Chunk.deleteables.size(); i++) {
			//	loader.deleteVAO(Chunk.deleteables.get(i));
			//}
			
			MusicMaster.update();
			DisplayManager.updateDisplay();
			double lastFrame = Sys.getTime() * 1000 / Sys.getTimerResolution();
			deltaTime += lastFrame - startTime;
			frames++;
			if(deltaTime > 1000) {
				frameRate = (double)frames*0.5d + frameRate*0.5d;
				averageFrameTimeMilliseconds  = 1000.0/(frameRate==0?0.001:frameRate);
				StringBuilder sb = new StringBuilder();
				sb.append("FPS: ");
				sb.append((int)frameRate);
				sb.append(" + FT-MS: ");
				sb.append(averageFrameTimeMilliseconds);
				sb.append(" + YAW: ");
				sb.append(player.getYaw());
				sb.append(" + POS: [");
				Vector3f pos = player.getPosition();
				sb.append(pos.x);
				sb.append(", ");
				sb.append(pos.y);
				sb.append(", ");
				sb.append(pos.z);
				sb.append("]");
				badjava.changeText(sb.toString());
				//System.out.println(sb.toString());
				frames = 0;
				deltaTime = 0;
			}
		}
		if (VoxelWorld.isRemote)
			VoxelWorld.localClient.disconnect();
		isOpen = false;
		Mouse.setGrabbed(false);
		player.cleanup();
		BlockCrafting.craft.saveInventory();
		//testserver.close();
		//client.close();and it will 
		staticSource.delete();
		AudioController.cleanup();
		MusicMaster.cleanup();
		//PostProcessing.cleanUp();
		//vworld.cleanup();
		ui.cleanup();
		StaticText.cleanUp();
		ls.cleanUp();
		world.cleanup();
		loader.cleanUp();
		System.out.println("World and Loader have been cleaned up!");
		SettingsLoader.saveSettings();
		DisplayManager.closeDisplay();
		//System.exit(0);
	}
	
	public static void changeDisplaySource(DisplaySource s) {
		VoxelScreenManager.scene = s;
	}
	
}
