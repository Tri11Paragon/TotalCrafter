package com.brett.engine.ui.screen;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.engine.DebugInfo;
import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.data.IMouseState;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.GBufferShader;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.shaders.VoxelGBufferShader;
import com.brett.engine.tools.Maths;
import com.brett.engine.tools.Settings;
import com.brett.engine.ui.AnchorPoint;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.UITexture;
import com.brett.engine.ui.console.Console;
import com.brett.engine.ui.console.TeleportCommand;
import com.brett.engine.ui.font.UIText;
import com.brett.world.GameRegistry;
import com.brett.world.Lighting;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.NdHashMap;
import com.brett.world.chunks.data.ShortBlockStorage;
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
	public VoxelGBufferShader shader;
	public GBufferShader gshader;
	private UIMenu console;
	private static int gBuffer = -1, rboDepth, gPosition, gNormal, gColorSpec;
	
	public SinglePlayer() {
		textureAtlas = ScreenManager.loader.loadSpecialTextureATLAS(16, 16);
		GameRegistry.registerBlocks();
		GameRegistry.registerItems();
		console = Console.init();
		InputMaster.mouse.add(this);
	}
	
	@Override
	public void onSwitch() {
		super.onSwitch();
		elements.add(new UITexture(ScreenManager.loader.loadTexture("crosshair"), -2, -2, 0, 0, 16, 16, AnchorPoint.CENTER));
		
		/*UIText text = new UIText("he l loe there", 250.0f, "mono", 600, 300, 20);
		elements.add(new UIButton(ScreenManager.loader.loadTexture("dirt"), ScreenManager.loader.loadTexture("clay"), 600, 300, 115, 100).setText(text));
		
		UITexture bar = new UITexture(ScreenManager.loader.loadTexture("coal_block"), -1, -1, 50, 50, 10, 30);
		elements.add(new UISlider(ScreenManager.loader.loadTexture("sand"), bar, 50, 50, 300, 30));
		elements.add(bar);
		UIText textd = new UIText("", 250, "mono", 50, 200, 500);
		addText(textd);
		elements.add(new UITextInput(ScreenManager.loader.loadTexture("clay"), textd, 31, 50, 200, 400, 50));*/
		
		world = new World();
		Lighting.init(world);
		camera = new CreativeCamera(new Vector3d(0,140,0), world);
		Console.registerCommand(new String[] {"tp", "teleport"}, new TeleportCommand(camera));
		
		menus.add(console);
		menus.add(DebugInfo.init(camera));
		
		
		shader = new VoxelGBufferShader();
		gshader = new GBufferShader();
		shader.start();
		shader.loadProjectionMatrox(ProjectionMatrix.projectionMatrix);
		shader.stop();
		gshader.start();
		gshader.connectTextureUnits();
		gshader.loadProjectionMatrox(ProjectionMatrix.projectionMatrix);
		gshader.stop();
		
		ShortBlockStorage shrt2 = new ShortBlockStorage();
		
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				shrt2.setWorld(i, 1, k, Block.STONE);
			}
		}
		
		Chunk c2 = new Chunk(world, shrt2, null, 1, 0, 0);
		world.setChunk(1, 0, 0, c2);
		c2.meshChunk();
		
		genBuffers();
		
		//elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gColorSpec, -1, 0, 0, 500, 500));
		//elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gNormal, -1, 500, 0, 500, 500));
		elements.add(new UITexture(ScreenManager.loader.loadTexture("stone"), gPosition, -1, 0, 0, 500, 500));
		
		//GL13.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
	}
	
	private float ccx,ccy,ccz;
	private int cx,cy,cz, ll;
	private Vector3d pos;
	
	@Override
	public List<UIElement> render() {
		ll = 0;
		camera.move();
		chunkViewMatrix = Maths.createViewMatrixROT(camera);
		viewMatrix = Maths.createViewMatrix(camera);
		camera.calculateFrustum(ProjectionMatrix.projectionMatrix, viewMatrix);
		RayCasting.update(camera);
		
		ScreenManager.enableCulling();
		ScreenManager.enableTransparentcy();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
		
		shader.start();
		shader.loadViewMatrix(chunkViewMatrix);
		pos = camera.getPosition();
		shader.loadCamera(pos);
		
		for (int i = -Settings.RENDER_DISTANCE; i <= Settings.RENDER_DISTANCE; i++) {
			for (int j = -Settings.RENDER_DISTANCE; j <= Settings.RENDER_DISTANCE; j++) {
				for (int k = -Settings.RENDER_DISTANCE; k <= Settings.RENDER_DISTANCE; k++) {
					double distance = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));
					if (distance > Settings.RENDER_DISTANCE)
						continue;
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
						world.queueChunk(cx, cy, cz);
					else
						c.render(shader, i, j, k);
				}
			}
		}
		
		shader.stop();
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL11.glDrawBuffer(GL11.GL_BACK);
		GL30.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		gshader.start();
		gshader.loadViewPos(camera.getPosition());
		
		ScreenManager.uiRenderer.startRenderQuad();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gColorSpec);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gPosition);
		
		for (int i = -Settings.RENDER_DISTANCE; i <= Settings.RENDER_DISTANCE; i++) {
			for (int j = -Settings.RENDER_DISTANCE; j <= Settings.RENDER_DISTANCE; j++) {
				for (int k = -Settings.RENDER_DISTANCE; k <= Settings.RENDER_DISTANCE; k++) {
					double distance = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));
					if (distance > Settings.RENDER_DISTANCE)
						continue;
					pos = camera.getPosition();
					cx = ((int) (pos.x / 16)) + i;
					cy = ((int) (pos.y / 16)) + j;
					cz = ((int) (pos.z / 16)) + k;
					
					ccx = cx*16;
					ccy = cy*16;
					ccz = cz*16;
					
					if (!camera.cubeInFrustum(ccx, ccy, ccz, ccx+16, ccy+16, ccz+16))
						continue;
					
					Chunk c = world.getChunk(cx, cy, cz);
					if (c != null) {
						c.lights.iterate((NdHashMap<Integer, Byte> dt, Integer k1, Integer k2, Integer k3, Byte v1) -> {
							gshader.loadLightData(ll, (float) (k1 - pos.x), (float)(k2 - pos.y), (float) (k3 - pos.z));
							ll++;
						});
					}
				}
			}
		}
		gshader.loadLightAmount(ll);
		
		ScreenManager.uiRenderer.renderQuad();
		
		ScreenManager.uiRenderer.stopRenderQuad();
		
		gshader.stop();
		
		
		ScreenManager.disableCulling();
		ScreenManager.disableTransparentcy();
		
		return super.render();
	}
	
	@Override
	public Map<String, List<UIText>> renderText() {
		return super.renderText();
	}
	
	@Override
	public void close() {
		super.close();
		for (int i = 0; i < world.generatorThreads.size(); i++) {
			world.generatorThreads.get(i).interrupt();
		}
	}
	
	@Override
	public void onLeave() {
		world.save();
		menus.remove(DebugInfo.destroy());
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
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA16, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, gPosition, 0);
		
		gNormal = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gNormal);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA16, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, gNormal, 0);
		
		gColorSpec = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gColorSpec);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, gColorSpec, 0);

		IntBuffer drawBuffers = BufferUtils.createIntBuffer(3);
		drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL30.GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL30.GL_COLOR_ATTACHMENT2);
		
		drawBuffers.flip();
		GL20.glDrawBuffers(drawBuffers);
		
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
		if (button == 0) {
			MouseBlockPicker.getBlockMine(world, camera, 6, Block.AIR);
		} else if (button == 1)
			MouseBlockPicker.getBlockPlace(world, camera, 6, Block.GLOWSTONE);
	}

	@Override
	public void onMouseReleased(int button) {
	}
	
}
