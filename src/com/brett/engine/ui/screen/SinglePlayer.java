package com.brett.engine.ui.screen;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.engine.DebugInfo;
import com.brett.engine.Loader;
import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.data.IMouseState;
import com.brett.engine.data.datatypes.VAO;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.DeferredPass1Shader;
import com.brett.engine.shaders.DeferredPass2Shader;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.shaders.VoxelShader;
import com.brett.engine.shaders.WorldShader;
import com.brett.engine.tools.Maths;
import com.brett.engine.tools.Settings;
import com.brett.engine.ui.AnchorPoint;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.UITexture;
import com.brett.engine.ui.console.Console;
import com.brett.engine.ui.console.PolygonCommand;
import com.brett.engine.ui.console.TeleportCommand;
import com.brett.engine.ui.font.UIText;
import com.brett.utils.NdHashMap;
import com.brett.world.GameRegistry;
import com.brett.world.Lighting;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.Region;
import com.brett.world.tools.MouseBlockPicker;
import com.brett.world.tools.RayCasting;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class SinglePlayer extends Screen implements IMouseState {

	public static Matrix4f chunkViewMatrix;
	public static Matrix4f viewMatrix;
	public CreativeCamera camera;
	public int textureAtlas;
	public World world;
	private VoxelShader shader;
	private DeferredPass1Shader gshader;
	private DeferredPass2Shader sshader;
	private UIMenu console;
	private static int gBuffer = -1, rboDepth, gPosition, gNormal, gColorSpec;
	private Thread th;
	private boolean running = false;
	
	public SinglePlayer(UIMenu console) {
		textureAtlas = ScreenManager.loader.loadSpecialTextureATLAS(16, 16);
		GameRegistry.registerBiomes();
		GameRegistry.registerBlocks();
		GameRegistry.registerItems();
		this.console = console;
	}
	
	@Override
	public void onSwitch() {
		running = true;
		super.onSwitch();
		InputMaster.mouse.add(this);
		elements.add(new UITexture(ScreenManager.loader.loadTexture("crosshair"), -2, -2, 0, 0, 16, 16, AnchorPoint.CENTER));
		
		/*UIText text = new UIText("he l loe there", 250.0f, "mono", 600, 300, 20);
		elements.add(new UIButton(ScreenManager.loader.loadTexture("dirt"), ScreenManager.loader.loadTexture("clay"), 600, 300, 115, 100).setText(text));
		
		UITexture bar = new UITexture(ScreenManager.loader.loadTexture("coal_block"), -1, -1, 50, 50, 10, 30);
		elements.add(new UISlider(ScreenManager.loader.loadTexture("sand"), bar, 50, 50, 300, 30));
		elements.add(bar);
		UIText textd = new UIText("", 250, "mono", 50, 200, 500);
		addText(textd);
		elements.add(new UITextInput(ScreenManager.loader.loadTexture("clay"), textd, 31, 50, 200, 400, 50));*/
		
		world = new World("w1");
		Lighting.init(world);
		camera = new CreativeCamera(new Vector3d(0,30,0), world);
		Console.registerCommand(new String[] {"tp", "teleport"}, new TeleportCommand(camera));
		Console.registerCommand(new String[] {"poly", "pmode", "rend.p", "renderer.p"}, new PolygonCommand());
		
		menus.add(console);
		menus.add(DebugInfo.init(camera));
		
		
		shader = new VoxelShader();
		
		shader.start();
		shader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		shader.stop();
		
		gshader = new DeferredPass1Shader();
		gshader.start();
		gshader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		gshader.stop();
		
		sshader = new DeferredPass2Shader();
		sshader.start();
		sshader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		sshader.connectTextureUnits();
		sshader.stop();
		
		genBuffers();
		
		elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gColorSpec, -1, 0, 250, 250, -250));
		elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gNormal, -1, 0, 500, 250, -250));
		elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gPosition, -1, 0, 750, 250, -250));
		
		//GL13.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					
					int rgDist = (int)((Settings.RENDER_DISTANCE >> 3) + 1) * 2 + 1;
					//System.out.println("SAVE CYCLE " + rgDist);
					
					world.regions.iterate( (NdHashMap<Integer, Region> rg, Integer rx, Integer ry, Integer rz, Region r) -> {
						r.save();
						Vector3d pos = camera.getPosition();
						int rpx = ((int)(pos.x) >> 4) >> 3;
						int rpy = ((int)(pos.y) >> 4) >> 3;
						int rpz = ((int)(pos.z) >> 4) >> 3; 
						double dist = Math.sqrt(Math.pow(rx - rpx, 2) + Math.pow(ry - rpy, 2) + Math.pow(rz - rpz, 2));
						
						if (dist > rgDist) {
							System.out.println("Unloading region: " + rx + " " + ry + " " + rz + " || " + dist);
							world.regions.get(rx, ry, rz).cleanup();
							world.regions.remove(rx, ry, rz);
						}
					});
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {break;}
				}
			}
		});
		th.start();
	}
	
	private float ccx,ccy,ccz;
	private int cx,cy,cz;
	private Vector3d pos;
	private long lastCheckTime = 0;
	private boolean checked = false;
	
	@Override
	public List<UIElement> render() {
		if (World.deleteVAOS.size() > 0) {
			VAO v = World.deleteVAOS.get(0);
			if (v != null) {
				Loader.l.deleteVAO(v);
			}
			World.deleteVAOS.remove(0);
		}
		camera.move();
		chunkViewMatrix = Maths.createViewMatrixROT(camera);
		viewMatrix = Maths.createViewMatrix(camera);
		camera.calculateFrustum(ProjectionMatrix.projectionMatrix, viewMatrix);
		RayCasting.update(camera);
		
		ScreenManager.enableCulling();
		ScreenManager.enableTransparentcy();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
		
		//shader.start();
		//shader.loadViewMatrix(chunkViewMatrix);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		if (PolygonCommand.renderMode != GL11.GL_FILL)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, PolygonCommand.renderMode);
		gshader.start();
		gshader.loadViewMatrix(chunkViewMatrix);
		
		drawLoop(gshader);
		
		//shader.stop();
		gshader.stop();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		if (PolygonCommand.renderMode != GL11.GL_FILL)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		sshader.start();
		sshader.loadViewMatrix(chunkViewMatrix);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gColorSpec);
		// rewrite first pass to include light data in sperate texture
		// (store in a float16)
		// then in second pass we don't need to load light
		// just use light data from the image
		
		for (int i = 0; i < DeferredPass2Shader.MAX_LIGHTS; i++) {
			// not sure why only Y needs bitshift to work?
			int lx = 1;
			int ly = 81;
			int lz = 4;
			int x = ((int)((lx/16) - ((camera.getX()) / 16) )) * 16 + (lx % 16);
			int y = ((int)((ly >> 4) - ((int)(camera.getY()) >> 4) )) * 16 + (ly % 16);
			int z = ((int)((lz/16) - ((camera.getZ()) / 16) )) * 16 + (lz % 16);
			sshader.loadVector("lights[" + i + "].Position", x, y, z);
			sshader.loadVector("lights[" + i + "].Color", 0.5f, 1.0f, 1.0f);
			final float constant = 1.0f; // note that we don't send this to the shader, we assume it is always 1.0 (in our case)
            final float linear = 0.7f;
            final float quadratic = 1.2f;
            sshader.loadFloat("lights[" + i + "].Linear", linear);
            sshader.loadFloat("lights[" + i + "].Quadratic", quadratic);
            final float maxBrightness = Math.max(Math.max(0.5f, 1.0f), 1.0f);
            float radius = (float) ((-linear + Math.sqrt(linear * linear - 4 * quadratic * (constant - (256.0f / 5.0f) * maxBrightness))) / (2.0f * quadratic)) + 100000;
            sshader.loadFloat("lights[" + i + "].Radius", radius);
		}
		int lx = (int)camera.getX();
		int ly = (int)camera.getY();
		int lz = (int)camera.getZ();
		float cx = (float) (camera.getX() - lx);
		float cy = (float) (camera.getY() - ly);
		float cz = (float) (camera.getZ() - lz);
		int x = ((int)((lx/16) - ((camera.getX()) / 16) )) * 16 + (lx % 16);
		int y = ((int)((ly / 16) - ((camera.getY()) / 16) )) * 16 + (ly % 16);
		int z = ((int)((lz/16) - ((camera.getZ()) / 16) )) * 16 + (lz % 16);
		sshader.loadVector("lights[" + 0 + "].Position", x + cx, y + cy, z + cz);
		sshader.loadVector("lights[" + 0 + "].Color", 10.0f, 10.0f, 10.0f);
		sshader.loadViewPos(camera.getPosition());
		sshader.loadFloat("lights[" + 0 + "].Linear", 0.7f);
        sshader.loadFloat("lights[" + 0 + "].Quadratic", 1.1f);
		sshader.loadVector("directlight", -0.2f, -1.0f, -0.3f);
		sshader.loadFloat("lights[" + 0 + "].Radius", 80);
		
		renderQuad();
		
		sshader.stop();
		
		//GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer);
		//GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		//GL30.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL30.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
		//GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		ScreenManager.disableCulling();
		ScreenManager.disableTransparentcy();
		
		return super.render();
	}
	
	//TODO: not steal this please
	// make better too
	//VAO quadVAO = null;
	int quadVAO = 0;
	int quadVBO;
	private void renderQuad() {
		if (quadVAO == 0)
	    {
			final float quadVertices[] = {
		            // positions        // texture Coords
		            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
		            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
		             1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
		             1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
		       };
			quadVAO = GL30.glGenVertexArrays();
			quadVBO = GL30.glGenBuffers();
			GL30.glBindVertexArray(quadVAO);
			GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, quadVBO);
			GL30.glBufferData(GL30.GL_ARRAY_BUFFER, quadVertices, GL30.GL_STATIC_DRAW);
			GL30.glEnableVertexAttribArray(0);
			GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 5 * 4, 0);
			GL30.glEnableVertexAttribArray(1);
			GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 5 * 4, (3 * 4));
	    }
	    GL30.glBindVertexArray(quadVAO);
	    GL11.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4);
	    GL30.glBindVertexArray(0);
	}
	
	private void drawLoop(WorldShader sh) {
		pos = camera.getPosition();
		
		for (int i = -Settings.RENDER_DISTANCE; i <= Settings.RENDER_DISTANCE; i++) {
			for (int j = -Settings.RENDER_DISTANCE; j <= Settings.RENDER_DISTANCE; j++) {
				for (int k = -Settings.RENDER_DISTANCE; k <= Settings.RENDER_DISTANCE; k++) {
					//double distance = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));
					//if (distance > Settings.RENDER_DISTANCE)
					//	continue;
					cx = ((int) (pos.x / 16)) + i;
					cy = ((int) (pos.y / 16)) + j;
					cz = ((int) (pos.z / 16)) + k;
					
					ccx = cx*16;
					ccy = cy*16;
					ccz = cz*16;
					
					if (!camera.cubeInFrustum(ccx, ccy, ccz, ccx+16, ccy+16, ccz+16))
						continue;
					
					Chunk c = world.getChunk(cx, cy, cz);
					if (c == null)
						continue;
						//world.queueChunk(cx, cy, cz);
					else {
						c.render(sh, i, j, k);
						// this is cancer pls ignore
						if (System.currentTimeMillis() - lastCheckTime > 500) {
							checked = true;
							if (!c.hasMeshed || c.chunkInfo > 0) {
								world.worldExecutor.execute(() -> {
									c.iNeedMesh();
								});
							}
						}
					}
				}
			}
		}
		if (checked) {
			lastCheckTime = System.currentTimeMillis();
			checked = false;
		}
	}
	
	@Override
	public Map<String, List<UIText>> renderText() {
		return super.renderText();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	@Override
	public void onLeave() {
		world.save();
		running = false;
		th.interrupt();
		menus.remove(DebugInfo.destroy());
		InputMaster.mouse.remove(this);
		if (gBuffer > -1) {
			GL30.glDeleteFramebuffers(gBuffer);
			GL11.glDeleteTextures(gPosition);
			GL11.glDeleteTextures(gNormal);
			GL11.glDeleteTextures(gColorSpec);
			GL30.glDeleteRenderbuffers(rboDepth);
		}
		super.onLeave();
	}
	
	public static void genBuffers() {
		if (gBuffer > -1) {
			GL30.glDeleteFramebuffers(gBuffer);
			GL11.glDeleteTextures(gPosition);
			GL11.glDeleteTextures(gNormal);
			GL11.glDeleteTextures(gColorSpec);
			
			GL30.glDeleteRenderbuffers(rboDepth);
		}
		
		gBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		gPosition = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, gPosition, 0);
		
		gNormal = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, gNormal, 0);
		
		gColorSpec = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gColorSpec);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, gColorSpec, 0);

		//IntBuffer drawBuffers = BufferUtils.createIntBuffer(3);
		//drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
		//drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
		//drawBuffers.put(GL30.GL_COLOR_ATTACHMENT2);
		
		//drawBuffers.flip();
		//GL20.glDrawBuffers(drawBuffers);
		GL20.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2});
		
		rboDepth = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboDepth);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboDepth);
		
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error creating framebuffer!");
			System.err.println(GL30.glGetError());
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void onMousePressed(int button) {
		if (button == 0)
			MouseBlockPicker.getBlockMine(world, camera, 6, Block.AIR);
		else if (button == 1) {
			if (InputMaster.keyDown[GLFW.GLFW_KEY_X])
				MouseBlockPicker.getBlockPlace(world, camera, 6, Block.STONE);
			else
				MouseBlockPicker.getBlockPlace(world, camera, 6, Block.GLOWSTONE);
		}
	}

	@Override
	public void onMouseReleased(int button) {
	}
	
}
