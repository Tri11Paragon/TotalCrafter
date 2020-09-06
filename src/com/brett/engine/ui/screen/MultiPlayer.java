package com.brett.engine.ui.screen;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.brett.engine.DebugInfo;
import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.data.IMouseState;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.GBufferShader;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.shaders.VoxelShader;
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
import com.brett.networking.ServerConnection;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.tools.MouseBlockPicker;
import com.brett.world.tools.RayCasting;

/**
* @author Brett
* @date 4-Sep-2020
*/

public class MultiPlayer extends Screen implements IMouseState {
	
	public static Matrix4f chunkViewMatrix;
	public static Matrix4f viewMatrix;
	public CreativeCamera camera;
	public int textureAtlas;
	public World world;
	public VoxelShader shader;
	public GBufferShader gshader;
	public UIMenu console;
	public boolean running = false;
	public ServerConnection sc;
	
	public MultiPlayer(UIMenu console) {
		this.console = console;
	}
	
	@Override
	public void onSwitch() {
		running = true;
		super.onSwitch();
		InputMaster.mouse.add(this);
		elements.add(new UITexture(ScreenManager.loader.loadTexture("crosshair"), -2, -2, 0, 0, 16, 16, AnchorPoint.CENTER));
		
		sc = ServerConnection.connectToServer("paragonscode.ddns.net", 1337);
		world = new World(sc);
		camera = new CreativeCamera(new Vector3d(0,140,0), world, sc);
		
		menus.add(console);
		menus.add(DebugInfo.init(camera));
		
		Console.registerCommand(new String[] {"tp", "teleport"}, new TeleportCommand(camera));
		Console.registerCommand(new String[] {"poly", "pmode", "rend.p", "renderer.p"}, new PolygonCommand());
		
		shader = new VoxelShader();
		gshader = new GBufferShader();
		shader.start();
		shader.loadProjectionMatrox(ProjectionMatrix.projectionMatrix);
		shader.stop();
		gshader.start();
		gshader.connectTextureUnits();
		gshader.loadProjectionMatrox(ProjectionMatrix.projectionMatrix);
		gshader.stop();
	}
	
	private float ccx,ccy,ccz;
	private int cx,cy,cz;
	private Vector3d pos;
	private long lastCheckTime = 0;
	private boolean checked = false;
	
	@Override
	public List<UIElement> render() {
		camera.move();
		chunkViewMatrix = Maths.createViewMatrixROT(camera);
		viewMatrix = Maths.createViewMatrix(camera);
		camera.calculateFrustum(ProjectionMatrix.projectionMatrix, viewMatrix);
		RayCasting.update(camera);
		
		ScreenManager.enableCulling();
		ScreenManager.enableTransparentcy();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
		
		shader.start();
		shader.loadViewMatrix(chunkViewMatrix);
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
						world.queueChunk(cx, cy, cz);
					else {
						c.render(shader, i, j, k);
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
		
		shader.stop();
		
		
		ScreenManager.disableCulling();
		ScreenManager.disableTransparentcy();
		
		return super.render();
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
		menus.remove(DebugInfo.destroy());
		InputMaster.mouse.remove(this);
		try {
			sc.fromServer.close();
			sc.toServer.close();
			sc.close();
		} catch (Exception e) {}
		super.onLeave();
	}

	@Override
	public void onMouseReleased(int button) {
	}
	
}
