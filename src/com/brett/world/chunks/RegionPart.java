package com.brett.world.chunks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.world.World;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.ShortBlockStorage;

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
	
	public void load(World world) {
		try {
			String loc = new StringBuilder().append(location).append(x).append("_").append(y).append("_").append(z).append(".rg").toString();
			if (!new File(loc).exists())
				return;
			
			DataInputStream ios = new DataInputStream(new GZIPInputStream(new BufferedInputStream(
					new FileInputStream(loc), 8192)));
			
			chunks = new ArrayList<Chunk>();
			
			int r1,r3,r4,r5,r6;
			while (true) {
				r1 = ios.readInt();
				if (r1 == Integer.MAX_VALUE-1)
					break;
				ios.readInt();
				r3 = ios.readInt();
				r4 = ios.readInt();
				r5 = ios.readInt();
				r6 = ios.readInt();
				if (r6 != Integer.MIN_VALUE) {
					ios.close();
					throw new IOException("Number r6 was not an acceptable flag. {" + r6 + "}");
				}
				chunks.add(new Chunk(world, new ShortBlockStorage(), new ByteBlockStorage(), r3, r4, r5));
			}
			
			int index = 0;
			while (ios.available() > 0) {
				if (index >= chunks.size())
					break;
				try {
					for (int i = 0; i < 16; i++) {
						for (int j = 0; j < 16; j++) {
							for (int k = 0; k < 16; k++) {
								chunks.get(index).blocks.blocks[i][j][k] = ios.readShort();
							}
						}
					}
				} catch (Exception e) {e.printStackTrace(); break;}
				index++;
			}
			ios.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			// it does a little bit. 400kb -> 200kb (ish)
			
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
