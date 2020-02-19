package com.brett.renderer.world;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.SixBoolean;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.tools.Maths;
import com.brett.world.VoxelWorld;
import com.brett.world.blocks.Block;

/**
*
* @author brett
*
*
*/

public class Chunk {
	
	public static float FOV = 90;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	private int x = 16;
	private int y = 128;
	private int z = 16;
	
	private int[][][] blocks = new int[x][y][z];
	private RawModel[][][] blocksModels = new RawModel[x][y][z];
	List<Block> bls = new ArrayList<Block>();
	private Vector3f offset;
	
	public static RawModel fullBlock;
	public static RawModel emptyBlock;
	
	public Chunk(Loader loader, Vector3f offset) {
		this.offset = offset;
		Chunk.fullBlock = loader.loadToVAO(MeshStore.verts, MeshStore.uv, MeshStore.indicies);
		Chunk.emptyBlock = loader.loadToVAO(MeshStore.vertsNONE, MeshStore.uvNONE, MeshStore.indiciesNONE);
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					if (j < 60) {
						// TODO: make use master block.
						// blocks should only have a position and nothing more
						// hopefully will use less ram
						// maybe don't store position in the block but use array and the offset
						blocks[i][j][k] = 1;
						blocksModels[i][j][k] = fullBlock;
					} else {
						blocks[i][j][k] = 0;
						blocksModels[i][j][k] = emptyBlock;
					}
				}
			}	
		}
		
		new Thread(new Runnable() {		
			@Override
			public void run() {
				System.out.println("Mesher Thread Start");
				for (int i =0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						for (int k = 0; k < z; k++) {
							mesh(i,j,k);
						}
					}
				}
				System.out.println("Mesher Thread Dead");
			}
		}).start();
	}
	
	// don't look at this please
	// it is not good.
	// TODO: improve this
	// this is rough code
	private void mesh(int i, int j, int k) {
		if (blocks[i][j][k] == 0)
			return;
		boolean top = true;
		boolean bottom = true;
		boolean left = true;
		boolean right = true;
		boolean front = true;
		boolean back = true;
		try {
			if (blocks[i][j + 1][k] != 0) {
				top = false;
			}
		} catch (IndexOutOfBoundsException e) {
			top = false; // we can assume that at chunk bounds there is going to be another chunk. anyways the chunk mesh will handle this.
		}
		try {
			if (blocks[i][j - 1][k] != 0) {
				bottom = false;
			}
		} catch (IndexOutOfBoundsException e) {bottom = false;}
		try {
			if (blocks[i + 1][j][k] != 0) {
				right = false;
			}
		} catch (IndexOutOfBoundsException e) {right = false;}
		try {
			if (blocks[i - 1][j][k] != 0) {
				left = false;
			}
		} catch (IndexOutOfBoundsException e) {left = false;}
		try {
			if (blocks[i][j][k + 1] != 0) {
				front = false;
			}
		} catch (IndexOutOfBoundsException e) {front = false;}
		try {
			if (blocks[i][j][k - 1] != 0) {
				back = false;
			}
		} catch (IndexOutOfBoundsException e) {back = false;}
		
		blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(new SixBoolean(top, bottom, left, right, front, back)));
	}
	
	public void remesh() {
		new Thread(new Runnable() {		
			@Override
			public void run() {
				System.out.println("Remesher Thread Start");
				for (int i =0; i < blocks.length; i++) {
					for (int j = 0; j < blocks[i].length; j++) {
						for (int k = 0; k < blocks[j][j].length; k++) {
							mesh(i,j,k);
						}
					}
				}
				System.out.println("Remesher Thread Dead");
			}
		}).start();
	}
	
	//TODO: this
	public void chunkRemesh() {
		
	}
	
	public void render(VoxelShader shader) {
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					if (blocks[i][j][k] == 0)
						continue;
					
					// make this based on texture id?
					ModelTexture model = Block.blocks.get(blocks[i][j][k]).model;
					RawModel rawModel = blocksModels[i][j][k];
					
					if (rawModel == null)
						continue;
					
					if (rawModel.getVaoID() == MeshStore.boolEmpty)
						continue;
					
					
					GL30.glBindVertexArray(rawModel.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					Matrix4f transformationMatrix = Maths.createTransformationMatrixCube(i+offset.x,j+offset.y,k+offset.z);
					shader.loadTransformationMatrix(transformationMatrix);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getID());
					GL11.glDrawElements(MasterRenderer.DRAWMODE, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					GL20.glDisableVertexAttribArray(0);
					GL20.glDisableVertexAttribArray(1);
					GL30.glBindVertexArray(0);
				}
			}
		}
		
	}
	
	// locking prevents race conditions!
	// right?
	// i did this right
	// if its not it works so i don't care.
	
	public Vector3f getOffset() {
		return offset;
	}
	
	public void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}
