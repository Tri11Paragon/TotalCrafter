package com.brett.renderer.world;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
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
	
	public static int x = 16;
	public static int y = 128;
	public static int z = 16;
	
	private int[][][] blocks = new int[x][y][z];
	private RawModel[][][] blocksModels = new RawModel[x][y][z];
	List<Block> bls = new ArrayList<Block>();
	private int xoff,zoff;
	
	public static RawModel fullBlock;
	public static RawModel emptyBlock;
	
	public Chunk(Loader loader, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		Chunk.fullBlock = loader.loadToVAO(MeshStore.verts, MeshStore.uv, MeshStore.indicies);
		Chunk.emptyBlock = loader.loadToVAO(MeshStore.vertsNONE, MeshStore.uvNONE, MeshStore.indiciesNONE);
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					if (j == 70) {
						blocks[i][j][k] = 3;
						blocksModels[i][j][k] = fullBlock;
					} else if (j <= 69 && j >= 60) {
						blocks[i][j][k] = 2;
						blocksModels[i][j][k] = fullBlock;
					} else if (j < 60) {
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
				//System.out.println("Mesher Thread Start");
				for (int i =0; i < x; i++) {
					for (int j = 0; j < y; j++) {
						for (int k = 0; k < z; k++) {
							mesh(i,j,k);
						}
					}
				}
				//System.out.println("Mesher Thread Dead");
			}
		}).start();
	}
	
	public Chunk(Loader loader, int[][][] blocks, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		Chunk.fullBlock = loader.loadToVAO(MeshStore.verts, MeshStore.uv, MeshStore.indicies);
		Chunk.emptyBlock = loader.loadToVAO(MeshStore.vertsNONE, MeshStore.uvNONE, MeshStore.indiciesNONE);
		
		this.blocks = blocks;
		
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					blocksModels[i][j][k] = fullBlock;
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
	public void mesh(int i, int j, int k) {
		if(blocks[i][j][k] == 0) {
			blocksModels[i][j][k] = emptyBlock;
			return;
		}
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
				//System.out.println("Remesher Thread Start");
				for (int i =0; i < blocks.length; i++) {
					for (int j = 0; j < blocks[i].length; j++) {
						for (int k = 0; k < blocks[i][j].length; k++) {
							mesh(i,j,k);
						}
					}
				}
				//System.out.println("Remesher Thread Dead");
			}
		}).start();
	}
	
	public void remesh(Chunk l, Chunk r, Chunk f, Chunk b) {
		new Thread(new Runnable() {		
			@Override
			public void run() {
				//System.out.println("Remesher Thread Start");
				for (int i =0; i < blocks.length; i++) {
					for (int j = 0; j < blocks[i].length; j++) {
						for (int k = 0; k < blocks[i][j].length; k++) {
							chunkRemesh(i, j, k, l, r, f, b);
						}
					}
				}
				//System.out.println("Remesher Thread Dead");
			}
		}).start();
	}
	
	public void remeshSpecial(int i, int j, int k, int side) {
		if(blocks[i][j][k] == 0) {
			blocksModels[i][j][k] = emptyBlock;
			return;
		}
		boolean top = true;
		boolean bottom = true;
		boolean left = true;
		boolean right = true;
		boolean front = true;
		boolean back = true;
		if (side != 0) {
			try {
				if (blocks[i][j + 1][k] != 0) {
					top = false;
				}
			} catch (IndexOutOfBoundsException e) {
				top = false; // we can assume that at chunk bounds there is going to be another chunk. anyways the chunk mesh will handle this.
			}
		}
		if (side != 1) {
			try {
				if (blocks[i][j - 1][k] != 0) {
					bottom = false;
				}
			} catch (IndexOutOfBoundsException e) {bottom = false;}
		}
		if (side != 2) {
			try {
				if (blocks[i - 1][j][k] != 0) {
					left = false;
				}
			} catch (IndexOutOfBoundsException e) {left = false;}
		}
		if (side != 3) {
			try {
				if (blocks[i + 1][j][k] != 0) {
					right = false;
				}
			} catch (IndexOutOfBoundsException e) {right = false;}
		}
		if (side != 4) {
			try {
				if (blocks[i][j][k + 1] != 0) {
					front = false;
				}
			} catch (IndexOutOfBoundsException e) {front = false;}
		}
		if (side != 5) {
			try {
				if (blocks[i][j][k - 1] != 0) {
					back = false;
				}
			} catch (IndexOutOfBoundsException e) {back = false;}
		}
		
		blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(new SixBoolean(top, bottom, left, right, front, back)));
	}
	
	//TODO: this
	private void chunkRemesh(int i, int j, int k, Chunk l, Chunk r, Chunk f, Chunk b) {
		if(blocks[i][j][k] == 0) {
			blocksModels[i][j][k] = emptyBlock;
			return;
		}
		boolean top = true;
		boolean bottom = true;
		boolean left = true;
		boolean right = true;
		boolean front = true;
		boolean back = true;
		try {
			if (blocks[i][j + 1][k] != 0) {top = false;}
		} catch (IndexOutOfBoundsException e) {}
		try {
			if (blocks[i][j - 1][k] != 0) {bottom = false;}
		} catch (IndexOutOfBoundsException e) {bottom = false;}
		try {
			if (blocks[i + 1][j][k] != 0) {right = false;}
		} catch (IndexOutOfBoundsException e) {
			try {
				if(r.blocks[0][j][k] != 0) {
					right = false;
				} 
			} catch (Exception d) {right = false;}
		}
		try {
			if (blocks[i - 1][j][k] != 0) {left = false;}
		} catch (IndexOutOfBoundsException e) {
			try {
				if (l.blocks[Chunk.x-1][j][k] != 0) {
					left = false;
				}
			} catch (Exception d) {left = false;}
		}
		try {
			if (blocks[i][j][k + 1] != 0) {front = false;}
		} catch (IndexOutOfBoundsException e) {
			try {
				if (f.blocks[i][j][0] != 0) {
					front = false;
				}
			} catch (Exception d) {front = false;}
		}
		try {
			if (blocks[i][j][k - 1] != 0) {back = false;}
		} catch (IndexOutOfBoundsException e) {
			try {
				if (b.blocks[i][j][Chunk.z-1] != 0) {
					back = false;
				}
			} catch (Exception d) {back = false;}
		}
		
		blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(new SixBoolean(top, bottom, left, right, front, back)));
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
					Matrix4f transformationMatrix = Maths.createTransformationMatrixCube(i+(x*xoff),j,k+(z*zoff));
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
	
	public int getBlock(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return blocks[x][y][z];
	}
	
	public Block getBlockE(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return null;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return Block.blocks.get(getBlock(x, y, z));
	}
	
	public boolean isBlockUnderGround(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z)
			return false;
		if (y < 0)
			return true;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return blocks[x][y + 1][z] == 0 ? false : true;
	}
	
	public int getHeight(int x, int z) {
		if (x >= Chunk.x || y <= 0 || z >= Chunk.z)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y > Chunk.y)
			return Chunk.y;
		for (int i = 0; i < 128; i++) {
			if (blocks[x][i][z] == 0) {
				return i-1;
			}
		}
		return 0;
	}
	
	public int[][][] getBlocks(){
		return blocks;
	}
	
	public void setBlock(int x, int y, int z, int block) {
		if (x >= Chunk.x || z >= Chunk.z)
			return;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y > Chunk.y)
			y = Chunk.y;
		if (y < 0)
			y = 0;
		if (block == 0)
			blocksModels[x][y][z] = emptyBlock;
		blocks[x][y][z] = block;
	}
	
	// locking prevents race conditions!
	// right?
	// i did this right
	// if its not it works so i don't care.
	
	public void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}
