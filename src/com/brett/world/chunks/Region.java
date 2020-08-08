package com.brett.world.chunks;

import java.io.File;
import java.util.ArrayList;
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
		NdHashMap<Integer, RegionPart> regions = new NdHashMap<Integer, RegionPart>();
		chunks.iterate((NdHashMap<Integer, Chunk> dt, Integer k1, Integer k2, Integer k3, Chunk v1) -> {
			int lx = k1 >> 3;
			int ly = k2 >> 3;
			int lz = k3 >> 3;
			RegionPart pt = regions.get(lx, ly, lz);
			if (pt == null) {
				pt = new RegionPart(saveLocation, lx, ly, lz, new ArrayList<Chunk>());
				regions.set(lx, ly, lz, pt);
			}
			pt.chunks.add(v1);
			
		});
		regions.iterate((NdHashMap<Integer, RegionPart> dt, Integer k1, Integer k2, Integer k3, RegionPart v1) -> {
			new Thread(new Runnable() {
				@Override
				public void run() {
					v1.save();
				}
			}).start();
		});
	}
	
}
