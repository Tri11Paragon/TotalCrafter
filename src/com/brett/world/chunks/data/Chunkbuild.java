package com.brett.world.chunks.data;

/**
* @author Brett
* @date Jul. 1, 2020
*/

public class Chunkbuild {
	
	/**
	 * 	public void greedyMesh2() {
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
	 */
	
	/**
	 * Proof of concept greedy mesher.
	 */
	/*public void greedyMesh() {
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
				for(int k = i; k < list.size(); k++) {
					int textureIN = ((int) list.get(k)[1][0] >> 10) & 0x2FF;
					if ((k-i) > 15) {
						i = k;
						break;
					}
					if (textureM != textureIN) {
						i = k;
						break;
					}
					for (int p = 0; p < list.get(k)[0].length; p += 3) {
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
				System.out.println(xmin + " " + xmax + " " + zmin + " " + zmax);
				if (entry.getKey() % 2 == 0)
					newpos = addArray(newpos, MeshStore.createTopComplete(entry.getKey(), xmin, xmax, zmin, zmax));
				else
					newpos = addArray(newpos, MeshStore.createBottomComplete(entry.getKey(), xmin, xmax, zmin, zmax));

				nwedata = addArrayData(nwedata, list.get(i - 1)[1]);
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
	
	/**
	 * for (int i = 0; i < list.size(); i++) {

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

				int mindex = i+1;
				w1: while (mindex < list.size()) {
					int textureIN = ((int) list.get(mindex)[1][0] >> 10) & 0x2FF;
					
					if (mindex - i > 16)
						break;
					
					if (textureIN == textureM) {
						for (int k = 0; k < list.get(mindex)[0].length; k += 3) {
							float x = list.get(mindex)[0][k];
							float z = list.get(mindex)[0][k + 2];
							
							if (x < xmin)
								xmin = x;
							if (x > xmax)
								xmax = x;
							if (z < zmin)
								zmin = z;
							if (z > zmax)
								zmax = z;
							float xdiffm = Math.abs(xmin - x);
							float xdiffx = Math.abs(xmax - x);
							float zdiffm = Math.abs(zmin - z);
							float zdiffx = Math.abs(zmax - z);
							if (xdiffm > 1 && xdiffx > 1) {
								xmin -= (xdiffm-1);
								break;
							}
						}
					} else 
						break w1;
					mindex++;
				}
				i = mindex;
				if (entry.getKey() % 2 == 0)
					newpos = addArray(newpos, MeshStore.createTopComplete(entry.getKey(), xmin, xmax, zmin, zmax));
				else
					newpos = addArray(newpos, MeshStore.createBottomComplete(entry.getKey(), xmin, xmax, zmin, zmax));

				nwedata = addArrayData(nwedata, list.get(mindex - 1)[1]);
			}

	 */
	
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
}
