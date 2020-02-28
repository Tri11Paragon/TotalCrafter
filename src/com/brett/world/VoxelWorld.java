package com.brett.world;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.renderer.world.MeshStore;
import com.brett.tools.MouseBlockPicker;
import com.brett.world.cameras.Camera;
import com.tester.Main;

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
	
	public VoxelWorld(MasterRenderer renderer, Loader loader, Camera cam) {
		this.loader = loader;
		shader = new VoxelShader();
		shader.start();
		shader.loadProjectionMatrix(renderer.getProjectionMatrix());
		shader.stop();
		resolveMeshes();
		chunk = new ChunkStore(cam, loader);
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				chunk.setChunk(new Chunk(loader, i, j), i, j);
			}
		}
		
		// reduces ram at cost of CPU
		// not much anymore but at a time
		// this freed much ram
		new Thread(new Runnable() {		
			@Override
			public void run() {
				while (Main.isOpen) {
					System.gc();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		picker = new MouseBlockPicker(cam, renderer.getProjectionMatrix(), this);
	}
	
	public void render(Camera camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		picker.update();
		MasterRenderer.enableCulling();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		/*Iterator<Entry<Coord, Chunk>> ic = chunks.entrySet().iterator();
		while (ic.hasNext()) {
			Entry<Coord, Chunk> w = ic.next();
			w.getValue().render(shader);
		}*/
		chunk.renderChunks(shader);
		
		while (Mouse.next()){
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					picker.setCurrentBlockPointNEW(0);
					Vector3f d = picker.getCurrentTerrainPoint();
					if (d == null)
						return;
					int dx = (int)(d.x)/Chunk.x;
					int dz = (int)(d.z)/Chunk.z;
					Chunk c = chunk.getChunk(dx, dz);
					if (c == null)
						return;
					c.remesh(chunk.getChunk(dx-1, dz), chunk.getChunk(dx+1, dz), chunk.getChunk(dx, dz+1), chunk.getChunk(dx, dz-1));
				}
			}
		}
		
		//System.gc();
		
		MasterRenderer.disableCulling();
		shader.stop();
	}
	
	public void resolveMeshes() {
		for (int t = 0; t < 2; t++) {
			for (int l = 0; l < 2; l++) {
				for (int r = 0; r < 2; r++) {
					for (int f = 0; f < 2; f++) {
						for (int b = 0; b < 2; b++) {
							for (int u = 0; u < 2; u++) {
								// i don't like this.
								// trust me i tried for loop booleans
								SixBoolean boo = createSixBooleans(new SixBoolean(t == 0 ? false : true, u == 0 ? false : true, l == 0 ? false : true, r == 0 ? false : true, 
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
								if (t == 0 && l == 0 && r == 0 && f == 0 && b == 0 && u == 0)
									MeshStore.boolEmpty = m.getVaoID();
								MeshStore.models.put(boo, m);
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
		shader.cleanUp();
	}
	
}
