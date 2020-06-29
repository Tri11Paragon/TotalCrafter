package com.brett.world.chunks;

import java.util.Arrays;

import com.brett.world.block.Block;

/**
* @author Brett
* @date Jun. 28, 2020
*/

public class Chunk {
	
	public ShortBlockStorage blocks = new ShortBlockStorage();
	public ByteBlockStorage lightLevel = new ByteBlockStorage();
	public ByteBlockStorage lights = new ByteBlockStorage();
	
	public float[] positions;
	public float[] data;
	
	public int x_pos,y_pos,z_pos;
	public int lastIndex;
	public int lastIndexData;
	
	public Chunk(short[] blocks, byte[] lightLevel, byte[] lights, int x_pos, int y_pos, int z_pos) {
		this.blocks.blocks = blocks;
		this.lightLevel.blocks = lightLevel;
		this.lights.blocks = lights;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
	}
	
	public float[][] meshChunk(int x_pos, int y_pos, int z_pos, ShortBlockStorage blocks, ByteBlockStorage lightlevels) {
		float[] positions = new float[4096];
		float[] data = new float[4096];
		lastIndex = 0;
		lastIndexData = 0;

		for (int i = 0; i < ShortBlockStorage.SIZE; i++) {
			for (int j = 0; j < ShortBlockStorage.SIZE; j++) {
				for (int k = 0; k < ShortBlockStorage.SIZE; k++) {
					short block = blocks.get(i, j, k);
					if (block == Block.AIR)
						continue;
					
				}
			}
		}
		
		return new float[][] { positions, data };
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

		System.arraycopy(array2, 0, rtv, lastIndex, array2.length);
		lastIndexData += array2.length;

		return rtv;
	}
	
}
