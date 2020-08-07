package com.brett.world.chunks;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.world.chunks.data.NdHashMap;

/**
* @author Brett
* @date 7-Aug-2020
*/

public class Region {
	
	private String saveLocation;
	private NdHashMap<Integer, Chunk> chunks;
	
	public Region(String saveLocation, NdHashMap<Integer, Chunk> chunks) {
		this.saveLocation = saveLocation;
		this.chunks = chunks;
	}
	
	public void saveChunks() {
		new File(saveLocation).mkdirs();
		chunks.iterate((NdHashMap<Integer, Chunk> dt, Integer k1, Integer k2, Integer k3, Chunk v1) -> {
			
			try {
				DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(saveLocation + k1 + "_" + k2 + "_" + k3), 8192)));
				
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						for (int k = 0; k < 16; k++) {
							dos.writeShort(v1.blocks.get(i, j, k));
						}
					}
				}
					
				dos.close();
			} catch (Exception e) {e.printStackTrace();}
			
		});
	}
	
}
