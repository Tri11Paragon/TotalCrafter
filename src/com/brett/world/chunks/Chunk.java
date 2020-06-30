package com.brett.world.chunks;

import java.util.Arrays;

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
	
	public Chunk(World world, ShortBlockStorage blocks, ByteBlockStorage lightLevel, ByteBlockStorage lights, int x_pos, int y_pos, int z_pos) {
		this.blocks = blocks;
		this.lightLevel = lightLevel;
		this.lights = lights;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
		this.world = world;
	}

	public void meshChunk() {
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

					//RenderMode blockR = world.getRenderMode(wx, wy, wz);
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
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsLeftComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvLeftCompleteCompress, (byte)15, b.textureLeft));
						}
					}

					if (rightR == null) {
						chunkInfo |= RIGHT;
					} else {
						if (rightR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsRightComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvRightCompleteCompress, (byte)15, b.textureRight));
						}
					}

					if (frontR == null) {
						chunkInfo |= FRONT;
					} else {
						if (frontR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsFrontComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvFrontCompleteCompress, (byte)15, b.textureFront));
						}
					}

					if (backR == null) {
						chunkInfo |= BACK;
					} else {
						if (backR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsBackComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBackCompleteCompress, (byte)15, b.textureBack));
						}
					}

					if (topR == null) {
						chunkInfo |= TOP;
					} else {
						if (topR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsTopComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvTopCompleteCompress, (byte)15, b.textureTop));
						}
					}

					if (bottomR == null) {
						chunkInfo |= BOTTOM;
					} else {
						if (bottomR != RenderMode.SOLID) {
							positions = addArray(positions, updateVertexTranslation(MeshStore.vertsBottomComplete, i, j, k));
							data = addArrayData(data, MeshStore.updateCompression(MeshStore.uvBottomCompleteCompress, (byte)15, b.textureBottom));
						}
					}

				}
			}
		}
		
		isMeshing = false;
		waitingForMesh = true;
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
			
			shader.loadTranslationMatrix(Maths.createTransformationMatrixCube(x_pos*16, y_pos*16, z_pos*16));
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}
	
	public static float[] updateVertexTranslation(float[] verts, int xTrans, int yTrans, int zTrans) {
		float[] newVerts = new float[verts.length];
		for (int i = 0; i < verts.length; i+=3) {
			newVerts[i] = verts[i] + xTrans;
			newVerts[i+1] = verts[i+1] + yTrans;
			newVerts[i+2] = verts[i+2] + zTrans;
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
