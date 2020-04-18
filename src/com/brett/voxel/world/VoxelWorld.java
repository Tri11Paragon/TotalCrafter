package com.brett.voxel.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.VOverlayRenderer;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.chunk.ChunkStore;
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
	public Random random = new Random();
	public HashMap<RawBlockModel, HashMap<Integer, List<float[]>>> models = new HashMap<RawBlockModel, HashMap<Integer, List<float[]>>>();
	private List<TileEntity> tents = new ArrayList<TileEntity>();
	protected PlayerInventory i;
	
	// replace pi with a player
	public VoxelWorld(MasterRenderer renderer, Loader loader, Camera cam, PlayerInventory i) {
		this.loader = loader;
		shader = new VoxelShader();
		resolveMeshes();
		shader.start();
		shader.loadProjectionMatrix(renderer.getProjectionMatrix());
		shader.stop();
		Chunk.fullBlock = RawBlockModel.convertRawModel(loader.loadToVAO(MeshStore.verts, MeshStore.uv, MeshStore.indicies));
		Chunk.emptyBlock = RawBlockModel.convertRawModel(loader.loadToVAO(MeshStore.vertsNONE, MeshStore.uvNONE, MeshStore.indiciesNONE));
		chunk = new ChunkStore(cam, loader, this);
		random.setSeed(LevelLoader.seed);
		LightingEngine.init(this);
		this.i = i;
		this.cam = cam;
		
		// reduces ram at cost of CPU
		// not much anymore but at a time
		// this freed much ram
		new Thread(new Runnable() {		
			@Override
			public void run() {
				long lastTime = 0;
				while (VoxelScreenManager.isOpen) {
					if (Sys.getTime() - lastTime < 100)
						continue;
					for (int i = -ChunkStore.renderDistance; i < ChunkStore.renderDistance; i++) {
						for (int k = -ChunkStore.renderDistance; k < ChunkStore.renderDistance; k++) {
							int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
							int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
							try {
								Chunk c = chunk.getChunk(cx, cz);
								if (c == null)
									continue;
								c.remeshNo();
							} catch (Exception e) {}
						}
					}
					lastTime = Sys.getTime();
				}
			}
		}).start();
		this.voverlayrenderer = new VOverlayRenderer(renderer, cam, this);
		picker = new MouseBlockPicker(cam, renderer.getProjectionMatrix(), this, i, this.voverlayrenderer);
	}
	
	public void render(ICamera camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		MasterRenderer.enableCulling();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			chunk.renderChunks(shader);
			/**
			 * Hello kedwell! This code below is very important for a game like this
			 * its something called batched rendering. Basically changing the model and texture of what you
			 * are rendering for every model is VERY expensive. (changing the vertex array was taking up the majority of the CPU time according to my profiler)
			 * So batch rendering fixes this but only changing what model we are using after we have rendered all the blocks for a particular model.
			 * batch rendering improved FPS by a ton. looking down at world origin would run about 60fps on my beefy computer and now it runs at over 165fps
			 */
			for (RawBlockModel model : models.keySet()) {
				// get the model maps
				HashMap<Integer, List<float[]>> maps = models.get(model);
				// bind the model to the GPU
				GL30.glBindVertexArray((int)model.getVaoID());
				// enable these vertex arrays (0 and 1)
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				// loop through all textures associated with this model
				for (int tex : maps.keySet()) {
					// bind the texture
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
					// loop through all the blocks
					List<float[]> s = maps.get(tex);
					for (int i = 0; i < s.size(); i++) {
						// get the pos
						float[] pos = s.get(i);
						// create the transformation matrix using the best matrix for cubes the cube matrix
						shader.loadTransformationMatrix(Maths.createTransformationMatrixCube(pos[0],pos[1],pos[2]));
						// load lighting data
						shader.loadLightData(pos[3], pos[4], pos[5], pos[6], pos[7], pos[8]);
						// draw the bound model. NOTE: 0x4 means GL_Triangles in opengl speak and is required if you are using lighting
						GL11.glDrawElements(0x4, (int)model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					}
				}
				// disable arrays and unbind the current model.
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL30.glBindVertexArray(0);
			}
			//This method of batch rendering could be improved as this has increased ram usage by about 0.2gb
			//(such has not clearing the model array and instead only modifying it if a block has changed)
			models.clear();
		MasterRenderer.disableCulling();
		shader.stop();
	}
	
	public void addBlock(RawBlockModel model, int texture, float[] pos) {
		HashMap<Integer, List<float[]>> map = models.get(model);
		if (map == null) {
			HashMap<Integer, List<float[]>> newmap = new HashMap<Integer, List<float[]>>();
			List<float[]> newarr = new ArrayList<float[]>();
			newarr.add(pos);
			newmap.put(texture, newarr);
			models.put(model, newmap);
		} else {
			List<float[]> poses = map.get(texture);
			if (poses == null) {
				List<float[]> newarr = new ArrayList<float[]>();
				newarr.add(pos);
				map.put(texture, newarr);
			} else {
				poses.add(pos);
			}
		}
	}
	
	public void createExplosion(float x, float y, float z, float size) {
		new Explosion(x, y, z, size, this).explode();;
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
			}
		}
		/*if (Mouse.getEventButton() == 0) {
			int id = picker.mineBlock();
			if (id != 0) {
				i.addItemToInventory(new ItemStack(
						Item.items.get(Block.blocks.get((short)id).getBlockDropped()), 
								Block.blocks.get((short)id).getAmountDropped()));
			}
			Vector3f d = picker.getCurrentTerrainPoint();
			if (d == null)
				return;
			int dx = (int)(d.x)/Chunk.x;
			int dz = (int)(d.z)/Chunk.z;
			Chunk c = chunk.getChunk(dx, dz);
			if (c == null)
				return;
			c.remesh();
		}*/
	}
	//TODO move this to the mesh store.
	byte pos = 0;
	public void resolveMeshes() {
		for (int t = 0; t < 2; t++) {
			for (int l = 0; l < 2; l++) {
				for (int r = 0; r < 2; r++) {
					for (int f = 0; f < 2; f++) {
						for (int b = 0; b < 2; b++) {
							for (int u = 0; u < 2; u++) {
								// i don't like this.
								// trust me i tried for loop booleans
								SixBoolean boo = VoxelWorld.createSixBooleans(new SixBoolean(t == 0 ? false : true, u == 0 ? false : true, l == 0 ? false : true, r == 0 ? false : true, 
										f == 0 ? false : true, b == 0 ? false : true));
								int[] ind = {};
								// TODO: maybe not use index arrays?
								// doesn't really do much btw - future me
								// cause like its not saving much if you have to load full vertex models in.
								if (t == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesTOP);}
								if (l == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesLEFT);}
								if (r == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesRIGHT);}
								if (f == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesFRONT);}
								if (b == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesBACK);}
								if (u == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesBOTTOM);}
								RawModel m = loader.loadToVAO(MeshStore.verts, MeshStore.uv, ind);
								if (t == 0 && l == 0 && r == 0 && f == 0 && b == 0 && u == 0)
									MeshStore.boolEmpty = m.getVaoID();
								MeshStore.models.put(boo, RawBlockModel.convertRawModel(m));
								pos++;
							}
						}	
					}	
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
	
	public static synchronized SixBoolean createSixBooleans(SixBoolean boo) {
		SixBoolean b = boo;
		for (int i = 0; i < MeshStore.booleans.size(); i++) {
			if (MeshStore.booleans.get(i).isYes(boo)){
				b = MeshStore.booleans.get(i);
				return b;
			}
		}
		MeshStore.booleans.add(b);
		return b;
	}
	
	public static synchronized SixBoolean createSixBooleans(boolean left, boolean right, boolean front, boolean back, boolean top, boolean bottom) {
		SixBoolean b;
		for (int i = 0; i < MeshStore.booleans.size(); i++) {
			if (MeshStore.booleans.get(i).isYes(top, bottom, left, right, front, back)){
				b = MeshStore.booleans.get(i);
				return b;
			}
		}
		b = new SixBoolean(top, bottom, left, right, front, back);
		MeshStore.booleans.add(b);
		return b;
	}
	
	public int[] addArrays(int[] array1, int[] array2) {
		int[] rtv = new int[array1.length + array2.length];
		
		for (int i = 0; i<array1.length;i++) {
			rtv[i] = array1[i];
		}
		
		for (int i = 0; i<array2.length; i++) {
			rtv[i + array1.length] = array2[i];
		}
		
		return rtv;
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
