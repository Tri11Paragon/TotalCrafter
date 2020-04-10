package com.brett.voxel.world.chunk;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.tools.Maths;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.world.terrain.noisefunctions.NoiseFunction;

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
	
	private short[][][] blocks = new short[x][y][z];
	private byte[][][] lightLevel = new byte[x][y][z];
	private RawBlockModel[][][] blocksModels = new RawBlockModel[x][y][z];
	List<Block> bls = new ArrayList<Block>();
	private int xoff,zoff;
	private VoxelWorld s;
	private NoiseFunction nf;
	
	public static RawBlockModel fullBlock;
	public static RawBlockModel emptyBlock;
	
	public Chunk(Loader loader, VoxelWorld s, NoiseFunction f, int xoff, int zoff) {
		this.nf = f;
		this.xoff = xoff;
		this.zoff = zoff;
		this.s = s;
		for (int i = 0; i < x; i++) {
			for (int k = 0; k < z; k++) {
				int ref = (int) (f.getInterpolatedNoise(((xoff * x) + (i)) / 128.0f, ((zoff * z) + (k)) / 128.0f));
				for (int j = 0; j < y; j++) {
					if (j == ref) {
						if (ref < 32)
							blocks[i][j][k] = 5;
						else 
							blocks[i][j][k] = 4;
						blocksModels[i][j][k] = fullBlock;
					} else if (j <= ref - 1 && j >= ref - 5) {
						if (ref < 32)
							blocks[i][j][k] = 5;
						else 
							blocks[i][j][k] = 2;
						blocksModels[i][j][k] = fullBlock;
					} else if (j < 1) {
						blocks[i][j][k] = 3;
						blocksModels[i][j][k] = fullBlock;
					} else if (j < ref - 5) {
						blocks[i][j][k] = 1;
						blocksModels[i][j][k] = fullBlock;
					} else {
						blocks[i][j][k] = 0;
						blocksModels[i][j][k] = emptyBlock;
					}
					Block.blocks.get(blocks[i][j][k]).onBlockCreated(i, j, k, s);
				}
			}
		}
		//remesh();
	}

	public Chunk(Loader loader, VoxelWorld s, short[][][] blocks, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		this.s = s;
		this.blocks = blocks;
		
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					blocksModels[i][j][k] = fullBlock;
				}
			}
		}
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
		} catch (IndexOutOfBoundsException e) {}
		try {
			if (blocks[i][j - 1][k] != 0) {
				bottom = false;
			}
		} catch (IndexOutOfBoundsException e) {bottom = false;}
		try {
			if (blocks[i + 1][j][k] != 0) {
				right = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff + 1, zoff);
			if (c == null)
				right = false;
			else {
				if (c.blocks[0][j][k] != 0)
					right = false;
			}
		}
		try {
			if (blocks[i - 1][j][k] != 0) {
				left = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff - 1, zoff);
			if (c == null)
				left = false;
			else {
				if (c.blocks[x-1][j][k] != 0)
					left = false;
			}
		}
		try {
			if (blocks[i][j][k + 1] != 0) {
				front = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff, zoff + 1);
			if (c == null)
				front = false;
			else {
				if (c.blocks[i][j][0] != 0)
					front = false;
			}
		}
		try {
			if (blocks[i][j][k - 1] != 0) {
				back = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff, zoff - 1);
			if (c == null)
				back = false;
			else {
				if (c.blocks[i][j][z-1] != 0)
					back = false;
			}
		}
		
		blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(left, right, front, back, top, bottom));
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
	
	public void remeshNo() {
		// System.out.println("Remesher Thread Start");
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				for (int k = 0; k < blocks[i][j].length; k++) {
					mesh(i, j, k);
				}
			}
		}
		// System.out.println("Remesher Thread Dead");	
	}
	
	public void render(VoxelShader shader) {
		// TODO: add update that runs on seperate thread to handle this.
		int xz = s.random.nextInt(Chunk.x);
		int yz = s.random.nextInt(Chunk.y);
		int zz = s.random.nextInt(Chunk.z);
		if (blocks[xz][yz][zz] != 0)
			Block.blocks.get(blocks[xz][yz][zz]).onBlockTick(xz, yz, zz, s);
		
		int un = MeshStore.boolEmpty;
		
		for (int i =0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					if (blocks[i][j][k] == 0)
						continue;
					
					// make this based on texture id?
					RawBlockModel rawModel = blocksModels[i][j][k];
					
					if (rawModel == null)
						continue;
					
					if (rawModel.getVaoID() == un)
						continue;
					
					ModelTexture model = Block.blocks.get(blocks[i][j][k]).model;
					
					s.addBlock(rawModel, model.getID(), i+(x*xoff),j,k+(z*zoff));
					
					//GL30.glBindVertexArray((int)rawModel.getVaoID());
					//GL20.glEnableVertexAttribArray(0);
					//GL20.glEnableVertexAttribArray(1);
					//shader.loadTransformationMatrix(Maths.createTransformationMatrixCube(i+(x*xoff),j,k+(z*zoff)));
					/*shader.loadLightData(
							lightLevel[i][j+1 < Chunk.y ? j + 1 : y-1][k], 
							lightLevel[i][j-1 > -1 ? j-1 : 0][k], 
							0, 0, 0, 15);*/
					//GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getID());
					//GL11.glDrawElements(0x4, (int)rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					//GL20.glDisableVertexAttribArray(0);
					//GL20.glDisableVertexAttribArray(1);
					//GL30.glBindVertexArray(0);
				}
			}
		}
		
	}
	
	public void nul() {
		this.blocksModels = null;
		this.blocks = null;
	}
	
	public boolean getNull() {
		return this.blocks == null ? true : false;
	}
	
	public Block getBlockE(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return null;	
		// locking prevents race conditions!
		// right?
		// i did this right
		// if its not it works so i don't care.
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return Block.blocks.get(getBlock(x, y, z));
	}
	
	public short getBlock(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return blocks[x][y][z];
	}
	
	public byte getLightLevel(int x, int y, int z) {
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return lightLevel[x][y][z];
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
	
	public short[][][] getBlocks(){
		return blocks;
	}
	
	public int getX() {
		return xoff;
	}
	
	public int getZ() {
		return zoff;
	}
	
	public void setLightLevel(int x, int y, int z, byte level) {
		if (x >= Chunk.x || z >= Chunk.z)
			return;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y >= Chunk.y)
			y = Chunk.y-1;
		if (y < 0)
			y = 0;
		lightLevel[x][y][z] = level;
	}
	
	public void setBlock(int x, int y, int z, int block) {
		if (x >= Chunk.x || z >= Chunk.z)
			return;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y >= Chunk.y)
			y = Chunk.y-1;
		if (y < 0)
			y = 0;
		if (block == 0)
			blocksModels[x][y][z] = emptyBlock;
		blocks[x][y][z] = (short)block;
	}
	
	public void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Returns the height that the block should be at a point
	 * This returns the first block.
	 */
	public int getNaturalHeight(float x, float z) {
		return (int) (nf.getInterpolatedNoise((x) / 128.0f, (z) / 128.0f));
	}
	
	/**
	 * Returns the height that the block should be at a point
	 * This returns the first block.
	 * (This is in block pos relative to this chunk)
	 */
	public int getNaturalHeight(int x, int z) {
		return (int) (nf.getInterpolatedNoise(((xoff * x) + (x)) / 128.0f, ((zoff * z) + (z)) / 128.0f));
	}
	
	/**
	 * Returns the height of the chunk at the specified world pos.
	 * Note: this returns the last block before air. it will not return the first air.
	 */
	public int getChunkHeight(float x, float z) {
		int px = (int)x%16;
		int pz = (int)z%16;
		for (int i = 0; i < y; i++) {
			if (blocks[px][i][pz] == 0)
				return i-1;
		}
		return y;
	}
	
	/**
	 * Returns the height of the chunk at the specified world pos.
	 * Note: this returns the last block. it will not return the first air. (Except for if the air is 0)
	 */
	public int[] getChunkHeightPBlock(float x, float z) {
		int px = (int)x%16;
		int pz = (int)z%16;
		int[] ys = new int[y];
		for (int i = 0; i < y; i++) {
			if (blocks[px][i][pz] == 0) 
				ys[i] = i-1 > 0 ? i - 1 : 0;
		}
		return ys;
	}
	
	/**
	 * Returns the same as @getChunkHeightPBlock in list form
	 */
	public List<Integer> getChunkHeightPBlockL(float x, float z) {
		int px = (int)x%16;
		int pz = (int)z%16;
		List<Integer> ys = new ArrayList<Integer>();
		for (int i = 0; i < y; i++) {
			if (blocks[px][i][pz] == 0) 
				ys.add(i-1 > 0 ? i - 1 : 0);
		}
		return ys;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chunk[");
		sb.append(xoff);
		sb.append(", ");
		sb.append(zoff);
		sb.append("]");
		return sb.toString();
	}
	
}
