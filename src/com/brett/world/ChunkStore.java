package com.brett.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.brett.renderer.Loader;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.renderer.world.Chunk;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Feb. 19, 2020
*/

public class ChunkStore {
	
	private static final int renderDistance = 3;
	public static final String worldLocation = "worlds/w1/";
	public static File wfolder = new File(worldLocation);
	public static File[] wfiles = wfolder.listFiles();
	
	private Chunk[][] chunkspp = new Chunk[500][500];
	private Chunk[][] chunksnn = new Chunk[500][500];
	private Chunk[][] chunksnp = new Chunk[500][500];
	private Chunk[][] chunkspn = new Chunk[500][500];
	
	@SuppressWarnings("unused")
	private List<Chunk> activeChunks = new ArrayList<Chunk>();
	
	private Camera cam;
	private Loader loader;
	
	public ChunkStore(Camera cam, Loader loader) {
		System.out.println("LN "+chunkspp.length);
		this.cam = cam;
		this.loader = loader;
		new Thread(new Runnable() {
			@Override
			public void run() {
				
			}
		}).start();
	}
	
	public Chunk generateChunk(int x, int z) {
		for (int i = 0; i < wfiles.length; i++) {
			if (wfiles[i].isFile()) {
				if (wfiles[i].getName().compareTo("c-"+ x + "-" + z) == 0) {
					try{
						BufferedReader reader = new BufferedReader(new FileReader(worldLocation + wfiles[i].getName()));
						String line;
						while((line = reader.readLine())!=null){
							
						}
						reader.close();
					}catch(IOException e){
						e.printStackTrace();
						System.err.println("Missing chunk. Maybe another program deleted it?");
						System.exit(-1);
					}
				}
			}
		}
		return new Chunk(loader, x, z);
	}
	
	public void renderChunks(VoxelShader shader) {
		for(int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				Chunk c = getChunk(cx, cz);
				if (c == null)
					continue;
				c.render(shader);
			}
		}
	}
	
	public void setChunk(Chunk c, int x, int z) {
		if (x < 0 && z < 0)
			chunksnn[(x*-1)-1][(z*-1) - 1] = c;
		else if (x < 0 && z >= 0) 
			chunksnp[(x*-1)-1][z] = c;
		else if (z < 0 && x >= 0)
			chunkspn[x][(z*-1) - 1] = c;
		else 
			chunkspp[x][z] = c;
	}
	
	public Chunk getChunk(int x, int z) {
		if (x < 0 && z < 0)
			return chunksnn[(x*-1)-1][(z*-1) - 1];
		else if (x < 0 && z >= 0) 
			return chunksnp[(x*-1)-1][z];
		else if (z < 0 && x >= 0)
			return chunkspn[x][(z*-1) - 1];
		else 
			return chunkspp[x][z];
	}
	
	/**
	 * Returns the chunk based on actual x position and not based on offset
	 * I would avoid this if possible.
	 */
	public Chunk getChunkUn(int x, int z) {
		x = x/Chunk.x;
		z = z/Chunk.z;
		if (x < 0 && z < 0)
			return chunksnn[(x*-1)-1][(z*-1) - 1];
		else if (x < 0 && z >= 0) 
			return chunksnp[(x*-1)-1][z];
		else if (z < 0 && x >= 0)
			return chunkspn[x][(z*-1) - 1];
		else 
			return chunkspp[x][z];
	}
	
}
