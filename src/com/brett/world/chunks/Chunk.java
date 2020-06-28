package com.brett.world.chunks;

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
	
	public Chunk(short[] blocks, byte[] lightLevel, byte[] lights, int x_pos, int y_pos, int z_pos) {
		this.blocks.blocks = blocks;
		this.lightLevel.blocks = lightLevel;
		this.lights.blocks = lights;
		this.x_pos = x_pos;
		this.y_pos = y_pos;
		this.z_pos = z_pos;
	}
	
	
	
}
