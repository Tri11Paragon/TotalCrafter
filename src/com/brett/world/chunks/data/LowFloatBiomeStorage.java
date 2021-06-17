package com.brett.world.chunks.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.biome.BiomePalette;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class LowFloatBiomeStorage {
	
	public BiomePalette palette = new BiomePalette();
	public byte[][][] biomeType = new byte[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
	public byte[][][] biomeBlend = new byte[Chunk.SIZE][Chunk.SIZE][Chunk.SIZE];
	
	public LowFloatBiomeStorage() {
		
	}
	
	public void save(DataOutputStream dos) {
		palette.save(dos);
		try {
			for (int i = 0; i < Chunk.SIZE; i++) {
				for (int j = 0; j < Chunk.SIZE; j++) {
					for (int k = 0; k < Chunk.SIZE; k++) {
						dos.writeByte(biomeType[i][j][k]);
						dos.writeByte(biomeBlend[i][j][k]);
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void load(DataInputStream dis) {
		palette.load(dis);
		try {
			for (int i = 0; i < Chunk.SIZE; i++) {
				for (int j = 0; j < Chunk.SIZE; j++) {
					for (int k = 0; k < Chunk.SIZE; k++) {
						biomeType[i][j][k] = dis.readByte();
						biomeBlend[i][j][k] = dis.readByte();
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public LowFloatBiomeStorage(BiomePalette palette) {
		this.palette = palette;
	}
	
	public LowFloatBiomeStorage setPalette(BiomePalette palette) {
		this.palette = palette;
		return this;
	}
	
	public byte getTypeWorld(int x, int y, int z) {
		return biomeType[x & 0xF][y & 0xF][z & 0xF];
	}
	
	public void setTypeWorld(int x, int y, int z, byte id) {
		biomeType[x & 0xF][y & 0xF][z & 0xF] = id;
	}
	
	public void setTypeWorld(int x, int y, int z, int id) {
		biomeType[x & 0xF][y & 0xF][z & 0xF] = (byte) id;
	}
	
	public byte getMixWorld(int x, int y, int z) {
		return biomeBlend[x & 0xF][y & 0xF][z & 0xF];
	}
	
	public void setMixWorld(int x, int y, int z, byte amount) {
		biomeBlend[x & 0xF][y & 0xF][z & 0xF] = amount;
	}
	
	public void setMixWorld(int x, int y, int z, int amount) {
		biomeBlend[x & 0xF][y & 0xF][z & 0xF] = (byte) amount;
	}
	
}
