package com.brett.voxel.world;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.InventoryMaster;
import com.brett.voxel.tools.MouseBlockPicker;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.items.Item;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Feb. 14, 2020
*/

public class VoxelWorld {
	
	private VoxelShader shader;
	private Loader loader;
	
	private MouseBlockPicker picker;
	public ChunkStore chunk;
	public Random random = new Random();
	
	public VoxelWorld(MasterRenderer renderer, Loader loader, Camera cam) {
		this.loader = loader;
		Block.registerBlocks(loader);
		Item.registerItems(loader);
		InventoryMaster.init(loader);
		shader = new VoxelShader();
		resolveMeshes();
		shader.start();
		shader.loadProjectionMatrix(renderer.getProjectionMatrix());
		shader.stop();
		Chunk.fullBlock = RawBlockModel.convertRawModel(loader.loadToVAO(MeshStore.verts, MeshStore.uv, MeshStore.indicies));
		Chunk.emptyBlock = RawBlockModel.convertRawModel(loader.loadToVAO(MeshStore.vertsNONE, MeshStore.uvNONE, MeshStore.indiciesNONE));
		chunk = new ChunkStore(cam, loader, this);
		random.setSeed(LevelLoader.seed);
		
		// reduces ram at cost of CPU
		// not much anymore but at a time
		// this freed much ram
		new Thread(new Runnable() {		
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					//System.gc();
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
					/*try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				}
			}
		}).start();
		/*new Thread(new Runnable() {		
			@Override
			public void run() {
				while (Main.isOpen) {
					System.gc();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();*/
		picker = new MouseBlockPicker(cam, renderer.getProjectionMatrix(), this);
	}
	
	public void render(Camera camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		MasterRenderer.enableCulling();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			chunk.renderChunks(shader);
		MasterRenderer.disableCulling();
		shader.stop();
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
		while (Mouse.next()){
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					picker.setCurrentBlockPoint(0);
					Vector3f d = picker.getCurrentTerrainPoint();
					if (d == null)
						return;
					int dx = (int)(d.x)/Chunk.x;
					int dz = (int)(d.z)/Chunk.z;
					Chunk c = chunk.getChunk(dx, dz);
					if (c == null)
						return;
					c.remesh();
				}
				if (Mouse.getEventButton() == 1) {
					picker.placeBlock(1);
				}
			}
		}
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
								float[] verts = {};
								float[] uvs = {};
								// TODO: maybe not use index arrays?
								// cause like its not saving much if you have to load full vertex models in.
								if (t == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesTOP); verts = addArrays(verts, MeshStore.vertsTop); uvs = addArrays(uvs, MeshStore.uvTop);}
								if (l == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesLEFT); verts = addArrays(verts, MeshStore.vertsLeft); uvs = addArrays(uvs, MeshStore.uvLeft);}
								if (r == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesRIGHT); verts = addArrays(verts, MeshStore.vertsRight); uvs = addArrays(uvs, MeshStore.uvRight);}
								if (f == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesFRONT); verts = addArrays(verts, MeshStore.vertsFront); uvs = addArrays(uvs, MeshStore.uvFront);}
								if (b == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesBACK); verts = addArrays(verts, MeshStore.vertsBack); uvs = addArrays(uvs, MeshStore.uvBack);}
								if (u == 0 ? false : true) {ind = addArrays(ind, MeshStore.indiciesBOTTOM); verts = addArrays(verts, MeshStore.vertsBottom); uvs = addArrays(uvs, MeshStore.uvBottom);}
								RawModel m = loader.loadToVAO(MeshStore.verts, MeshStore.uv, ind);
								//RawModel m = loader.loadToVAO(verts, 3, uvs);
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
	
	public float[] addArrays(float[] array1, float[] array2) {
		float[] rtv = new float[array1.length + array2.length];
		
		for (int i = 0; i<array1.length;i++) {
			rtv[i] = array1[i];
		}
		
		for (int i = 0; i<array2.length; i++) {
			rtv[i + array1.length] = array2[i];
		}
		
		return rtv;
	}
	
	public void cleanup() {
		chunk.cleanup();
		shader.cleanUp();
	}
	
}
