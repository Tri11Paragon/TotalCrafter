package com.brett.voxel.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.brett.datatypes.ModelVAO;
import com.brett.renderer.Loader;
import com.brett.tools.Maths;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.renderer.RENDERMODE;
import com.brett.voxel.renderer.shaders.VoxelShader;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;

/**
*
* @author brett
* Idk when I made this? likely close to when the semester started.
*/

public class Chunk {
	
	public static final int x = 16;
	public static final int y = 128;
	public static final int z = 16;
	
	private short[][][] blocks = new short[x][y][z];
	private byte[][][] lightLevel = new byte[x][y][z];
	private ModelVAO rawID;
	private float[] verts;
	private float[] uvs;
	private ModelVAO rawIDTrans;
	private float[] vertsTrans;
	private float[] uvsTrans;
	private int xoff,zoff;
	private IWorldProvider s;
	private Loader loader;
	private boolean waitingForMesh = false;
	private boolean isMeshing = false;
	private byte chunkErrors;
	
	private static volatile List<Chunk> meshables = new ArrayList<Chunk>();
	private static volatile List<Chunk> meshables2 = new ArrayList<Chunk>();
	private static volatile List<Chunk> meshables3 = new ArrayList<Chunk>();
	private static volatile List<Chunk> meshables4 = new ArrayList<Chunk>();
	public static volatile List<ModelVAO> deleteables = new ArrayList<ModelVAO>();
	
