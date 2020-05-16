package com.brett.voxel.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.VEntityRenderer;
import com.brett.voxel.renderer.VOverlayRenderer;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.ChunkStore;
import com.brett.voxel.world.entity.VEntity;
import com.brett.voxel.world.entity.animation.AnimatedModel;
import com.brett.voxel.world.entity.animation.animation.Animation;
import com.brett.voxel.world.entity.animation.loaders.AnimatedModelLoader;
import com.brett.voxel.world.entity.animation.loaders.AnimationLoader;
import com.brett.voxel.world.entity.animation.utils.MyFile;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.lighting.LightingEngine;
import com.brett.voxel.world.tileentity.TileEntity;
import com.brett.world.cameras.Camera;
import com.brett.world.cameras.ICamera;

/**
*
* @author brett
* @date Feb. 14, 2020
*/

public class VoxelWorld {
	
	public static final float GRAVITY = 9.8f;
	
	private VoxelShader shader;
	private Loader loader;
	
	private Camera cam;
	private MouseBlockPicker picker;
	public ChunkStore chunk;
	
	private VOverlayRenderer voverlayrenderer;
	private VEntityRenderer entityRenderer;
	
	public Random random = new Random();
	private List<TileEntity> tents = new ArrayList<TileEntity>();
	protected PlayerInventory i;
	private int textureAtlas;
	
	// replace pi with a player
	public VoxelWorld(MasterRenderer renderer, Loader loader, Camera cam, PlayerInventory i) {
		this.loader = loader;
		shader = new VoxelShader();
		//resolveMeshes();
		shader.start();
		shader.loadProjectionMatrix(renderer.getProjectionMatrix());
		shader.stop();
		chunk = new ChunkStore(cam, loader, this);
		random.setSeed(LevelLoader.seed);
		LightingEngine.init(this, cam);
		this.i = i;
		this.cam = cam;
		this.textureAtlas = loader.loadSpecialTextureATLAS(16,16);
		this.voverlayrenderer = new VOverlayRenderer(renderer, cam, this);
		picker = new MouseBlockPicker(cam, renderer.getProjectionMatrix(), this, i, this.voverlayrenderer);
		entityRenderer = new VEntityRenderer(this, renderer, cam);
		MyFile fi = new MyFile("resources/models");
		AnimatedModel mod = AnimatedModelLoader.loadEntity(new MyFile(fi, "model.dae"), loader, new ModelTexture(loader.loadTexture("clay")));
		Animation animation = AnimationLoader.loadAnimation(new MyFile(fi, "model.dae"));
		entityRenderer.spawnEntity(new VEntity(new Vector3f(0, 90, 0), 0, mod, animation));
	}
	
	public void render(ICamera camera) {
		MasterRenderer.enableCulling();
		MasterRenderer.enableTransparentcy();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			entityRenderer.render();
		shader.start();
		shader.loadViewMatrix(camera);
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
			chunk.renderChunks(shader);
		MasterRenderer.disableCulling();
		MasterRenderer.disableTransparentcy();
		shader.stop();
	}
	
	public void createExplosion(float x, float y, float z, float size) {
		new Explosion(x, y, z, size, this).explode();
	}
	
	public void updateBlocksAround(int x, int y, int z) {
		Block.blocks.get(this.chunk.getBlockBIAS(x, y + 1, z)).onBlockUpdated(x, y + 1, z, this);
		Block.blocks.get(this.chunk.getBlockBIAS(x, y - 1, z)).onBlockUpdated(x, y - 1, z, this);
		Block.blocks.get(this.chunk.getBlockBIAS(x+1, y, z)).onBlockUpdated(x+1, y, z, this);
		Block.blocks.get(this.chunk.getBlockBIAS(x-1, y, z)).onBlockUpdated(x-1, y, z, this);
		Block.blocks.get(this.chunk.getBlockBIAS(x, y, z+1)).onBlockUpdated(x, y, z+1, this);
		Block.blocks.get(this.chunk.getBlockBIAS(x, y, z-1)).onBlockUpdated(x, y, z-1, this);
	}
	
	public void update() {
		picker.update();
		for (int i = 0; i < tents.size(); i++) {
			tents.get(i).tick();
		}
		while (Mouse.next()){
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 1) {
					ItemStack st = i.getItemInSelectedSlot();
					if (st != null) {
						if (picker.placeBlock(Item.inverseItems.get(st.getItem()))) {
							i.getSelectedSlot().removeItems(1);
							i.getSelectedSlot().updateText();
						}
						if (st.getItem() instanceof ItemTool) {
							int[] c = picker.getCurrentBlockPoF();
							((ItemTool) st.getItem()).onRightClick(c[0], c[1], c[2], this, cam, i);
						}
							
					} else {
						picker.placeBlock((short) 0);
					}
				}
				if (Mouse.getEventButton() == 0) {
					ItemStack st = i.getItemInSelectedSlot();
					if (st != null) {
						if (st.getItem() instanceof ItemTool) {
							int[] c = picker.getCurrentBlockPoF();
							((ItemTool) st.getItem()).onLeftClick(c[0], c[1], c[2], this, cam, i);
						}
					}
				}
				if (Mouse.getEventButton() == 2) {
					picker.test();
				}
			}
		}
	}
	
	public void spawnTileEntity(TileEntity e, int x, int y, int z) {
		tents.add(e);
		e.spawnTileEntity(x, y, z, this);
	}
	
	public void destoryTileEntity(TileEntity e) {
		e.destroy();
		if (tents.contains(e))
			tents.remove(e);
	}
	
	public Loader getLoader() {
		return loader;
	}
	
	public void cleanup() {
		chunk.cleanup();
		for (int i = 0; i < tents.size(); i++) {
			tents.get(i).save();
		}
		LevelLoader.saveLevelData(ChunkStore.worldLocation);
		shader.cleanUp();
	}
	
}
