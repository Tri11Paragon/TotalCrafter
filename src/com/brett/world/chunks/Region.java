package com.brett.world.chunks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.world.World;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
* @author Brett
* @date 31-Aug-2020
*/

public class Region {
	
	//size of region in chunks
	private static final int regionSize = 8;
	
	private String location;
	public int rx,ry,rz;
	public Chunk[][][] chunks = new Chunk[regionSize][regionSize][regionSize];
	
	public Region(int rx, int ry, int rz,String worldLocation) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.location = worldLocation;
	}
	
	public static Region load(String location, World world, int rx, int ry, int rz) {
		return new Region(rx, ry, rz, location).load(world);
	}
	
	/**
	 * returns a chunk based on chunk world space
	 */
	public Chunk getChunk(int x, int y, int z) {
		return chunks[x & 7][y & 7][z & 7];
	}
	
	public Region setChunk(int x, int y, int z, Chunk c) {
		chunks[x & 7][y & 7][z & 7] = c;
		return this;
	}
	
	public Region load(World world) {
		String loc = new StringBuilder().append(location).append(rx).append("_").append(ry).append("_").append(rz).append(".rg").toString();
		if (!new File(loc).exists())
			return this;
		try {
			DataInputStream dis = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(loc), 8192)));
			
			rx = dis.readInt();
			ry = dis.readInt();
			rz = dis.readInt();
			
			while (dis.available() > 0) {
				int x = dis.readByte();
				int y = dis.readByte();
				int z = dis.readByte();
				
				Chunk c = new Chunk(world, new ShortBlockStorage(), new ByteBlockStorage(), rx + x, ry + y, rz + z);
				for (int w = 0; w < 16; w++) {
					for (int s = 0; s < 16; s++) {
						for (int d = 0; d < 16; d++) {
							c.blocks.blocks[w][s][d] = dis.readShort();
						}
					}
				}
				chunks[x][y][z] = c;
			
			}
			
			dis.close();
		} catch (Exception e) {}
		return this;
	}
	
	public Region save() {
		String loc = new StringBuilder().append(location).append(rx).append("_").append(ry).append("_").append(rz).append(".rg").toString();
		try {
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(loc), 8192)));
			dos.writeInt(rx);
			dos.writeInt(ry);
			dos.writeInt(rz);
			
			for (int i = 0; i < regionSize; i++) {
				for (int j = 0; j < regionSize; j++) {
					for (int k = 0; k < regionSize; k++) {
						Chunk c = chunks[i][j][k];
						if (c == null)
							continue;
						short[][][] blocks = c.blocks.blocks;
						dos.writeByte(i);
						dos.writeByte(j);
						dos.writeByte(k);
						for (int w = 0; w < 16; w++) {
							for (int s = 0; s < 16; s++) {
								for (int d = 0; d < 16; d++) {
									dos.writeShort(blocks[w][s][d]);
								}
							}
						}
					}
				}
			}
			
			dos.close();
		} catch (Exception e) {}
		return this;
	}
	
}
