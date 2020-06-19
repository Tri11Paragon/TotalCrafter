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
	
	/**
	 * why store as a short and not a block object???
	 * well its because if you use block objects then you waste ram.
	 * It also takes more time to create an object then it does to set a single short in an array.
	 */
	private short[][][] blocks = new short[x][y][z];
	// not currently using this but lighting is ready to be used
	// it just need to have a good flood fill algorithm and it will work.
	private byte[][][] lightLevel = new byte[x][y][z];

	private ModelVAO rawIDTrans;
	private ModelVAO rawID;
	private float[] verts;
	private float[] uvs;
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
		/*
		 * 4 threads of the same thing. they all mesh chunks as they are added.
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				// run while the game is open
				while (VoxelScreenManager.isOpen) {
					// go through all the chunks to mesh
					for (int i = 0; i < meshables.size(); i++) {
						try {
							// get the chunk
							Chunk c = meshables.get(i);
							if (c != null) {
								// remesh it
								if (c.remeshNo())
									continue;
							}
							try {
								// remove it.
								meshables.remove(i);
							} catch (Exception e) {
							}
						} catch (Exception e) {
							System.err.println("There has been an error in the chunk mesher. 1");
							System.err.println(e.getCause());
							e.printStackTrace();
						}
					}
					try {
						// make sure it can run too quick
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		// same as above
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
		// same as above
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
		// same as above but used for important stuff only.
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
		// no need to mesh air
		if(blocks[i][j][k] == 0)
			return 0;
		// true if we add the block face
		// false if not
		boolean top = true;
		boolean bottom = true;
		boolean left = true;
		boolean right = true;
		boolean front = true;
		boolean back = true;
		byte data = 0;
		
		// get the block at this pos
		Block b = Block.blocks.get((short)(blocks[i][j][k] & 0xFFF));
		
		// make sure we are not a special render case
		if (b.getRendermode() != RENDERMODE.SPECIAL) {
			// check if there is a solid block to the top
			try {
				if (Block.blocks.get((short)(blocks[i][j + 1][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					top = false;
				} else
					top = true;
			} catch (IndexOutOfBoundsException e) {}
			// check if there is a solid block to the bottom
			try {
				if (Block.blocks.get((short)(blocks[i][j - 1][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					bottom = false;
				} else 
					bottom = true;
			} catch (IndexOutOfBoundsException e) {bottom = false;}
			// check if there is a block to the left
			try {
				if (Block.blocks.get((short)(blocks[i - 1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					left = false;
				} else
					left = true;
			} catch (IndexOutOfBoundsException e) {
				// check if the chunk beside us has a block to the left
				Chunk c = s.chunk.getChunk(xoff - 1, zoff);
				// if the chunk is null then we need to flag it.
				if (c == null) {
					left = false;
					// top brain kind of stuff here
					// binary literal plus bitwise operator
					// top brain indeed
					// flag as left being null.
					data |= 0b0001;
				} else {
					// check if the block beside us has a left block solid
					if (Block.blocks.get((short)(c.blocks[x-1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						left = false;
					else
						left = true;
				}
			}
			// this stuff below is the same as above but for the other directions
			// check if there is a block beside us to the right
			try {
				if (Block.blocks.get((short)(blocks[i+1][j][k] & 0xFFF)).getRendermode() == RENDERMODE.SOLID) {
					right = false;
				} else
					right = true;
			} catch (IndexOutOfBoundsException e) {
				Chunk c = s.chunk.getChunk(xoff + 1, zoff);
				if (c == null) {
					right = false;
					// flag for right being null
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
					// flag for front being null
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
					// flag for back being null
					data |= 0b1000;
				} else {
					if (Block.blocks.get((short)(c.blocks[i][j][z-1] & 0xFFF)).getRendermode() == RENDERMODE.SOLID)
						back = false;
				}
			}
			
			// get the block state (last 4 bits in the short)
			byte blockState = (byte)(blocks[i][j][k] >> 12);
			int tLeft = 0, tRight = 0, tFront = 0, tBack = 0, tTop = 0, tBottom = 0;
			
			/**
			 * this just assigns texture based on what this block state for this block is
			 */
			if (blockState != 0) {
				if (blockState == 1) {
					// front (texture) being left
					tLeft = b.textureFront;
					tRight = b.textureBack;
					tFront = b.textureRight;
					tBack = b.textureLeft;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 2) {
					// front being right
					tLeft = b.textureBack;
					tRight = b.textureFront;
					tFront = b.textureLeft;
					tBack = b.textureRight;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 3) {
					// front being back
					tLeft = b.textureRight;
					tRight = b.textureLeft;
					tFront = b.textureBack;
					tBack = b.textureFront;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 4) {
					// front being left second image
					tLeft = b.textureFront2;
					tRight = b.textureBack;
					tFront = b.textureRight;
					tBack = b.textureLeft;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 5) {
					// front being right second image
					tLeft = b.textureBack;
					tRight = b.textureFront2;
					tFront = b.textureLeft;
					tBack = b.textureRight;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 6) {
					// front being back second image
					tLeft = b.textureRight;
					tRight = b.textureLeft;
					tFront = b.textureBack;
					tBack = b.textureFront2;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				} else if (blockState == 7) {
					// front being front second image
					tLeft = b.textureLeft;
					tRight = b.textureRight;
					tFront = b.textureFront2;
					tBack = b.textureBack;
					tTop = b.textureTop;
					tBottom = b.textureBottom;
				}
			} else {
				// normal image
				tLeft = b.textureLeft;
				tRight = b.textureRight;
				tFront = b.textureFront;
				tBack = b.textureBack;
				tTop = b.textureTop;
				tBottom = b.textureBottom;
			}
			
			// calculate the xoff and zoff
			float xOff = (xoff * Chunk.x) + i;
			float zOff = (zoff * Chunk.z) + k;
			// if we are solid then add to the solid arrays
			// if we are transparent then add to the trans arrays
			if (b.getRendermode() == RENDERMODE.SOLID) {
				// add the left face vertices and UVs if there is no block to the left
				if (left) {
					byte w = s.chunk.getLightLevel(xOff-1, j, zOff);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsLeftComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, w, tLeft));
				}
				// add the right face vertices and UVs if there is no block to the right
				if (right) {
					byte w = s.chunk.getLightLevel(xOff+1, j, zOff);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsRightComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, w, tRight));
				}
				// add the front face vertices and UVs if there is no block to the front
				if (front) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff+1);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsFrontComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, w, tFront));
				}
				// add the back face vertices and UVs if there is no block to the back
				if (back) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff-1);
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBackComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, w, tBack));
				}
				// add the top face vertices and UVs if there is no block to the top
				if (top) {
					// make sure we don't go outside the array
					int j1 = j+1;
					if (j1 >= Chunk.y)
						j1 = Chunk.y-1;
					byte w = lightLevel[i][j1][k];
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsTopComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, w, tTop));
				}
				// add the bottom face vertices and UVs if there is no block to the bottom
				if (bottom) {
					// make sure we don't go outside the array
					int j1 = j-1;
					if (j1 < 0)
						j1 = 0;
					byte w = lightLevel[i][j1][k];
					verts = addArrays(verts, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBottomComplete, i, j, k));
					uvs = addArraysUV(uvs, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, w, tBottom));
				}
			} else {
				// add the left face vertices and UVs if there is no block to the left but this time is transparent
				if (left) {
					byte w = s.chunk.getLightLevel(xOff-1, j, zOff);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsLeftComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, w, tLeft));
				}
				// add the right face vertices and UVs if there is no block to the right but this time is transparent
				if (right) {
					byte w = s.chunk.getLightLevel(xOff+1, j, zOff);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsRightComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, w, tRight));
				}
				// add the front face vertices and UVs if there is no block to the front but this time is transparent
				if (front) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff+1);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsFrontComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, w, tFront));
				}
				// add the back face vertices and UVs if there is no block to the back but this time is transparent
				if (back) {
					byte w = s.chunk.getLightLevel(xOff, j, zOff-1);
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBackComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, w, tBack));
				}
				// add the top face vertices and UVs if there is no block to the top but this time is transparent
				if (top) {
					// make sure we can't get outsidey array
					int j1 = j+1;
					if (j1 >= Chunk.y)
						j1 = Chunk.y-1;
					byte w = lightLevel[i][j1][k];
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsTopComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, w, tTop));
				}
				// add the bottom face vertices and UVs if there is no block to the bottom but this time is transparent
				if (bottom) {
					// no allowed array time
					int j1 = j-1;
					if (j1 < 0)
						j1 = 0;
					byte w = lightLevel[i][j1][k];
					vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(MeshStore.vertsBottomComplete, i, j, k));
					uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, w, tBottom));
				}
			}
		} else {
			// add the verts and uvs for this special texture
			byte w = lightLevel[i][j][k];
			vertsTrans = addArraysTrans(vertsTrans, ChunkBuilder.updateVertexTranslationCompress(b.getSpecialVerts(), i, j, k));
			uvsTrans = addArraysTransUV(uvsTrans, MeshStore.updateCompression(b.getSpecialTextures(), w,b.textureFront));
		}
		return data;
		//blocksModels[i][j][k] = MeshStore.models.get(VoxelWorld.createSixBooleans(left, right, front, back, top, bottom));
	}
	
	/**
	 * add to the priority mesher list
	 */
	public void remeshPRI() {
		Chunk.meshables4.add(this);
	}
	
	/**
	 * remeshes this chunk
	 */
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
		// make sure we don't remesh when meshing
		if (isMeshing)
			return true;
		// reset variables
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
					// apply mesh errors to mesh errors
					chunkErrors |= mesh(i, j, k);
				}
			}
		}
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
		// these are all the same for each direction.
		// right
		if ((chunkErrors & right) != right) {
			Chunk c = s.chunk.getChunk(xoff+1, zoff);
			// chunks can't be null but this has happened (since if chunk errors doesn't have a right in it
			// it should exist)
			if (c == null) {
				System.err.println("CHUNK MESHER ISSUE");
				System.out.flush();
			} else {
				// if the chunk to the right had a null chunk to its left
				// (that being this chunk)
				// then we need to remeshed it
				// this is the same for the other ones as well.
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
		
		int czx = x*cx;
		int czz = z*cz;
		
		// create the transformation matrix based on localized position
		Matrix4f trans = Maths.createTransformationMatrixCube(czx,0,czz);
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
	 * This method is the same thing as the regular renderer execpt it doesn't mesh and it
	 * handles all transparent objects.
	 */
	public void renderSpecial(VoxelShader shader, Matrix4f view, int cx, int cz) {
		// same as above but this time we render the transparent vertices after the non transparent verts
		// this prevents being able to see inside chunks through transparent objects.
		if (blocks == null)
			return;
		if (rawIDTrans == null)
			return;
		
		GL30.glBindVertexArray(rawIDTrans.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		int czx = x*cx;
		int czz = z*cz;
		
		Matrix4f trans = Maths.createTransformationMatrixCube(czx,0,czz);
		Matrix4f.mul(view, trans, trans);
		shader.loadTransformationMatrix(trans);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, rawIDTrans.getVertexCount());
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * Does block updates.
	 */
	public void fixedUpdate() {
		// pick a random block in our chunk
		int xz = s.random.nextInt(Chunk.x);
		int yz = s.random.nextInt(Chunk.y);
		int zz = s.random.nextInt(Chunk.z);
		if (blocks == null)
			return;
		// run the tick function.
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
	
	/**
	 * returns the raw block at a point (including block state)
	 */
	public short getBlockRaw(int x, int y, int z) {
		// adjusts for negative integers.
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		// make sure we are on the right block
		if (x >= Chunk.x || y >= Chunk.y || z >= Chunk.z || y < 0)
			return 0;
		if (blocks == null)
			return 0;
		return blocks[x][y][z];
	}
	
	/**
	 * returns the block at a pos
	 */
	public short getBlock(int x, int y, int z) {
		return (short) (getBlockRaw(x, y, z) & 0xFFF);
	}
	
	/**
	 * returns the block state at a pos
	 */
	public byte getBlockState(int x, int y, int z) {
		return (byte) (getBlockRaw(x, y, z) >> 12);
	}
	
	/**
	 * Returns compressed light level.
	 * First 4 bits is sunlight, last 4 bits is torch light.
	 */
	public byte getLightLevel(int x, int y, int z) {
		// again keep in bounds
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return lightLevel[x][y][z];
	}
	
	/**
	 * returns the torch light from a pos
	 */
	public byte getLightLevelTorch(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return (byte) (lightLevel[x][y][z] & 0x0F);
	}
	
	/**
	 * returns the sun light level at a pos
	 */
	public byte getLightLevelSun(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z || y < 0)
			return 0;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		return (byte) (lightLevel[x][y][z] & 0xF0);
	}
	
	/**
	 * returns true if a pos is underground.
	 */
	public boolean isBlockUnderGround(int x, int y, int z) {
		if (x >= Chunk.x || y >= (Chunk.y-1) || z >= Chunk.z)
			return false;
		if (y < 0)
			return true;
		if (x < 0)
			x *= -1;
		if (z < 0)
			z *= -1;
		if (y == 127)
			return false;
		return (short) (blocks[x][y + 1][z] & 0xFFF) == 0 ? false : true;
	}
	
	/**
	 * gets the height of the world from a pos
	 */
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
		// keep the y in bounds
		if (y >= Chunk.y)
			y = Chunk.y-1;
		if (y < 0)
			y = 0;
		lightLevel[x][y][z] = (byte) (level & 0xFF);
	}
	
	/**
	 * sets the light level at a pos
	 */
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
		// clear the light level
		lightLevel[x][y][z] &= 0x0F;
		// set it.
		lightLevel[x][y][z] |= ((level & 0xF) << 4);
	}
	
	/**
	 * set the light level for torch at a pos
	 */
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
		// clear the light level
		lightLevel[x][y][z] &= 0xF0;
		// assign it
		lightLevel[x][y][z] |= ((level & 0xF));
	}
	
	/**
	 * sets a raw block (4 most sig bits are state last 12 are block)
	 */
	public void setBlockRaw(int x, int y, int z, int rx, int rz, int block) {
		// update the block on the server
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
		// call the block breaked
		Block.blocks.get((short) (blocks[x][y][z] & 0xFFF)).onBlockBreaked(rx, y, rz, s);
		// can't replace block will
		if ((blocks[x][y][z] & 0xFFF) != Block.WILL)
			blocks[x][y][z] = (short) (block);
		// place the block
		if (block != 0)
			Block.blocks.get((short) block).onBlockPlaced(rx, y, rz, s);
	}
	
	/**
	 * same as set block raw but don't call the server about it.
	 */
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
	
	/**
	 * sets only the block at a pos
	 */
	public void setBlock(int x, int y, int z, int rx, int rz, int block) {
		setBlockRaw(x, y, z, rx, rz, (block & 0xFFFF));
	}
	
	/**
	 * doesn't call the server about setting a block but sets the block at a pos
	 */
	public void setBlockServer(int x, int y, int z, int rx, int rz, int block) {
		setBlockRawServer(x, y, z, rx, rz, (block & 0xFFFF));
	}
	
	/**
	 * sets the block state at a pos
	 */
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
