package com.brett.world.chunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.engine.data.datatypes.Face;
import com.brett.engine.data.datatypes.VAO;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.WorldShader;
import com.brett.engine.tools.Maths;
import com.brett.utils.NdHashMap;
import com.brett.world.GameRegistry;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.RenderMode;
import com.brett.world.chunks.data.BlockStorage;
import com.brett.world.mesh.MeshStore;

/**
 * @author Brett
 * @date Jun. 28, 2020
 */

public class Chunk {

	public static final byte LEFT = 0b000001;
	public static final byte RIGHT = 0b000010;
	public static final byte BACK = 0b000100;
	public static final byte FRONT = 0b001000;
	public static final byte TOP = 0b010000;
	public static final byte BOTTOM = 0b100000;
	
	public static final int SIZE = 16;

	public BlockStorage blocks = new BlockStorage();
	public ByteBlockStorage lightLevel = new ByteBlockStorage();
	public NdHashMap<Integer, Byte> lights = new NdHashMap<Integer, Byte>();

	public VAO vao;
	public float[] positions;
	public float[] data;
	public float[] normals;

	public int x_pos, y_pos, z_pos;

	public volatile int lastIndex;
	public volatile int lastIndexData;
	public volatile int lastIndexNormal;

	public volatile byte chunkInfo = 0;
	public volatile boolean hasMeshed = false;
	public volatile boolean isEmpty = false;
	public volatile boolean isMeshing = false;
	public volatile boolean yesMan = false;
	public volatile boolean waitingForMesh = false;

	public World world;

	/*public Chunk(World world, int[][][] blocks, byte[] lightLevel, byte[] lights, int x_pos, int y_pos, int z_pos) {
		this.blocks.blocks = blocks;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
		this.world = world;
	}*/

	public Chunk(World world, BlockStorage blocks, ByteBlockStorage lightLevel, int x_pos, int y_pos, int z_pos) {
		if (blocks != null)
			this.blocks = blocks;
		if (lightLevel != null)
			this.lightLevel = lightLevel;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
		this.world = world;
	}

	public void iNeedMesh() {
		if (isMeshing || waitingForMesh)
			return;
		//long start = System.nanoTime();
		if (hasMeshed) {
		//if ((chunkInfo & LEFT) == LEFT) {
			Chunk dc = world.getChunk(x_pos - 1, y_pos, z_pos);
			if (dc == null)
				return;
		//}
		
		//if ((chunkInfo & RIGHT) == RIGHT) {
			Chunk hc = world.getChunk(x_pos + 1, y_pos, z_pos);
			if (hc == null)
				return;
		//}
		
		//if ((chunkInfo & FRONT) == FRONT) {
			Chunk ch = world.getChunk(x_pos, y_pos, z_pos + 1);
			if (ch == null)
				return;
		//}
		
		//if ((chunkInfo & BACK) == BACK) {
			Chunk cg = world.getChunk(x_pos, y_pos, z_pos - 1);
			if (cg == null)
				return;
		//}
		
		//if ((chunkInfo & TOP) == TOP) {
			Chunk cd = world.getChunk(x_pos, y_pos + 1, z_pos);
			if (cd == null)
				return;
		//}
		
		//if ((chunkInfo & BOTTOM) == BOTTOM) {
			Chunk cr = world.getChunk(x_pos, y_pos - 1, z_pos);
			if (cr == null)
				return;
			//System.out.println("GQR " + dc + " " + hc + " " + ch + " " + cg + " " + cd + " " + cr);
		//}
		} 
		
		isMeshing = true;
		
		lastIndex = 0;
		lastIndexData = 0;
		lastIndexNormal = 0;
		chunkInfo = 0;
		positions = new float[0];
		data = new float[0];
		normals = new float[0];

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				for (int k = 0; k < SIZE; k++) {
					short block = blocks.get(i, j, k);
					if (block == Block.AIR)
						continue;

					Block b = GameRegistry.getBlock(block);

					int wx = i + this.x_pos * 16;
					int wy = j + this.y_pos * 16;
					int wz = k + this.z_pos * 16;

					RenderMode leftR = world.getRenderModeNull(wx - 1, wy, wz);
					RenderMode rightR = world.getRenderModeNull(wx + 1, wy, wz);
					RenderMode frontR = world.getRenderModeNull(wx, wy, wz + 1);
					RenderMode backR = world.getRenderModeNull(wx, wy, wz - 1);
					RenderMode topR = world.getRenderModeNull(wx, wy + 1, wz);
					RenderMode bottomR = world.getRenderModeNull(wx, wy - 1, wz);

					if (leftR == null) {
						chunkInfo |= LEFT;
					} else {
						if (leftR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsLeftComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, world.getLightLevel(wx - 1, wy, wz), b.textureLeft));
							normals = addArrayNormal(normals, MeshStore.normalsLeft);
						}
					}

