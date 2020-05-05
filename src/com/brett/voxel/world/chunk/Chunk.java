package com.brett.voxel.world.chunk;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.RawModel;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.lighting.LightingEngine;

/**
*
* @author brett
*
*
*/

public class Chunk {
	
	public static final int x = 16;
	public static final int y = 128;
	public static final int z = 16;
	
	private short[][][] blocks = new short[x][y][z];
	private byte[][][] lightLevel = new byte[x][y][z];
	//private RawBlockModel[][][] blocksModels = new RawBlockModel[x][y][z];
	private RawModel rawID;
	private float[] verts;
	private float[] uvs;
	private float[] lil;
	private int xoff,zoff;
	private VoxelWorld s;
	private Loader loader;
	private boolean waitingForMesh = false;
	private boolean isMeshing = false;
	
	private static List<Chunk> meshables = new ArrayList<Chunk>();
	private static List<Chunk> meshables2 = new ArrayList<Chunk>();
	private static List<Chunk> meshables3 = new ArrayList<Chunk>();
	private static List<Chunk> meshables4 = new ArrayList<Chunk>();
	
	public static void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					for (int i = 0; i < meshables.size(); i++) {
						Chunk c = meshables.get(i);
						if (c != null)
							c.remeshNo(-1);
						meshables.remove(i);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					for (int i = 0; i < meshables2.size(); i++) {
						Chunk c = meshables2.get(i);
						if (c != null)
							c.remeshNo(-1);
						meshables2.remove(i);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					for (int i = 0; i < meshables3.size(); i++) {
						Chunk c = meshables3.get(i);
						if (c != null)
							c.remeshNo(-1);
						meshables3.remove(i);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					for (int i = 0; i < meshables4.size(); i++) {
						Chunk c = meshables4.get(i);
						if (c != null)
							c.remeshNo(-1);
						meshables4.remove(i);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}
	
	/*public Chunk(Loader loader, VoxelWorld s, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		this.loader = loader;
		verts = new float[0];
		uvs = new float[0];
		lil = new float[0];
		this.s = s;
		for (int i = 0; i < x; i++) {
			for (int k = 0; k < z; k++) {
				//int ref = (int) (f.getBlockHeight(((xoff * x) + (i)), ((zoff * z) + (k))));
				int ref = 30;
				for (int j = 0; j < y; j++) {
					if (j == ref) {
						if (ref < 32)
							blocks[i][j][k] = 5;
						else 
							blocks[i][j][k] = 4;
					} else if (j <= ref - 1 && j >= ref - 5) {
						if (ref < 32)
							blocks[i][j][k] = 5;
						else 
							blocks[i][j][k] = 2;

					} else if (j < 1) {
						blocks[i][j][k] = 3;
					} else if (j < ref - 5) {
						blocks[i][j][k] = 1;
					} else {
						blocks[i][j][k] = 0;
					}
					Block.blocks.get(blocks[i][j][k]).onBlockCreated(i, j, k, s);
				}
			}
		}
		//remesh(-1);
	}*/

	public Chunk(Loader loader, VoxelWorld s, short[][][] blocks, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		this.s = s;
		this.blocks = blocks;
		this.loader = loader;
		verts = new float[0];
		uvs = new float[0];
		lil = new float[0];
		//remesh(-1);
	}
	
	// don't look at this please
	// it is not good.
	// TODO: improve this
	// this is rough code
	public byte mesh(int i, int j, int k) {
		if(blocks[i][j][k] == 0)
			return 0;
		boolean top = true;
		boolean bottom = true;
		boolean left = true;
		boolean right = true;
		boolean front = true;
		boolean back = true;
		byte data = 0;
		try {
			if (blocks[i][j + 1][k] != 0 && !Block.blocks.get(blocks[i][j + 1][k]).isTransparent()) {
				top = false;
			}
		} catch (IndexOutOfBoundsException e) {}
		try {
			if (blocks[i][j - 1][k] != 0 && !Block.blocks.get(blocks[i][j - 1][k]).isTransparent()) {
				bottom = false;
			}
		} catch (IndexOutOfBoundsException e) {bottom = false;}
		try {
			if (blocks[i - 1][j][k] != 0 && !Block.blocks.get(blocks[i - 1][j][k]).isTransparent()) {
				left = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff - 1, zoff);
			if (c == null) {
				left = false;
				// top brain kind of stuff here
				// binary literal plus bitwise operator
				// top brain indeed
				data |= 0b0001;
			} else {
				if (c.blocks[x-1][j][k] != 0 && !Block.blocks.get(blocks[x-1][j][k]).isTransparent())
					left = false;
			}
		}
		try {
			if (blocks[i + 1][j][k] != 0 && !Block.blocks.get(blocks[i+1][j][k]).isTransparent()) {
				right = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff + 1, zoff);
			if (c == null) {
				right = false;
				data |= 0b0010;
			} else {
				if (c.blocks[0][j][k] != 0 && !Block.blocks.get(c.blocks[0][j][k]).isTransparent())
					right = false;
			}
		}
		try {
			if (blocks[i][j][k + 1] != 0 && !Block.blocks.get(blocks[i][j][k + 1]).isTransparent()) {
				front = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff, zoff + 1);
			if (c == null) {
				front = false;
				data |= 0b0100;
			} else {
				if (c.blocks[i][j][0] != 0 && !Block.blocks.get(blocks[i][j][0]).isTransparent())
					front = false;
			}
		}
		try {
			if (blocks[i][j][k - 1] != 0 && !Block.blocks.get(blocks[i][j][k-1]).isTransparent()) {
				back = false;
			}
		} catch (IndexOutOfBoundsException e) {
			Chunk c = s.chunk.getChunk(xoff, zoff - 1);
			if (c == null) {
				back = false;
				data |= 0b1000;
			} else {
				if (c.blocks[i][j][z-1] != 0 && !Block.blocks.get(blocks[i][j][z-1]).isTransparent())
					back = false;
			}
		}
		
		Block b = Block.blocks.get(blocks[i][j][k]);
		
		float xOff = (xoff * Chunk.x) + i;
		float zOff = (zoff * Chunk.z) + k;
		if (left) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsLeftComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvLeftComplete, b.textureLeft));
			byte w = s.chunk.getLightLevel(xOff-1, j, zOff);
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		if (right) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsRightComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvRightComplete, b.textureRight));
			byte w = s.chunk.getLightLevel(xOff+1, j, zOff);
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		if (front) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsFrontComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvFrontComplete, b.textureFront));
			byte w = s.chunk.getLightLevel(xOff, j, zOff+1);
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		if (back) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsBackComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvBackComplete, b.textureBack));
			byte w = s.chunk.getLightLevel(xOff, j, zOff-1);
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		if (top) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsTopComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvTopComplete, b.textureTop));
			byte w = lightLevel[i][j+1 < Chunk.y ? j+1 : Chunk.y-1][k];
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		if (bottom) {
			verts = addArrays(verts, ChunkBuilder.updateVertexTranslation(MeshStore.vertsBottomComplete, i, j, k));
			uvs = addArrays(uvs, AtlasHelper.convertToIndex(MeshStore.uvBottomComplete, b.textureBottom));
			byte w = lightLevel[i][j-1 > 0 ? j-1 : 0][k];
			w += LightingEngine.sunLevel;
			if (w > 15)
				w = 15;
			lil = addArrays(lil, new float[] {w, w, w, w,w,w});
		}
		return data;
		//blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(left, right, front, back, top, bottom));
	}
	
	public void remesh() {
		//System.out.println("Too much");
		remesh(1);
	}
	
	/**
	 * 
	 */
	public void remeshPRI() {
		//System.out.println("Too much");
		Chunk.meshables4.add(this);
	}
	
	public void remeshNo() {
		remeshNo(1);
	}
	
	public void remesh(int sideQ) {
		if (Chunk.meshables.size() < Chunk.meshables2.size())
			Chunk.meshables.add(this);
		else if (Chunk.meshables2.size() < Chunk.meshables3.size())
			Chunk.meshables2.add(this);
		else
			Chunk.meshables3.add(this);
	}
	
	public void remeshSecond() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				remeshNo();
			}
		}).start();
		//Chunk.meshablesSecond.add(this);
	}
	
	public void remeshNo(int sideQ) {
		// System.out.println("Remesher Thread Start");
		while (isMeshing) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
		verts = new float[0];
		uvs = new float[0];
		lil = new float[0];
		@SuppressWarnings("unused")
		byte out = 0;
		for (int i = 0; i < x; i++) {
			for (int k = 0; k < z; k++) {
				for (int j = 0; j < y; j++) {
					out |= mesh(i, j, k);
				}
			}
		}
		waitingForMesh = true;
		/*if (out > 0 && sideQ == -1) {
			System.out.println(out);
			// the idea here is that after meshing this chunk, the missing chunks will be there
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {}
			
			if ((out & 0b0001) != 0b0001) {
				Chunk c = s.chunk.getChunk(xoff - 1, zoff);
				if (c != null)
					c.remeshSecond();
			}
			if ((out & 0b0010) != 0b0010) {
				Chunk c = s.chunk.getChunk(xoff + 1, zoff);
				if (c != null)
					c.remeshSecond();
			}
			if ((out & 0b0100) != 0b0100) {
				Chunk c = s.chunk.getChunk(xoff, zoff + 1);
				if (c != null)
					c.remeshSecond();
			}
			if ((out & 0b1000) != 0b1000) {
				Chunk c = s.chunk.getChunk(xoff, zoff - 1);
				if (c != null)
					c.remeshSecond();
			}
			//Chunk c = s.chunk.getChunk(xoff - 1, zoff);
			//if (c != null)
				//c.remeshNo();
			remeshNo(1);
		}*/
		// System.out.println("Remesher Thread Dead");	
	}
	
	public void render(VoxelShader shader) {
		if (waitingForMesh) {
			isMeshing = true;
			if(rawID != null)
				loader.deleteVAO(rawID);
			rawID = loader.loadToVAO(verts, uvs, lil);
			waitingForMesh = false;
			isMeshing = false;
		}
		// TODO: add update that runs on seperate thread to handle this.
		int xz = s.random.nextInt(Chunk.x);
		int yz = s.random.nextInt(Chunk.y);
		int zz = s.random.nextInt(Chunk.z);
		if (blocks[xz][yz][zz] != 0)
			Block.blocks.get(blocks[xz][yz][zz]).onBlockTick(xz, yz, zz, s);
		
		
		if (rawID == null)
			return;
		GL30.glBindVertexArray(rawID.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		shader.loadTransformationMatrix(Maths.createTransformationMatrixCube(x*xoff,0,z*zoff));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawID.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
	}
	
	public void nul() {
		//this.blocksModels = null;
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
		for (int i = 0; i < 128; i++) {
			if (blocks[x][i][z] == 0) {
				return i-1;
			}
		}
		return 0;
	}
	
	public int getHeightA(int x, int z) {
		if (x >= Chunk.x || y <= 0 || z >= Chunk.z)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		for (int i = 0; i < 128; i++) {
			if (blocks[x][i][z] == 0) {
				return i;
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
	
	public void setBlock(int x, int y, int z, int rx, int rz, int block) {
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
		if (block != 0)
			Block.blocks.get((short) block).onBlockPlaced(rx, y, rz, s);
		Block.blocks.get(blocks[x][y][z]).onBlockBreaked(rx, y, rz, s);
		if (blocks[x][y][z] != Block.BLOCK_WILL)
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
	/*public int getNaturalHeight(float x, float z) {
		return (int) (nf.getBlockHeight((x) / 128.0f, (z) / 128.0f));
	}*/
	
	/**
	 * Returns the height that the block should be at a point
	 * This returns the first block.
	 * (This is in block pos relative to this chunk)
	 */
	/*public int getNaturalHeight(int x, int z) {
		return (int) (nf.getBlockHeight(((xoff * x) + (x)) / 128.0f, ((zoff * z) + (z)) / 128.0f));
	}*/
	
	/**
	 * Returns the height of the chunk at the specified world pos.
	 * Note: this returns the last block before air. it will not return the first air.
	 */
	public int getChunkHeight(float x, float z) {
		int px = (int)x%Chunk.x;
		int pz = (int)z%Chunk.z;
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
		int px = (int)x%Chunk.x;
		int pz = (int)z%Chunk.z;
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
		int px = (int)x%Chunk.x;
		int pz = (int)z%Chunk.z;
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
	
}
