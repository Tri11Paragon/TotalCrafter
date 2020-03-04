package com.brett.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.Tuple;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.noisefunctions.BaseNoiseFunction;
import com.brett.world.terrain.noisefunctions.NoiseFunction;
import com.tester.Main;

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

	// actually the best way of storing chunk data.
	// however will need to add a way of moving between active and non active
	// chunks.
	private MultiKeyMap<Integer, Chunk> chunks = new MultiKeyMap<Integer, Chunk>();
	private List<Tuple<Integer, Integer>> ungeneratedChunks = Collections.synchronizedList(new ArrayList<Tuple<Integer, Integer>>());

	@SuppressWarnings("unused")
	private List<Chunk> activeChunks = new ArrayList<Chunk>();

	private Camera cam;
	private Loader loader;
	private NoiseFunction nf;

	public ChunkStore(Camera cam, Loader loader) {
		this.cam = cam;
		this.loader = loader;
		this.nf = new BaseNoiseFunction(694);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(Main.isOpen) {
					for (int i = 0; i < ungeneratedChunks.size(); i++) {
						Tuple<Integer, Integer> g = ungeneratedChunks.get(i);
						setChunk(generateChunk(g.getX(), g.getY()), g.getX(), g.getY());
						ungeneratedChunks.remove(i);
					}
				}
			}
		}).start();
	}

	public Chunk generateChunk(int x, int z) {
		// add the world gen...
		if (wfiles == null)
			return new Chunk(loader,this, nf, x, z);
		for (int f = 0; f < wfiles.length; f++) {
			if (wfiles[f].isFile()) {
				if (wfiles[f].getName().contentEquals(x + "_" + z + ".chunk")) {
					try {
						// buffered readers makes this VERY fast
						DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(worldLocation + wfiles[f].getName())));
						short[][][] blks = new short[Chunk.x][Chunk.y][Chunk.z];
						for (int i = 0; i < blks.length; i++) {
							for (int j = 0; j < blks[i].length; j++) {
								for (int k = 0; k < blks[i][j].length; k++) {
									blks[i][j][k] = is.readShort();
								}
							}
						}
						is.close();
						String[] strs = wfiles[f].getName().split("\\.")[0].split("_");
						return new Chunk(loader,this, blks, Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println("Missing chunk. Maybe another program deleted it?");
						System.exit(-1);
					}
				}
			}
		}
		return new Chunk(loader,this, nf, x, z);
	}

	public void saveChunks() {
		System.out.println("Saving world");
		MapIterator<MultiKey<? extends Integer>, Chunk> chunkIt = chunks.mapIterator();
		while (chunkIt.hasNext()) {
			MultiKey<? extends Integer> ke = chunkIt.next();
			try {
				DataOutputStream os = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(worldLocation + ke.getKey(0) + "_" + ke.getKey(1) + ".chunk")));
				Chunk c = chunks.get(ke);
				if (c == null)
					continue;
				short[][][] ch = c.getBlocks();
				for (int i = 0; i < ch.length; i++) {
					for (int j = 0; j < ch[i].length; j++) {
						for (int k = 0; k < ch[i][j].length; k++) {
							os.writeShort(ch[i][j][k]);
						}
					}
				}
				os.close();
			} catch (IOException e) {
				if (!new File(worldLocation).mkdirs())
					saveChunks();
			}
		}
		System.out.println("World Saved");
	}

	public void renderChunks(VoxelShader shader) {
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				c.render(shader);
			}
		}
	}
	
	public void queChunk(int cx, int cz) {
		ungeneratedChunks.add(new Tuple<Integer, Integer>(cx, cz));
	}

	public void cleanup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				saveChunks();
			}
		}).start();
	}

	public void setChunk(Chunk c, int x, int z) {
		chunks.put(x, z, c);
	}

	public Chunk getChunk(int x, int z) {
		if (chunks.containsKey(x, z))
			return chunks.get(x, z);
		else
			return null;
	}

	/**
	 * Returns the chunk based on actual x position and not based on offset I would
	 * avoid this if possible.
	 */
	public Chunk getChunkUn(int x, int z) {
		x /= Chunk.x;
		z /= Chunk.z;
		return getChunk(x, z);
	}

}
