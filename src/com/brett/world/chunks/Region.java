package com.brett.world.chunks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.world.World;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.BlockStorage;

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
		File fr = new File(loc);
		if (!fr.exists())
			return this;
		// some kind of error while writing file
		// so we won't load  it.
		if (fr.length() < 10)
			return this;
		try {
			
			DataInputStream dis = new DataInputStream(
					new GZIPInputStream(
							new BufferedInputStream(
									new FileInputStream(loc), 8192)
							)
					);
			
			while (dis.available() > 1) {
				try {
					int x = dis.readByte();
					int y = dis.readByte();
					int z = dis.readByte();
					
					int cx = rx * 8 + x;
					int cy = ry * 8 + y;
					int cz = rz * 8 + z;
					
					BlockStorage shbk = new BlockStorage();
					for (int i = 0; i < 16; i++) {
						for (int j = 0; j < 16; j++) {
							for (int k = 0; k < 16; k++) {
								try {
									short blk = dis.readShort();
									shbk.blocks[i][j][k] = blk;
									//if (blk != 0)
									//	GameRegistry.getBlock(blk).onBlockPlaced(world, blk, cx + i, cy + j, cz + k);
								} catch (Exception e) {
									System.err.println("Tiny Error: " + e.getLocalizedMessage());
									break;
								}
							}
						}
					}
					
					Chunk c = new Chunk(world, shbk, new ByteBlockStorage(), cx, cy, cz);
					chunks[x][y][z] = c;
					byte flag = dis.readByte();
					if (flag == -2)
						break;
				} catch (Exception e) {break;}
			}
			
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("BIG ERROR " + e.getLocalizedMessage() ); }
		return this;
	}
	
	public Region save() {
		String loc = new StringBuilder().append(location).append(rx).append("_").append(ry).append("_").append(rz).append(".rg").toString();
		File f = new File(loc + ".lock");
		try {
			if (f.exists())
				return this;
			f.createNewFile();
		} catch (IOException e1) {e1.printStackTrace();}
		try {
			int hasChangedN = 0;
			// check to see if the region has changed by, as we don't want to over save to the disk
			// which wastes IO and RAM.
			for (int i = 0; i < regionSize; i++) {
				for (int j = 0; j < regionSize; j++) {
					for (int k = 0; k < regionSize; k++) {
						Chunk c = chunks[i][j][k];
						if (c == null) {
							hasChangedN++;
							continue;
						}
						if (c.isEmpty) {
							hasChangedN++;
							continue;
						}
						if (!c.blocks.hasChanged) {
							hasChangedN++;
							continue;
						}
					}
				}
			}
			
			if (hasChangedN >= 512) {
				f.delete();
				return this;
			}
			
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(loc), 8192)));
			
			for (int i = 0; i < regionSize; i++) {
				for (int j = 0; j < regionSize; j++) {
					for (int k = 0; k < regionSize; k++) {
						Chunk c = chunks[i][j][k];
						if (c == null)
							continue;
						if (c.isEmpty)
							continue;
						// change to false because we have written changes to disk
						c.blocks.hasChanged = false;
						short[][][] blocks = c.blocks.blocks;
						dos.writeByte(i);
						dos.writeByte(j);
						dos.writeByte(k);
						for (int xx = 0; xx < 16; xx++) {
							for (int yy = 0; yy < 16; yy++) {
								for (int zz = 0; zz < 16; zz++) {
									dos.writeShort(blocks[xx][yy][zz]);
								}
							}
						}
						dos.writeByte(-1);
					}
				}
			}
			dos.writeByte(-2);
			dos.close();
			f.delete();
		} catch (Exception e) {e.printStackTrace();}
		return this;
	}
	
	public void cleanup() {
		for (int i = 0; i < regionSize; i++) {
			for (int j = 0; j < regionSize; j++) {
				for (int k = 0; k < regionSize; k++) {
					Chunk c = chunks[i][j][k];
					if (c == null)
						continue;
					c.qdelete();
				}
			}
		}
	}
	
}