					if (rightR == null) {
						chunkInfo |= RIGHT;
					} else {
						if (rightR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsRightComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, world.getLightLevel(wx + 1, wy, wz), b.textureRight));
							normals = addArrayNormal(normals, MeshStore.normalsRight);
						}
					}

					if (frontR == null) {
						chunkInfo |= FRONT;
					} else {
						if (frontR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsFrontComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, world.getLightLevel(wx, wy, wz + 1), b.textureFront));
							normals = addArrayNormal(normals, MeshStore.normalsFront);
						}
					}

					if (backR == null) {
						chunkInfo |= BACK;
					} else {
						if (backR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsBackComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, world.getLightLevel(wx, wy, wz - 1), b.textureBack));
							normals = addArrayNormal(normals, MeshStore.normalsBack);
						}
					}

					if (topR == null) {
						chunkInfo |= TOP;
					} else {
						if (topR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsTopComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, world.getLightLevel(wx, wy + 1, wz), b.textureTop));
							normals = addArrayNormal(normals, MeshStore.normalsTop);
						}
					}

					if (bottomR == null) {
						chunkInfo |= BOTTOM;
					} else {
						if (bottomR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsBottomComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, world.getLightLevel(wx, wy - 1, wz), b.textureBottom));
							normals = addArrayNormal(normals, MeshStore.normalsBottom);
						}
					}

				}
			}
		}
		positions = Arrays.copyOfRange(positions, 0, lastIndex);
		data = Arrays.copyOfRange(data, 0, lastIndexData);
		normals = Arrays.copyOfRange(normals, 0, lastIndexNormal);
		
		waitingForMesh = true;
		isMeshing = false;
		yesMan = true;
		//long end = System.nanoTime();
		//System.out.println("Took: " + (end - start) + "ns or " + ((end-start)/1000000) + "ms to mesh chunk");
	}
	
	public void meshChunk(boolean isDoneMeshing) {
		if (isMeshing || waitingForMesh)
			return;

		
		isMeshing = true;
		lastIndex = 0;
		lastIndexData = 0;
		lastIndexNormal = 0;
		chunkInfo = 0;
		positions = new float[0];
		data = new float[0];
		normals = new float[0];

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				for (int k = 0; k < SIZE; k++) {
					short block = blocks.get(i, j, k);
					if (block == Block.AIR)
						continue;

					Block b = GameRegistry.getBlock(block);

					int wx = i + this.x_pos * 16;
					int wy = j + this.y_pos * 16;
					int wz = k + this.z_pos * 16;

					RenderMode leftR = world.getRenderModeNull(wx - 1, wy, wz);
					RenderMode rightR = world.getRenderModeNull(wx + 1, wy, wz);
					RenderMode frontR = world.getRenderModeNull(wx, wy, wz + 1);
					RenderMode backR = world.getRenderModeNull(wx, wy, wz - 1);
					RenderMode topR = world.getRenderModeNull(wx, wy + 1, wz);
					RenderMode bottomR = world.getRenderModeNull(wx, wy - 1, wz);

					if (leftR == null) {
						chunkInfo |= LEFT;
					} else {
						if (leftR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsLeftComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, world.getLightLevel(wx - 1, wy, wz), b.textureLeft));
							normals = addArrayNormal(normals, MeshStore.normalsLeft);
						}
					}

					if (rightR == null) {
						chunkInfo |= RIGHT;
					} else {
						if (rightR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsRightComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, world.getLightLevel(wx + 1, wy, wz), b.textureRight));
							normals = addArrayNormal(normals, MeshStore.normalsRight);
						}
					}

					if (frontR == null) {
						chunkInfo |= FRONT;
					} else {
						if (frontR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsFrontComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, world.getLightLevel(wx, wy, wz + 1), b.textureFront));
							normals = addArrayNormal(normals, MeshStore.normalsFront);
						}
					}

					if (backR == null) {
						chunkInfo |= BACK;
					} else {
						if (backR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsBackComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, world.getLightLevel(wx, wy, wz - 1), b.textureBack));
							normals = addArrayNormal(normals, MeshStore.normalsBack);
						}
					}

					if (topR == null) {
						chunkInfo |= TOP;
					} else {
						if (topR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsTopComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, world.getLightLevel(wx, wy + 1, wz), b.textureTop));
							normals = addArrayNormal(normals, MeshStore.normalsTop);
						}
					}

					if (bottomR == null) {
						chunkInfo |= BOTTOM;
					} else {
						if (bottomR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsBottomComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, world.getLightLevel(wx, wy - 1, wz), b.textureBottom));
							normals = addArrayNormal(normals, MeshStore.normalsBottom);
						}
					}

				}
			}
		}
		
		if ((chunkInfo & LEFT) != LEFT) {
			Chunk c = world.getChunk(x_pos - 1, y_pos, z_pos);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & RIGHT) == RIGHT) {
					c.meshChunk();
				}
			}
		}
		
		if ((chunkInfo & RIGHT) != RIGHT) {
			Chunk c = world.getChunk(x_pos + 1, y_pos, z_pos);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & LEFT) == LEFT) {
					c.meshChunk();
				}
			}
		}
		
		if ((chunkInfo & FRONT) != FRONT) {
			Chunk c = world.getChunk(x_pos, y_pos, z_pos + 1);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & BACK) == BACK) {
					c.meshChunk();
				}
			}
		}
		
		if ((chunkInfo & BACK) != BACK) {
			Chunk c = world.getChunk(x_pos, y_pos, z_pos - 1);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & FRONT) == FRONT) {
					c.meshChunk();
				}
			}
		}
		
		if ((chunkInfo & TOP) != TOP) {
			Chunk c = world.getChunk(x_pos, y_pos + 1, z_pos);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & BOTTOM) == BOTTOM) {
					c.meshChunk();
				}
			}
		}
		
		if ((chunkInfo & BOTTOM) != BOTTOM) {
			Chunk c = world.getChunk(x_pos, y_pos - 1, z_pos);
			if (c == null) {
				
			} else {
				if ((c.chunkInfo & TOP) == TOP) {
					c.meshChunk();
				}
			}
		}
		
		positions = Arrays.copyOfRange(positions, 0, lastIndex);
		data = Arrays.copyOfRange(data, 0, lastIndexData);
		normals = Arrays.copyOfRange(normals, 0, lastIndexNormal);
		yesMan = true;
	}

	public Chunk meshChunk() {
		meshChunk(true);
		isMeshing = false;
		waitingForMesh = true;
		return this;
	}
	
	/**
	 * Proof of concept greedy mesher.
	 */
	@SuppressWarnings("unused")
	public void greedyMesh() {
		meshChunk(false);
		lastIndex = 0;
		lastIndexData = 0;

		float[] newpos = new float[0];
		float[] nwedata = new float[0];

		HashMap<Float, ArrayList<Face>> yp = new HashMap<Float, ArrayList<Face>>();
		HashMap<Float, ArrayList<Face>> xp = new HashMap<Float, ArrayList<Face>>();
		HashMap<Float, ArrayList<Face>> zp = new HashMap<Float, ArrayList<Face>>();

		int index = 0;
		while (index < positions.length) {
			float[] face = Arrays.copyOfRange(positions, index, index + 18);
			float[] faceData = Arrays.copyOfRange(data, index / 3, index / 3 + 6);
			int textureM = (((int) faceData[0]) >> 10) & 0x2FF;
			
			float[] cy = findYCommon(face);
			if (cy[0] == 1) {
				if (yp.containsKey(cy[1])) {
					yp.get(cy[1]).add(new Face(face, faceData, textureM, cy[1] % 2 == 0 ? Face.TOP : Face.BOTTOM));
				} else {
					ArrayList<Face> fl = new ArrayList<Face>();
					fl.add(new Face(face, faceData, textureM, cy[1] % 2 == 0 ? Face.TOP : Face.BOTTOM));
					yp.put(cy[1], fl);
				}
			} else {
				float[] cx = findXCommon(face);
				if (cx[0] == 1) {
					if (xp.containsKey(cx[1])) {
						xp.get(cx[1]).add(new Face(face, faceData, textureM, cx[1] % 2 == 0 ? Face.LEFT : Face.RIGHT));
					} else {
						ArrayList<Face> fl = new ArrayList<Face>();
						fl.add(new Face(face, faceData, textureM, cx[1] % 2 == 0 ? Face.LEFT : Face.RIGHT));
						xp.put(cx[1], fl);
					}
				} else {
					float[] cz = findZCommon(face);
					if (cz[0] == 1) {
						if (zp.containsKey(cz[1])) {
							zp.get(cz[1]).add(new Face(face, faceData, textureM, cz[1] % 2 == 0 ? Face.BACK : Face.FRONT));
						} else {
							ArrayList<Face> fl = new ArrayList<Face>();
							fl.add(new Face(face, faceData, textureM, cz[1] % 2 == 0 ? Face.BACK : Face.FRONT));
							zp.put(cz[1], fl);
						}
					} else {
						newpos = addArray(newpos, face);
						nwedata = addArrayData(nwedata, faceData);
					}
				}
			}
			index += 18;
		}

		Iterator<Entry<Float, ArrayList<Face>>> yit = yp.entrySet().iterator();

		while (yit.hasNext()) {
			Entry<Float, ArrayList<Face>> entry = yit.next();
			ArrayList<Face> list = entry.getValue();

			
		}

		Iterator<Entry<Float, ArrayList<Face>>> xit = xp.entrySet().iterator();

		while (xit.hasNext()) {
			Entry<Float, ArrayList<Face>> entry = xit.next();
			ArrayList<Face> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {

			}
		}

		Iterator<Entry<Float, ArrayList<Face>>> zit = zp.entrySet().iterator();

		while (zit.hasNext()) {
			Entry<Float, ArrayList<Face>> entry = zit.next();
			ArrayList<Face> list = entry.getValue();

		}

		positions = newpos;
		data = nwedata;

		isMeshing = false;
		waitingForMesh = true;
	}

	public float[] findYCommon(float[] data) {
		float commons = 0;
		float found = 0;
		float y1 = data[1];
		float y2 = data[4];
		float y3 = data[7];
		float y4 = data[10];
		float y5 = data[13];
		float y6 = data[16];
		if (y1 == y2 && y1 == y3 && y1 == y4 && y1 == y5 && y1 == y6) {
			found = 1;
			commons = y1;
		}
		return new float[] { found, commons };
	}

	public float[] findXCommon(float[] data) {
		float commons = 0;
		float found = 0;
		float x1 = data[0];
		float x2 = data[3];
		float x3 = data[6];
		float x4 = data[9];
		float x5 = data[12];
		float x6 = data[15];
		if (x1 == x2 && x1 == x3 && x1 == x4 && x1 == x5 && x1 == x6) {
			found = 1;
			commons = x1;
		}
		return new float[] { found, commons };
	}

	public float[] findZCommon(float[] data) {
		float commons = 0;
		float found = 0;
		float z1 = data[2];
		float z2 = data[5];
		float z3 = data[8];
		float z4 = data[11];
		float z5 = data[14];
		float z6 = data[17];
		if (z1 == z2 && z1 == z3 && z1 == z4 && z1 == z5 && z1 == z6) {
			found = 1;
			commons = z1;
		}
		return new float[] { found, commons };
	}
	
	public void render(WorldShader shader, int cx, int cy, int cz) {
		if (waitingForMesh && !isMeshing) {
			isMeshing = true;
			isEmpty = false;
			//if (vao != null)
			//	vao = ScreenManager.loader.deleteVAO(vao);
			
			if (vao == null) {
				if (positions != null && data != null) {
					if (positions.length > 0 && data.length > 0)
						vao = ScreenManager.loader.loadToVAOChunk(positions, normals, data);
					else
						isEmpty = true;
				}
			} else {
				if (positions != null && data != null) {
					if (positions.length > 0 && data.length > 0)
						ScreenManager.loader.updateChunkVAO(vao, positions, normals, data);
					else
						isEmpty = true;
				}
			}
			
			if (yesMan) {
				positions = new float[0];
				normals = new float[0];
				data = new float[0];
			}

			//positions = null;
			//data = null;
			hasMeshed = true;
			yesMan = false;
			waitingForMesh = false;
			isMeshing = false;
		}

		if (!isEmpty && vao != null) {
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);

			shader.loadTranslationMatrix(Maths.createTransformationMatrixCube(cx * 16, cy * 16, cz * 16));
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());

			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}
	
	protected void finalize() throws Throwable  
	{  
		super.finalize();
		if (vao != null)
			World.deleteVAOS.add(vao);
		vao = null;
	}
	
	public void qdelete() {
		//if (vao != null)
		//	World.deleteVAOS.add(vao);
		//vao = null;
		delete();
	}

	public void delete() {
		isEmpty = true;
		isMeshing = true;
		//vao = ScreenManager.loader.deleteVAO(vao);
		if (vao != null)
			World.deleteVAOS.add(vao);
		vao = null;
		positions = null;
		data = null;
	}
	
	public static float[] updateVertexTranslation(float[] verts, int xTrans, int yTrans, int zTrans) {
		float[] newVerts = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3) {
			newVerts[i] = verts[i] + xTrans;
			newVerts[i + 1] = verts[i + 1] + yTrans;
			newVerts[i + 2] = verts[i + 2] + zTrans;
		}
		return newVerts;
	}

	public float[] addArray(float[] array1, float[] array2) {
		float[] rtv = array1;

		if (lastIndex + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length) * 2);

		System.arraycopy(array2, 0, rtv, lastIndex, array2.length);
		lastIndex += array2.length;

		return rtv;
	}

	public float[] addArrayData(float[] array1, float[] array2) {
		float[] rtv = array1;

		if (lastIndexData + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length) * 2);

		System.arraycopy(array2, 0, rtv, lastIndexData, array2.length);
		lastIndexData += array2.length;

		return rtv;
	}
	
	public float[] addArrayNormal(float[] array1, float[] array2) {
		float[] rtv = array1;

		if (lastIndexNormal + array2.length >= array1.length)
			rtv = Arrays.copyOf(array1, (array1.length + array2.length) * 2);

		System.arraycopy(array2, 0, rtv, lastIndexNormal, array2.length);
		lastIndexNormal += array2.length;

		return rtv;
	}

}
