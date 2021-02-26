package com.brett.voxel.world;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.brett.cameras.ICamera;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.ProjectionMatrix;
import com.brett.tools.IMouseState;
import com.brett.tools.InputMaster;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.networking.Client;
import com.brett.voxel.renderer.ScreenShot;
import com.brett.voxel.renderer.VEntityRenderer;
import com.brett.voxel.renderer.VOverlayRenderer;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.lighting.LightingEngine;
import com.brett.voxel.world.player.Player;

/**
*
* @author brett
* @date Feb. 14, 2020
*/

public class VoxelWorld extends IWorldProvider implements IMouseState {
	
	private static final long serialVersionUID = 4794008908629628949L;
	
	public static final float GRAVITY = (float) Math.sqrt(9.8f/6f);
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
		// create all the stuff we need for the game.
		shader = new VoxelShader();
		ply.assignWorld(this);
		ply.init();
		InputMaster.keyboard.add(ply.getInventory());
		chunk = new ChunkStore(ply, loader, this);
		// setup the seed
		random.setSeed(LevelLoader.seed);
		LightingEngine.init(this, ply);
		this.textureAtlas = loader.loadSpecialTextureATLAS(16, 16);
		this.voverlayrenderer = new VOverlayRenderer(renderer, ply, this);
		picker = new MouseBlockPicker(ply, ProjectionMatrix.projectionMatrix, this, ply, this.voverlayrenderer);
		if (!isRemote)
			tickTileEnts();
		// screen shot is f2
		InputMaster.keyboard.add(new ScreenShot());
		InputMaster.mouse.add(this);
		if (localClient != null)
			localClient.world = this;
		shader.start();
		shader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		shader.stop();
	}
	
	private long startTime = 0;
	public void render(ICamera camera) {
		// enable stuff we need for rendering
		MasterRenderer.enableCulling();
		MasterRenderer.enableTransparentcy();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		vrend.render();
		shader.start();
			// 2d array because we are using 3d textures
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
			chunk.renderChunks(shader, ProjectionMatrix.projectionMatrix);
		shader.stop();
		MasterRenderer.disableCulling();
		MasterRenderer.disableTransparentcy();
		if (times > 0) {
			// reset the amount of times after 5 seconds
			if (System.currentTimeMillis() - startTime > 5000)
				times = 0;
		}
	}
	
	/**
	 * creates an explosion at a position with a size.
	 */
	public void createExplosion(float x, float y, float z, float size) {
		new Explosion(x, y, z, size, this).explode();
	}
	
	public void tickTileEnts() {
		// update tile entities in another thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				long start = 0;
				long end = 0;
				long ticksSkiped = 1;
				while (VoxelScreenManager.isOpen) {
					// calculate the amount of time required
					// to skip as there has been some lag since last update
					start = System.currentTimeMillis();
					long time = start - end;
					if (time > 50) {
						ticksSkiped = time / 50;
						if (ticksSkiped < 1)
							ticksSkiped = 1;
					}
					// tick all entities
					for (int i = 0; i < tents.size(); i++) {
						tents.get(i).tick(ticksSkiped);
					}
					// update the chunks
					chunk.updateChunks();
					end = System.currentTimeMillis();
					// sleep to only run at a certain amount of updates per second
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
		// update some tile entities
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

	private int times = 0;
	@Override
	public void onMousePressed(int button) {
		if (button ==1) {
			PlayerInventory i = ply.getInventory();
			ItemStack st = i.getItemInSelectedSlot();
			// make sure there is an item in our hand.
			if (st != null) {
				// try to place the block
				if (picker.placeBlock(Item.inverseItems.get(st.getItem()))) {
					i.getSelectedSlot().removeItems(1);
					i.getSelectedSlot().updateText();
				}
				// try to right click with item
				if (st.getItem() instanceof ItemTool) {
					int[] c = picker.getCurrentBlockPoF();
					// do the right click
					((ItemTool) st.getItem()).onRightClick(c[0], c[1], c[2], this, ply, i);
				}
					
			} else {
				// im not sure why this was here so I disabled it.
				// turns out this is so that way blocks get interacted with.
				picker.placeBlock((short) 0);
			}
		}
		if (button == 0) {
			// make sure the item we are holding knows that we have pressed the mouse button
			PlayerInventory i = ply.getInventory();
			ItemStack st = i.getItemInSelectedSlot();
			if (st != null) {
				if (st.getItem() instanceof ItemTool) {
					// hey i found it again!
					// do the left click
					int[] c = picker.getCurrentBlockPoF();
					((ItemTool) st.getItem()).onLeftClick(c[0], c[1], c[2], this, ply, i);
				}
			}
		}
		if (button == 2) {
			// set the timer for reset
			if (times == 0)
				startTime = System.currentTimeMillis();
			times++;
			if (times > 3) {
				// don't want the user exploding their house by accident.
				new Explosion((float)ply.getPosition().x, (float)ply.getPosition().y, (float)ply.getPosition().z, 4, this).explode();
				times = 0;
			}
		}
	}
	
	public Loader getLoader() {
		return loader;
	}
	
	/**
	 * cleans all resources
	 */
	public void cleanup() {
		// save some data
		if (chunk != null)
			chunk.cleanup();
		for (int i = 0; i < tents.size(); i++) {
			tents.get(i).save();
		}
		LevelLoader.saveLevelData(ChunkStore.worldLocation);
		// destroy some shaders
		if (shader != null)
			shader.cleanUp();
	}
	
	@Override
	public void onMouseReleased(int button) {
	}
	
}
