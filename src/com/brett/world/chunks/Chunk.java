package com.brett.world.chunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.engine.data.datatypes.VAO;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.VoxelShader;
import com.brett.engine.tools.Maths;
import com.brett.world.GameRegistry;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.data.RenderMode;
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

	public ShortBlockStorage blocks = new ShortBlockStorage();
	public ByteBlockStorage lightLevel = new ByteBlockStorage();
	public ByteBlockStorage lights = new ByteBlockStorage();

	public VAO vao;
	public float[] positions;
	public float[] data;

	public int x_pos, y_pos, z_pos;

	public int lastIndex;
	public int lastIndexData;

	public byte chunkInfo = 0;
	public boolean isEmpty = false;
	public boolean isMeshing = false;
	public boolean waitingForMesh = false;

	public World world;

	public Chunk(World world, short[][][] blocks, byte[] lightLevel, byte[] lights, int x_pos, int y_pos, int z_pos) {
		this.blocks.blocks = blocks;
		this.lightLevel.blocks = lightLevel;
		this.lights.blocks = lights;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
		this.world = world;
	}

	public Chunk(World world, ShortBlockStorage blocks, ByteBlockStorage lightLevel, ByteBlockStorage lights, int x_pos,
			int y_pos, int z_pos) {
		this.blocks = blocks;
		this.lightLevel = lightLevel;
		this.lights = lights;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
		this.world = world;
	}

	public void meshChunk(boolean isDoneMeshing) {
		if (isMeshing)
			return;

		isMeshing = true;
		lastIndex = 0;
		lastIndexData = 0;
		chunkInfo = 0;
		positions = new float[0];
		data = new float[0];

		for (int i = 0; i < ShortBlockStorage.SIZE; i++) {
			for (int j = 0; j < ShortBlockStorage.SIZE; j++) {
				for (int k = 0; k < ShortBlockStorage.SIZE; k++) {
					short block = blocks.get(i, j, k);
					if (block == Block.AIR)
						continue;

					Block b = GameRegistry.getBlock(block);

					int wx = i + this.x_pos * 16;
					int wy = j + this.y_pos * 16;
					int wz = k + this.z_pos * 16;

					// RenderMode blockR = world.getRenderMode(wx, wy, wz);
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
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress,
									(byte) 15, b.textureLeft));
						}
					}

					if (rightR == null) {
						chunkInfo |= RIGHT;
					} else {
						if (rightR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsRightComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress,
									(byte) 15, b.textureRight));
						}
					}

					if (frontR == null) {
						chunkInfo |= FRONT;
					} else {
						if (frontR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsFrontComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress,
									(byte) 15, b.textureFront));
						}
					}

					if (backR == null) {
						chunkInfo |= BACK;
					} else {
						if (backR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsBackComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress,
									(byte) 15, b.textureBack));
						}
					}

					if (topR == null) {
						chunkInfo |= TOP;
					} else {
						if (topR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsTopComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress,
									(byte) 15, b.textureTop));
						}
					}

					if (bottomR == null) {
						chunkInfo |= BOTTOM;
					} else {
						if (bottomR != RenderMode.SOLID) {
							positions = addArray(positions,
									updateVertexTranslation(MeshStore.vertsBottomComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress,
									(byte) 15, b.textureBottom));
						}
					}

				}
			}
		}

		positions = Arrays.copyOfRange(positions, 0, lastIndex);
		data = Arrays.copyOfRange(data, 0, lastIndexData);

		isMeshing = !isDoneMeshing;
		waitingForMesh = isDoneMeshing;
	}

	public void meshChunk() {
		meshChunk(true);
		isMeshing = false;
		waitingForMesh = true;
	}

	public void greedyMesh2() {
		if (isMeshing)
			return;

		isMeshing = true;
		lastIndex = 0;
		lastIndexData = 0;
		chunkInfo = 0;
		positions = new float[0];
		data = new float[0];

		for (int i = 0; i < ShortBlockStorage.SIZE; i++) {
			for (int j = 0; j < ShortBlockStorage.SIZE; j++) {
				short mainblock = 0;
				for (int k = 0; k < ShortBlockStorage.SIZE; k++) {
					short block = blocks.get(i, j, k);
					if (block == Block.AIR)
						continue;

					if (block != mainblock) {
						mainblock = block;
						continue;
					}

					int wx = i + this.x_pos * 16;
					int wy = j + this.y_pos * 16;
					int wz = k + this.z_pos * 16;

					RenderMode leftR = world.getRenderModeNull(wx - 1, wy, wz);
					RenderMode rightR = world.getRenderModeNull(wx + 1, wy, wz);
					RenderMode frontR = world.getRenderModeNull(wx, wy, wz + 1);
					RenderMode backR = world.getRenderModeNull(wx, wy, wz - 1);
					RenderMode topR = world.getRenderModeNull(wx, wy + 1, wz);
					RenderMode bottomR = world.getRenderModeNull(wx, wy - 1, wz);

					short leftB = world.getBlock(wx - 1, wy, wz);
					short rightB = world.getBlock(wx + 1, wy, wz);
					short frontB = world.getBlock(wx, wy, wz + 1);
					short backB = world.getBlock(wx, wy, wz - 1);
					short topB = world.getBlock(wx, wy + 1, wz);
					short bottomB = world.getBlock(wx, wy - 1, wz);

				}
			}
		}

		positions = Arrays.copyOfRange(positions, 0, lastIndex);
		data = Arrays.copyOfRange(data, 0, lastIndexData);

		isMeshing = false;
		waitingForMesh = true;
	}

	/**
	 * Proof of concept greedy mesher.
	 */
	public void greedyMesh() {
		meshChunk(false);
		lastIndex = 0;
		lastIndexData = 0;

		float[] newpos = new float[0];
		float[] nwedata = new float[0];

		HashMap<Float, ArrayList<float[][]>> yp = new HashMap<Float, ArrayList<float[][]>>();
		HashMap<Float, ArrayList<float[][]>> xp = new HashMap<Float, ArrayList<float[][]>>();
		HashMap<Float, ArrayList<float[][]>> zp = new HashMap<Float, ArrayList<float[][]>>();

		int index = 0;
		while (index < positions.length) {
			float[] face = Arrays.copyOfRange(positions, index, index + 18);
			float[] faceData = Arrays.copyOfRange(data, index / 3, index / 3 + 6);

			float[] cy = findYCommon(face);
			if (cy[0] == 1) {
				if (yp.containsKey(cy[1])) {
					yp.get(cy[1]).add(new float[][] { face, faceData });
				} else {
					ArrayList<float[][]> fl = new ArrayList<float[][]>();
					fl.add(new float[][] { face, faceData });
					yp.put(cy[1], fl);
				}
			} else {
				float[] cx = findXCommon(face);
				if (cx[0] == 1) {
					if (xp.containsKey(cx[1])) {
						xp.get(cx[1]).add(new float[][] { face, faceData });
					} else {
						ArrayList<float[][]> fl = new ArrayList<float[][]>();
						fl.add(new float[][] { face, faceData });
						xp.put(cx[1], fl);
					}
				} else {
					float[] cz = findZCommon(face);
					if (cz[0] == 1) {
						if (zp.containsKey(cz[1])) {
							zp.get(cz[1]).add(new float[][] { face, faceData });
						} else {
							ArrayList<float[][]> fl = new ArrayList<float[][]>();
							fl.add(new float[][] { face, faceData });
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

		Iterator<Entry<Float, ArrayList<float[][]>>> yit = yp.entrySet().iterator();

		while (yit.hasNext()) {
			Entry<Float, ArrayList<float[][]>> entry = yit.next();
			ArrayList<float[][]> list = entry.getValue();

			for (int i = 0; i < list.size(); i++) {

				float xmin = 16;
				float zmin = 16;
				float xmax = 0;
				float zmax = 0;
				float[] l = list.get(i)[0];
				for (int w = 0; w < 18; w += 3) {
					if (l[w] > xmax)
						xmax = l[w];
					if (l[w + 2] > zmax)
						zmax = l[w];
					if (l[w] < xmax)
						xmin = l[w];
					if (l[w + 2] < zmax)
						zmin = l[w];
				}

				int textureM = ((int) list.get(i)[1][0] >> 10) & 0x2FF;

				int mindex = i + 1;
				w1: while (mindex < list.size()) {
					int textureIN = ((int) list.get(mindex)[1][0] >> 10) & 0x2FF;

					if (textureIN == textureM) {
						for (int k = 0; k < list.get(mindex)[0].length; k += 3) {
							float x = list.get(mindex)[0][k];
							float z = list.get(mindex)[0][k + 2];
							float xdiffm = Math.abs(xmin - x);
							float xdiffx = Math.abs(xmax - x);
							float zdiffm = Math.abs(zmin - z);
							float zdiffx = Math.abs(zmax - z);

							if (x < xmin)
								xmin = x;
							if (x > xmax)
								xmax = x;
							if (z < zmin)
								zmin = z;
							if (z > zmax)
								zmax = z;
						}
					} else {
						if ((mindex - i) > 16) {
							mindex -= mindex % 16;
							xmin = 16;
							xmax = 0;
							zmin = 16;
							zmax = 0;
							for (int k = 0; k < mindex-1; k ++) {
								for (int p = 0; p < list.get(p)[0].length; p+=3) {
									float x = list.get(k)[0][p];
									float z = list.get(k)[0][p + 2];
									
									if (x < xmin)
										xmin = x;
									if (x > xmax)
										xmax = x;
									if (z < zmin)
										zmin = z;
									if (z > zmax)
										zmax = z;
								}
							}
						}
						break w1;
					}
					mindex++;
				}
				System.out.println(mindex + " " + i);
				i = mindex;
				if (entry.getKey() % 2 == 0)
					newpos = addArray(newpos, MeshStore.createTopComplete(entry.getKey(), xmin, xmax, zmin, zmax));
				else
					newpos = addArray(newpos, MeshStore.createBottomComplete(entry.getKey(), xmin, xmax, zmin, zmax));

				nwedata = addArrayData(nwedata, list.get(mindex - 1)[1]);
			}

		}

		Iterator<Entry<Float, ArrayList<float[][]>>> xit = xp.entrySet().iterator();

		while (xit.hasNext()) {
			Entry<Float, ArrayList<float[][]>> entry = xit.next();
			ArrayList<float[][]> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {

			}
		}

		Iterator<Entry<Float, ArrayList<float[][]>>> zit = zp.entrySet().iterator();

		while (zit.hasNext()) {
			Entry<Float, ArrayList<float[][]>> entry = zit.next();
			ArrayList<float[][]> list = entry.getValue();

		}

		positions = newpos;
		data = nwedata;

		isMeshing = false;
		waitingForMesh = true;
	}

	/*
	 * for (int i = 0; i < list.size(); i++) {
	 * 
	 * int in = 0; float xmin = 16; float zmin = 16; float xmax = 0; float zmax = 0;
	 * float[] l = list.get(i)[0]; for (int w = 0; w < 18; w += 3) { if (l[w] >
	 * xmax) xmax = l[w]; if (l[w + 2] > zmax) zmax = l[w]; if (l[w] < xmax) xmin =
	 * l[w]; if (l[w + 2] < zmax) zmin = l[w]; }
	 * 
	 * int textureM = ((int) list.get(i)[1][0] >> 10) & 0x2FF;
	 * 
	 * for (int k = i+1; k < list.size(); k++) { try { int textureIN = ((int)
	 * list.get(k)[1][0] >> 10) & 0x2FF;
	 * 
	 * if (textureM == textureIN) { float x = list.get(k)[0][k]; float z =
	 * list.get(k)[0][k + 2];
	 * 
	 * if (x > xmax) xmax = x; if (z > zmax) zmax = z; i = k+1; } else { if (k < 16)
	 * { in = k; i = k+1; break; } else { int j = k; while (j % 16 != 0) j--; xmax =
	 * list.get(k)[0][k]; zmax = list.get(k)[0][k + 2]; in = k; i = k+1; break; } }
	 * } catch (Exception e) {} } if (entry.getKey() % 2 == 0) newpos =
	 * addArray(newpos, MeshStore.createTopComplete(entry.getKey(), xmin, xmax,
	 * zmin, zmax)); else newpos = addArray(newpos,
	 * MeshStore.createBottomComplete(entry.getKey(), xmin, xmax, zmin, zmax));
	 * 
	 * nwedata = addArrayData(nwedata, list.get(in)[1]); }
	 */

	/**
	 * for (int i = 0; i < list.size(); i++) {
	 * 
	 * float xmin = 16; float zmin = 16; float xmax = 0; float zmax = 0; float[] l =
	 * list.get(i)[0]; for (int w = 0; w < 18; w += 3) { if (l[w] > xmax) xmax =
	 * l[w]; if (l[w + 2] > zmax) zmax = l[w]; if (l[w] < xmax) xmin = l[w]; if (l[w
	 * + 2] < zmax) zmin = l[w]; }
	 * 
	 * int textureM = ((int) list.get(i)[1][0] >> 10) & 0x2FF;
	 * 
	 * int mindex = i + 1; boolean broken = false; while (mindex < list.size()) {
	 * int textureIN = ((int) list.get(mindex)[1][0] >> 10) & 0x2FF;
	 * 
	 * if (textureIN == textureM) { for (int k = 0; k < list.get(mindex)[0].length;
	 * k += 3) { float x = list.get(mindex)[0][k]; float z = list.get(mindex)[0][k +
	 * 2]; float xdiffm = Math.abs(xmin - x); float xdiffx = Math.abs(xmax - x);
	 * float zdiffm = Math.abs(zmin - z); float zdiffx = Math.abs(zmax - z);
	 * 
	 * if (xdiffx > 1 || xdiffm > 1) { break; }
	 * 
	 * if (x < xmin) xmin = x; if (x > xmax) xmax = x; if (z < zmin) zmin = z; if (z
	 * > zmax) zmax = z; } } else { //i = mindex; //broken = true; mindex++; break;
	 * } mindex++; } if (!broken) { i = mindex; if (entry.getKey() % 2 == 0) newpos
	 * = addArray(newpos, MeshStore.createTopComplete(entry.getKey(), xmin, xmax,
	 * zmin, zmax)); else newpos = addArray(newpos,
	 * MeshStore.createBottomComplete(entry.getKey(), xmin, xmax, zmin, zmax));
	 * 
	 * nwedata = addArrayData(nwedata, list.get(mindex - 1)[1]); } }
	 */

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

	public void render(VoxelShader shader) {
		if (waitingForMesh && !isMeshing) {
			isMeshing = true;
			isEmpty = false;
			if (vao != null)
				ScreenManager.loader.deleteVAO(vao);

			if (positions.length > 0 && data.length > 0)
				vao = ScreenManager.loader.loadToVAOChunk(positions, data);
			else
				isEmpty = true;

			waitingForMesh = false;
			positions = null;
			data = null;
			isMeshing = false;
		}

		if (!isEmpty && vao != null) {
			GL30.glBindVertexArray(vao.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);

			shader.loadTranslationMatrix(Maths.createTransformationMatrixCube(x_pos * 16, y_pos * 16, z_pos * 16));
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());

			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
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

}
