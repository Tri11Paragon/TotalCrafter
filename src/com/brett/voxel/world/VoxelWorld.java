package com.brett.voxel.world;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.brett.IMouseState;
import com.brett.KeyMaster;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.networking.Client;
import com.brett.voxel.renderer.ScreenShot;
import com.brett.voxel.renderer.VEntityRenderer;
import com.brett.voxel.renderer.VOverlayRenderer;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.lighting.LightingEngine;
import com.brett.voxel.world.player.Player;
import com.brett.world.cameras.ICamera;

/**
*
* @author brett
* @date Feb. 14, 2020
*/

public class VoxelWorld extends IWorldProvider implements IMouseState {
	
	public static final float GRAVITY = 9.8f;
	// is this a remote server?
	public static boolean isRemote = false;
	public static Client localClient;
	
	private VoxelShader shader;
	private Loader loader;
	private MouseBlockPicker picker;
	private MasterRenderer renderer;
	private VEntityRenderer vrend;
	
	private VOverlayRenderer voverlayrenderer;
	
	private int textureAtlas;
	
	// replace pi with a player (done)
	public VoxelWorld(MasterRenderer renderer, Loader loader, Player ply) {
		this.loader = loader;
		this.renderer = renderer;
		this.ply = ply;
		vrend = new VEntityRenderer(this, renderer, ply, loader);
	}
	
	public void init() {
		shader = new VoxelShader();
		ply.assignWorld(this);
		ply.init();
		KeyMaster.registerKeyRequester(ply.getInventory());
		chunk = new ChunkStore(ply, loader, this);
		random.setSeed(LevelLoader.seed);
		LightingEngine.init(this, ply);
		this.textureAtlas = loader.loadSpecialTextureATLAS(16, 16);
		this.voverlayrenderer = new VOverlayRenderer(renderer, ply, this);
		picker = new MouseBlockPicker(ply, renderer.getProjectionMatrix(), this, ply, this.voverlayrenderer);
		if (!isRemote)
			tickTileEnts();
		KeyMaster.registerKeyRequester(new ScreenShot());
		KeyMaster.registerMouseRequester(this);
		if (localClient != null)
			localClient.world = this;
	}
	
	public void render(ICamera camera) {
		MasterRenderer.enableCulling();
		MasterRenderer.enableTransparentcy();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		vrend.render();
		shader.start();
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
			chunk.renderChunks(shader, renderer.getProjectionMatrix());
		shader.stop();
		MasterRenderer.disableCulling();
		MasterRenderer.disableTransparentcy();
	}
	
	public void createExplosion(float x, float y, float z, float size) {
		new Explosion(x, y, z, size, this).explode();
	}
	
	public void tickTileEnts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long start = 0;
				long end = 0;
				long ticksSkiped = 1;
				while (VoxelScreenManager.isOpen) {
					start = System.currentTimeMillis();
					long time = start - end;
					if (time > 50) {
						ticksSkiped = time / 50;
						if (ticksSkiped < 1)
							ticksSkiped = 1;
					}
					for (int i = 0; i < tents.size(); i++) {
						tents.get(i).tick(ticksSkiped);
					}
					chunk.updateChunks();
					end = System.currentTimeMillis();
					if (start - end < 50) {
						try {
							Thread.sleep(50 - (start-end));
						} catch (InterruptedException e) {}
					}
				}
			}
		}).start();
		
	}
	
	public void update() {
		picker.update();		
		for (int i = 0; i < tents.size(); i++) {
			tents.get(i).renderUpdate();
		}
		/*while (Mouse.next()){
			if (Mouse.getEventButtonState()) {
				
				//if (Mouse.getEventButton() == 2) {
				//	picker.test();
				//}
			}
		}*/
	}

	@Override
	public void onMousePressed() {
		if (Mouse.getEventButton() == 1) {
			PlayerInventory i = ply.getInventory();
			ItemStack st = i.getItemInSelectedSlot();
			if (st != null) {
				if (picker.placeBlock(Item.inverseItems.get(st.getItem()))) {
					i.getSelectedSlot().removeItems(1);
					i.getSelectedSlot().updateText();
				}
				if (st.getItem() instanceof ItemTool) {
					int[] c = picker.getCurrentBlockPoF();
					((ItemTool) st.getItem()).onRightClick(c[0], c[1], c[2], this, ply, i);
				}
					
			} else {
				picker.placeBlock((short) 0);
			}
		}
		if (Mouse.getEventButton() == 0) {
			PlayerInventory i = ply.getInventory();
			ItemStack st = i.getItemInSelectedSlot();
			if (st != null) {
				if (st.getItem() instanceof ItemTool) {
					int[] c = picker.getCurrentBlockPoF();
					((ItemTool) st.getItem()).onLeftClick(c[0], c[1], c[2], this, ply, i);
				}
			}
		}
		if (Mouse.getEventButton() == 2) {
			
		}
	}
	
	public Loader getLoader() {
		return loader;
	}
	
	public void cleanup() {
		if (chunk != null)
			chunk.cleanup();
		for (int i = 0; i < tents.size(); i++) {
			tents.get(i).save();
		}
		LevelLoader.saveLevelData(ChunkStore.worldLocation);
		if (shader != null)
			shader.cleanUp();
	}

	@Override
	public void onMouseReleased() {
	}
	
}