	public static void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
						for (int i = 0; i < meshables.size(); i++) {
							try {
								Chunk c = meshables.get(i);
								if (c != null) {
									if (c.remeshNo())
										continue;
								}
								try {
									meshables.remove(i);
								} catch (Exception e) {}
							} catch (Exception e) {
								System.err.println("There has been an error in the chunk mesher. 1");
								System.err.println(e.getCause());
								e.printStackTrace();
							}
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
					try {
						for (int i = 0; i < meshables2.size(); i++) {
							Chunk c = meshables2.get(i);
							if (c != null) {
								if (c.remeshNo())
									continue;
							}
							try {
								meshables2.remove(i);
							} catch (Exception e) {}
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
					} catch (Exception e) {
						System.err.println("There has been an error in the chunk mesher. 2");
						System.err.println(e.getCause());
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					try {
						for (int i = 0; i < meshables3.size(); i++) {
							Chunk c = meshables3.get(i);
							if (c != null) {
								if (c.remeshNo())
									continue;
							}
							try {
								meshables3.remove(i);
							} catch (Exception e) {}
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
					} catch (Exception e) {
						System.err.println("There has been an error in the chunk mesher. 3");
						System.err.println(e.getCause());
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (VoxelScreenManager.isOpen) {
					try {
						for (int i = 0; i < meshables4.size(); i++) {
							Chunk c = meshables4.get(i);
							if (c != null) {
								if (c.remeshNo())
									continue;
							}
							try {
								meshables4.remove(i);
							} catch (Exception e) {}
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}
					} catch (Exception e) {
						System.err.println("There has been an error in the chunk mesher. 4");
						System.err.println(e.getCause());
					}
				}
			}
		}).start();
	}

	public Chunk(Loader loader, IWorldProvider s, short[][][] blocks, int xoff, int zoff) {
		this.xoff = xoff;
		this.zoff = zoff;
		this.s = s;
		this.blocks = blocks;
		this.loader = loader;
		verts = new float[0];
		uvs = new float[0];
		vertsTrans = new float[0];
		uvsTrans = new float[0];
		remesh();
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
		
		Block b = Block.blocks.get((short)(blocks[i][j][k] & 0xFFF));
		
		if (b.getRendermode() != RENDERMODE.SPECIAL) {
			try {
				if (Block.blocks.get((short)(blocks[i][j + 1][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					top = false;
				} else
					top = true;
			} catch (IndexOutOfBoundsException e) {}
			try {
				if (Block.blocks.get((short)(blocks[i][j - 1][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					bottom = false;
				} else 
					bottom = true;
			} catch (IndexOutOfBoundsException e) {bottom = false;}
			try {
				if (Block.blocks.get((short)(blocks[i - 1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					left = false;
				} else
					left = true;
			} catch (IndexOutOfBoundsException e) {
				Chunk c = s.chunk.getChunk(xoff - 1, zoff);
				if (c == null) {
					left = false;
					// top brain kind of stuff here
					// binary literal plus bitwise operator
					// top brain indeed
					data |= 0b0001;
				} else {
					if (Block.blocks.get((short)(c.blocks[x-1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						left = false;
					else
						left = true;
				}
			}
			try {
				if (Block.blocks.get((short)(blocks[i+1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					right = false;
				} else
					right = true;
			} catch (IndexOutOfBoundsException e) {
				Chunk c = s.chunk.getChunk(xoff + 1, zoff);
				if (c == null) {
					right = false;
					data |= 0b0010;
				} else {
					if (Block.blocks.get((short)(c.blocks[0][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						right = false;
					else
						right = true;
				}
			}
			try {
				if (Block.blocks.get((short)(blocks[i][j][k + 1] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					front = false;
				} else
					front = true;
			} catch (IndexOutOfBoundsException e) {
				Chunk c = s.chunk.getChunk(xoff, zoff + 1);
				if (c == null) {
					front = false;
					data |= 0b0100;
				} else {
					if (Block.blocks.get((short)(c.blocks[i][j][0] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						front = false;
					else
						front = true;
				}
			}
			try {
				if (Block.blocks.get((short)(blocks[i][j][k-1] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					back = false;
				}
			} catch (IndexOutOfBoundsException e) {
				Chunk c = s.chunk.getChunk(xoff, zoff - 1);
				if (c == null) {
					back = false;
					data |= 0b1000;
				} else {
					if (Block.blocks.get((short)(c.blocks[i][j][z-1] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						back = false;
				}
			}
			
			byte blockState = (byte)(blocks[i][j][k] >> 12);
			int tLeft = 0, tRight = 0, tFront = 0, tBack = 0, tTop = 0, tBottom = 0;
			
			if (blockState != 0) {
				if (blockState == 1) {
					tLeft = b.textureFront;
					tRight = b.textureBack;
					tFront = b.textureRight;
					tBack = b.textureLeft;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 2) {
					tLeft = b.textureBack;
					tRight = b.textureFront;
					tFront = b.textureLeft;
					tBack = b.textureRight;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 3) {
					tLeft = b.textureRight;
					tRight = b.textureLeft;
					tFront = b.textureBack;
					tBack = b.textureFront;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 4) {
					tLeft = b.textureFront2;
					tRight = b.textureBack;
					tFront = b.textureRight;
					tBack = b.textureLeft;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 5) {
					tLeft = b.textureBack;
					tRight = b.textureFront2;
					tFront = b.textureLeft;
					tBack = b.textureRight;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 6) {
					tLeft = b.textureRight;
					tRight = b.textureLeft;
					tFront = b.textureBack;
					tBack = b.textureFront2;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 7) {
					tLeft = b.textureLeft;
					tRight = b.textureRight;
					tFront = b.textureFront2;
					tBack = b.textureBack;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				}
			} else {
				tLeft = b.textureLeft;
				tRight = b.textureRight;
				tFront = b.textureFront;
				tBack = b.textureBack;
				tTop = b.textureTop;
				tBottom = b.textureBottom;
			}
			
			
			float xOff = (xoff * Chunk.x) + i;
			float zOff = (zoff * Chunk.z) + k;
			if (b.getRendermode() == RENDERMODE.SOLID) {
				if (left) {
					byte w = s.chunk.getLightLevel(xOff-1, j, zOff);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsLeftComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, w, tLeft));
				}
				if (right) {
					byte w = s.chunk.getLightLevel(xOff+1, j, zOff);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsRightComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, w, tRight));
				}
				if (front) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff+1);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsFrontComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, w, tFront));
				}
				if (back) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff-1);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBackComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, w, tBack));
				}
				if (top) {
					int j1 = j+1;
					if (j1 >= Chunk.y)
						j1 = Chunk.y-1;
					byte w = lightLevel[i][j1][k];
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsTopComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, w, tTop));
				}
				if (bottom) {
					int j1 = j-1;
					if (j1 < 0)
						j1 = 0;
					byte w = lightLevel[i][j1][k];
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBottomComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, w, tBottom));
				}
			} else {
				if (left) {
					byte w = s.chunk.getLightLevel(xOff-1, j, zOff);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsLeftComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, w, tLeft));
				}
				if (right) {
					byte w = s.chunk.getLightLevel(xOff+1, j, zOff);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsRightComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, w, tRight));
				}
				if (front) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff+1);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsFrontComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, w, tFront));
				}
				if (back) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff-1);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBackComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, w, tBack));
				}
				if (top) {
					int j1 = j+1;
					if (j1 >= Chunk.y)
						j1 = Chunk.y-1;
					byte w = lightLevel[i][j1][k];
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsTopComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, w, tTop));
				}
				if (bottom) {
					int j1 = j-1;
					if (j1 < 0)
						j1 = 0;
					byte w = lightLevel[i][j1][k];
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBottomComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, w, tBottom));
				}
			}
		} else {
			byte w = lightLevel[i][j][k];
			vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(b.getSpecialVerts(), i, j, k));
			uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(b.getSpecialTextures(), w,b.textureFront));
		}
		return data;
		//blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(left, right, front, back, top, bottom));
	}
	
	public void remeshPRI() {
		Chunk.meshables4.add(this);
	}
	
	public void remesh() {
		if (Chunk.meshables.size() < Chunk.meshables2.size())
			Chunk.meshables.add(this);
		else if (Chunk.meshables2.size() < Chunk.meshables3.size())
			Chunk.meshables2.add(this);
		else
			Chunk.meshables3.add(this);
	}
	
	// binary literal representation of each direction.
	private static final byte left = 0b0001;
	private static final byte right = 0b0010;
	private static final byte front = 0b0100;
	private static final byte back = 0b1000;
	public boolean remeshNo() {
		if (isMeshing)
			return true;
		isMeshing = true;
		verts = new float[0];
		uvs = new float[0];
		vertsTrans = new float[0];
		uvsTrans = new float[0];
		lastIndex = 0;
		lastIndexUV = 0;
		lastIndexTransUV = 0;
		lastIndexTrans = 0;
		chunkErrors = 0;
		for (int k = 0; k < z; k++) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					chunkErrors |= mesh(i, j, k);
				}
			}
		}
		// this is how you encode a boolean (9 bytes???!?!?!?) 
		// with only half a byte!
		// SixBoolean!
		// ^ thats a callback to the dark ages
		
		// left
		if ((chunkErrors & left) != left) {
			Chunk c = s.chunk.getChunk(xoff-1, zoff);
			if (c == null) {
				System.err.println("CHUNK MESHER ISSUE");
				System.out.flush();
			} else {
				if ((c.chunkErrors & right) == right) {
					c.remesh();
				}
			}
		}
		// right
		if ((chunkErrors & right) != right) {
			Chunk c = s.chunk.getChunk(xoff+1, zoff);
			// chunks can't be null but this has happened
			if (c == null) {
				System.err.println("CHUNK MESHER ISSUE");
				System.out.flush();
			} else {
				if ((c.chunkErrors & left) == left) {
					c.remesh();
				}
			}
		}
		// front
		if ((chunkErrors & front) != front) {
			Chunk c = s.chunk.getChunk(xoff, zoff+1);
			if (c == null) {
				System.err.println("CHUNK MESHER ISSUE");
				System.out.flush();
			}else {
				if ((c.chunkErrors & back) == back) {
					c.remesh();
				}
			}
		}
		// back
		if ((chunkErrors & back) != back) {
			Chunk c = s.chunk.getChunk(xoff, zoff-1);
			if (c == null) {
				System.err.println("CHUNK MESHER ISSUE");
				System.out.flush();
			} else {
				if ((c.chunkErrors & front) == front) {
					c.remesh();
				}
			}
		}
		waitingForMesh = true;
		return false;
	}
	
	/**
	 * Draws this chunk object. <br> 
	 * This method will load the chunk mesh to the GPU if there is a mesh waiting to be loaded.
	 * @param shader the shader to load the translation matrix up to
	 * @param view the matrix that holds the combined view and projection matrices
	 * @param cx the local chunk x position (in chunk space) relative to the camera
	 * @param cz the local chunk z position (in chunk space) relative to the camera
	 */
	public void render(VoxelShader shader, Matrix4f view, int cx, int cz) {
		if (waitingForMesh) {
			// make sure we don't change the mesh while we are loading it to the GPU
			isMeshing = true;
			// delete the old VAO off the GPU if it exists
			if(rawID != null)
				loader.deleteVAO(rawID);
			// delete the old transparnt VAO off the GPU if it exists
			if(rawIDTrans != null)
				loader.deleteVAO(rawIDTrans);
			// load the newly defined verts into the GPU
			rawID = loader.loadToVAO(verts, uvs, 1);
			// load the transparent vertices if we have transparent objects
			if (vertsTrans.length > 0 && uvsTrans.length > 0)
				rawIDTrans = loader.loadToVAO(vertsTrans, uvsTrans, 1);
			// we are no longer waiting to mesh
			waitingForMesh = false;
			// no need to store verts locally as the GPU now has them
			verts = null;
			uvs = null;
			vertsTrans = null;
			uvsTrans = null;
			// we are no longer meshing. The mesher can now mesh again.
			isMeshing = false;
		}
		// don't render if we don't have blocks or a mesh
		if (blocks == null || rawID == null)
			return;
		// enable the VAO with the chunk mesh
		GL30.glBindVertexArray(rawID.getVaoID());
		// enable the pointers to the data
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		// create the transformation matrix based on localized position
		Matrix4f trans = Maths.createTransformationMatrixCube(x*cx,0,z*cz);
		// multiplies the matrix here on the CPU instead of doing it per vertex on the GPU
		Matrix4f.mul(view, trans, trans);
		// load this to the shader
		shader.loadTransformationMatrix(trans);
		// draws all the vertices found in the VBOs
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawID.getVertexCount());
		
		// disable the VBO and VAO as we no longer are using them
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		
	}
	
	/**
	 * slowdown at the cost of it looking good.
	 */
	public void renderSpecial(VoxelShader shader, Matrix4f view, int cx, int cz) {
		if (blocks == null)
			return;
		if (rawIDTrans == null)
			return;
		
		GL30.glBindVertexArray(rawIDTrans.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		Matrix4f trans = Maths.createTransformationMatrixCube(x*cx,0,z*cz);
		Matrix4f.mul(view, trans, trans);
		shader.loadTransformationMatrix(trans);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawIDTrans.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	// same thing as a tick() function.
	public void fixedUpdate() {
		int xz = s.random.nextInt(Chunk.x);
		int yz = s.random.nextInt(Chunk.y);
		int zz = s.random.nextInt(Chunk.z);
		if (blocks == null)
			return;
		if (blocks[xz][yz][zz] != 0) // TODO: check if this is right
			Block.blocks.get((short) (blocks[xz][yz][zz] & 0xFFF)).onBlockTick(xz + xoff*Chunk.x, yz, zz + zoff*Chunk.z, s);
	}
	
	/**
	 * deletes this chunk from memory.
	 */
	public void nul() {
		//this.blocksModels = null;
		//this.rawID = loader.deleteVAO(rawID);
		Chunk.deleteables.add(this.rawID);
		this.rawID = null;
		this.blocks = null;
	}
	
	public short getBlockRaw(int x, int y, int z) {
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return 0;
		if (blocks == null)
			return 0;
		return blocks[x][y][z];
	}
	
	public short getBlock(int x, int y, int z) {
		return (short) (getBlockRaw(x, y, z) & 0xFFF);
	}
	
	public byte getBlockState(int x, int y, int z) {
		return (byte) (getBlockRaw(x, y, z) >> 12);
	}
	
	/**
	 * Returns compressed light level.
	 * First 4 bits is sunlight, last 4 bits is torch light.
	 */
	public byte getLightLevel(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return lightLevel[x][y][z];
	}
	
	public byte getLightLevelTorch(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return (byte) (lightLevel[x][y][z] & 0x0F);
	}
	
	public byte getLightLevelSun(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return (byte) (lightLevel[x][y][z] & 0xF0);
	}
	
	public boolean isBlockUnderGround(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z)
			return false;
		if (y < 0)
			return true;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return (short) (blocks[x][y + 1][z] & 0xFFF) == 0 ? false : true;
	}
	
	public int getHeight(int x, int z) {
		if (x >= Chunk.x || y <= 0 || z >= Chunk.z)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		for (int i = 0; i < 128; i++) {
			if ((short) (blocks[x][i][z] & 0xFFF) == 0) {
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
			if ((short) (blocks[x][i][z] & 0xFFF) == 0) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Sets the compressed light level for the block
	 * first 4 bits is sunlight, last 4 bits is torch light
	 */
	public void setLightLevel(int x, int y, int z, int level) {
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
		lightLevel[x][y][z] = (byte) (level & 0xFF);
	}
	
	public void setLightLevelSun(int x, int y, int z, int level) {
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
		lightLevel[x][y][z] &= 0x0F;
		lightLevel[x][y][z] |= ((level & 0xF) << 4);
	}
	
	public void setLightLevelTorch(int x, int y, int z, int level) {
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
		lightLevel[x][y][z] &= 0xF0;
		lightLevel[x][y][z] |= ((level & 0xF));
	}
	
	public void setBlockRaw(int x, int y, int z, int rx, int rz, int block) {
		if (VoxelWorld.isRemote)
			VoxelWorld.localClient.updateBlock((int)rx, (int)y, (int)rz, (short)block);
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
		Block.blocks.get((short) (blocks[x][y][z] & 0xFFF)).onBlockBreaked(rx, y, rz, s);
		if ((blocks[x][y][z] & 0xFFF) != Block.WILL)
			blocks[x][y][z] = (short) (block);
		if (block != 0)
			Block.blocks.get((short) block).onBlockPlaced(rx, y, rz, s);
	}
	
	public void setBlockRawServer(int x, int y, int z, int rx, int rz, int block) {
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
		Block.blocks.get((short) (blocks[x][y][z] & 0xFFF)).onBlockBreaked(rx, y, rz, s);
		if ((blocks[x][y][z] & 0xFFF) != Block.WILL)
			blocks[x][y][z] = (short) (block);
		if (block != 0)
			Block.blocks.get((short) block).onBlockPlaced(rx, y, rz, s);
	}
	
	public void setBlock(int x, int y, int z, int rx, int rz, int block) {
		setBlockRaw(x, y, z, rx, rz, (block & 0xFFFF));
	}
	
	public void setBlockServer(int x, int y, int z, int rx, int rz, int block) {
		setBlockRawServer(x, y, z, rx, rz, (block & 0xFFFF));
	}
	
	public void setBlockState(int x, int y, int z, int rx, int rz, byte state) {
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
		blocks[x][y][z] &= 0x0FFF;
		if (blocks[x][y][z] != Block.WILL)
			blocks[x][y][z] |= (state << 12);
	}
	
	public void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	/**
	 * Returns a nice neat packet of information containing the x and z offsets
	 * which define this chunk's position in the world.
	 */
	@Override
	public String toString() {
		// when you do String $something = String1 + String2
		// java calls string builder internally.
		// so instead of making 5 string builder objects and discarding them
		// make one string builder and return the string it builds.
		StringBuilder sb = new StringBuilder();
		sb.append("Chunk[");
		sb.append(xoff);
		sb.append(", ");
		sb.append(zoff);
		sb.append("]");
		return sb.toString();
	}
	
	// all these addarrays functions are the same
	// and the only difference is that they are for the different parts of the vertex
	// these define the last index we where at for each function.
	// im sure there is a "better" way of doing this but this is fine.
	private int lastIndex = 0;
	private int lastIndexUV = 0;
	private int lastIndexTrans = 0;
	private int lastIndexTransUV = 0;
	
	/**
	 * Adds array2 to array1 allocating more space only if needed.
	 * 
	 * This is to be used for non transparent vertices ONLY.
	 * Please be sure to clear the lastIndex for each mesh!
	 */
	private float[] addArrays(float[] array1, float[] array2) {
		float[] rtv = array1;
		
		// if copying this array goes out of the base array (array1) range, then we need to allocate more
		// ram to this array. We should allocate some extra space as we will likely be needing it in a few ns
		// Arrays.copyof uses System.arraycopy which is a <b>native</b> java function
		if (lastIndex + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length)*3);
		
		// copy the add array (array2) into the base array (array1)
		System.arraycopy(array2, 0, rtv, lastIndex, array2.length);
		// increase the last index that was used.
		lastIndex += array2.length;
		
		return rtv;
	}

	/**
	 * Adds array2 to array1 allocating more space only if needed.
	 * 
	 * This is to be used for non transparent UVs ONLY.
	 * Please be sure to clear the lastIndexUV for each mesh!
	 */
	private float[] addArraysUV(float[] array1, float[] array2) {
		// see other function. {@code addArrays} It is the same as this except it uses lastIndex.
		float[] rtv = array1;
		
		if (lastIndexUV + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length)*3);
		
		System.arraycopy(array2, 0, rtv, lastIndexUV, array2.length);
		lastIndexUV += array2.length;

		return rtv;
	}
	
	/**
	 * Adds array2 to array1 allocating more space only if needed.
	 * 
	 * This is to be used for transparent vertices ONLY.
	 * Please be sure to clear the lastIndexTrans for each mesh!
	 */
	private float[] addArraysTrans(float[] array1, float[] array2) {
		// see other function. {@code addArrays} It is the same as this except it uses lastIndex.
		float[] rtv = array1;
		
		if (lastIndexTrans + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length)*3);
		
		System.arraycopy(array2, 0, rtv, lastIndexTrans, array2.length);
		lastIndexTrans += array2.length;
		
		return rtv;
	}
	
	/**
	 * Adds array2 to array1 allocating more space only if needed.
	 * 
	 * This is to be used for transparent UVs ONLY.
	 * Please be sure to clear the lastIndexTransUV for each mesh!
	 */
	private float[] addArraysTransUV(float[] array1, float[] array2) {
		// see other function. {@code addArrays} It is the same as this except it uses lastIndex.
		float[] rtv = array1;
		
		if (lastIndexTransUV + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length)*3);
		
		System.arraycopy(array2, 0, rtv, lastIndexTransUV, array2.length);
		lastIndexTransUV += array2.length;
		
		return rtv;
	}
	
	/**
	 * Some getter and setters below.
	 */
	
	public void setBlocks(short[][][] blocks) {
		this.blocks = blocks;
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
	
}
