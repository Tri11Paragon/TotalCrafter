package com.brett.world.chunks;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
* @author Brett
* @date 7-Aug-2020
*/

public class RegionPart {
	
	// max number of chunks in the region
	public static final int size = 8;
	
	private int x,y,z;
	private String location;
	public List<Chunk> chunks;
	
	public RegionPart(String location, int x, int y, int z, List<Chunk> chunks) {
		this.location = location;
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunks = chunks;
	}
	
	public void save() {
		try {
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(
					new FileOutputStream(new StringBuilder().append(location).append(x).append("_").append(y).append("_").append(z).append(".rg").toString()), 8192)));
			
			// write header information
			
			for (int i = 0; i < chunks.size(); i++) {
				Chunk c = chunks.get(i);
				// unlikely that anyone will ever reach a chunk at the min/max integer locations.
				// the chunk generator doesn't even support it.
				dos.writeInt(Integer.MAX_VALUE);
				dos.writeInt(i);
				dos.writeInt(c.x_pos);
				dos.writeInt(c.y_pos);
				dos.writeInt(c.z_pos);
				dos.writeInt(Integer.MIN_VALUE);
			}
			
			dos.writeInt(Integer.MAX_VALUE-1);
			
			// writing all in order allows for the GZIP compression to *hopefully* compress the region file better.
			
			for (int in = 0; in < chunks.size(); in++) {
				Chunk c = chunks.get(in);
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						for (int k = 0; k < 16; k++) {
							dos.writeShort(c.blocks.get(i, j, k));
						}
					}
				}
			}
			
			dos.close();
		} catch (Exception e) {}
	}
	
}
