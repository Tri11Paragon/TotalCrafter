package com.brett.voxel.world;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.Tuple;
import com.brett.renderer.shaders.VoxelShader;
import com.brett.voxel.VoxelScreenManager;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.noisefunctions.ChunkNoiseFunction;
import com.brett.world.terrain.noisefunctions.NoiseFunction;

/**
 *
 * @author brett
 * @date Feb. 19, 2020
 */

public class ChunkStore {

	public static int renderDistance = 3;
	public static final String worldLocation = "worlds/w1/";
	public static final String dimLocation = "worlds/w1/DIM";
	public static File wfolder = new File(worldLocation);
	public static File[] wfiles = wfolder.listFiles();

	// actually the best way of storing chunk data.
	// however will need to add a way of moving between active and non active
	// chunks.
	private MultiKeyMap<Integer, Region> chunks = new MultiKeyMap<Integer, Region>();
	private MultiKeyMap<Integer, Region> chunksCopy = null;
	private List<Tuple<Integer, Integer>> ungeneratedChunks = Collections.synchronizedList(new ArrayList<Tuple<Integer, Integer>>());

	@SuppressWarnings("unused")
	private List<Chunk> activeChunks = new ArrayList<Chunk>();

	protected Camera cam;
	private Loader loader;
	private NoiseFunction nf;
	private VoxelWorld world;

	public ChunkStore(Camera cam, Loader loader, VoxelWorld world) {
		new File(dimLocation).mkdirs();
		LevelLoader.loadLevelData(worldLocation);
		this.cam = cam;
		this.loader = loader;
		this.nf = new ChunkNoiseFunction(LevelLoader.seed);
		this.world = world;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(VoxelScreenManager.isOpen) {
					for (int i = 0; i < ungeneratedChunks.size(); i++) {
						Tuple<Integer, Integer> g = ungeneratedChunks.get(i);
						setChunk(generateChunk(g.getX(), g.getY()), g.getX(), g.getY());
						ungeneratedChunks.remove(i);
					}
				} 
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(VoxelScreenManager.isOpen) {
					// what the region list will look like.
					// butters = collin?
					
					/**
					 * I have tried many ways of doing this.
					 * (not pushed to git)
					 * I thought hey instead of checking each update tick of the generation thread, make a new thread that handles this
					 * so that is what this does
					 * it handles the unloading of chunk regions.
					 * this doesn't need to be run every update, so it gets ran every 20 seconds.
					 */
					try {
						Thread.sleep(20*1000);
					} catch (InterruptedException e) {}
					
					if (chunksCopy == null) {
						chunksCopy = new MultiKeyMap<Integer, Region>();
						for (int i = -renderDistance; i < renderDistance; i++) {
							for (int k = -renderDistance; k < renderDistance; k++) {
								int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
								int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
								int xoff = 0, zoff = 0;
								if (cx < 0)
									xoff = -1;
								if (cz < 0)
									zoff = -1;
								int rx = cx / Region.x + xoff;
								int rz = cz / Region.z + zoff;
								chunksCopy.put(rx, rz, chunks.get(rx, rz));
							}
						}
					}
					// This is required to keep memory usage down.
					// however it causes major stuttering.
					// who can blame it? it has to dereference about half a gig of data
					// This is why java sucks
					// i can't control the memory myself and because of that
					// the gc ends up running at 88%
					// was hoping that manual nulling would prevent this
					// and therefore not require calling gc howeever it just caused a bunch of null errors when rendering
					// and it didn't free up memory
					// infact when the GC did decide to run, mc3 ended up using more memory (:facepalm:)
					//System.gc();
					
					/**
					 * This also highlights the issue with school computers in a computer science classroom:
					 * 	There is no control.
					 * There would have been 0 chance of me finding this or even knowing how bad the ram usage was if i was only working on this
					 * at school
					 * 
					 * I think a good step would be to switch to linux. Programming on linux is much better due to access to the underlying source of the kernel
					 * and therefore memory
					 * on linux security is better then windows. im not a windows admin so i can't speak about the policies that can be implimented in windows
					 * but again because windows is closed source, and linux is open source you can actually edit and change how linux runs.
					 * ie if you don't like the way users are managed you can change it.
					 * 
					 * note im not suggesting that schools develop their own user control software, however i think it would be neat for a company
					 * to make a linux software for computer science schools that give access to memory but prevent the kids from doing
					 * bad stuff.
					 * 
					 * This can already be done using user privileges and stuff (i do this on the servers i host: i chroot them to their home directories
					 * and don't allow them to use sudo or have root access.
					 * 
					 * if this was computer science, a linux admin could allow users to read from /proc/ but not write (this is already standard in linux)
					 * Hint for kedwell (/proc/ is where basiclly all memory is stored as a file. this is whats neat about *nix os. they use files for memory.
					 * its actually really neat
					 * 
					 * allowing students to read from proc would allow them to keep track of how much memory their programs are using along with a bunch of other neat stuff.
					 * 
					 * another thing that could be implemented is using being able to kill processes using a task manager like thing.
					 * i use htop but there is a GUI task manager in my linux distro.
					 * htop is much better though. much more information about task and what it is doing, along with its children.
					 * 
					 * what an admin could do is allow users to only kill userspace programs or even limit it to programs that the user has started themselves.
					 * the second thing there would likely require some custom software however it could be done and likely has already been done
					 * 
					 * most system processes already run in kernel space and require privileges to kill.
					 * thought i feel like htop can bypass this but that can be easily fixed
					 * or everything i have killed with htop was userspace.
					 * 
					 * i would try and kill something in kernel space right now but i don't want my computer to crash if its not protected
					 * however knowing *nix operating systems kernel processes are protected from htop
					 * 
					 * anyways my point is comp sci needs linux and windows bad
					 * 
					 *	/rant
					 */
				}
			}
		}).start();
	}

	public Chunk generateChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			if (chunks.get(regionPosX, regionPosZ) == null)
				return new Chunk(loader,world, nf, x, z);
			else {
				Chunk c = chunks.get(regionPosX, regionPosZ).getChunk(x, z);
				if (c == null) {
					c = new Chunk(loader,world, nf, x, z);
					chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
					return c;
				} else {
					return c;
			}
			}
		} else {
			Region r = Region.loadRegion(loader, world, regionPosX, regionPosZ, worldLocation);
			chunks.put(regionPosX, regionPosZ, r);
			Chunk c = r.getChunk(x, z);
			if (c == null) {
				c = new Chunk(loader,world, nf, x, z);
				chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
				return c;
			}else
				return c;
		}
	}

	public void saveChunks() {
		System.out.println("Saving world");
		MapIterator<MultiKey<? extends Integer>, Region> regionIt = chunks.mapIterator();
		while (regionIt.hasNext()) {
			try {
			regionIt.next();
			} catch (ConcurrentModificationException e) {System.err.println("Tried saving map while loading it. \nPlease wait for map to complete loading before exiting game."); return;}
			Region val = regionIt.getValue();
			if (val != null)
				val.saveRegion(worldLocation);
		}
		LevelLoader.saveLevelData(worldLocation);
		System.out.println("World Saved");
	}

	public void queSave(MultiKeyMap<Integer, Region> rgs) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MapIterator<MultiKey<? extends Integer>, Region> rg = rgs.mapIterator();
				while (rg.hasNext()) {
					MultiKey<? extends Integer> r = rg.next();
					Region rgg = rgs.get(r.getKey(0), r.getKey(1));
					//System.out.println(rgg + " " + r.getKey(0) + " " + r.getKey(1));
					if (rgg != null) {
						StringBuilder b = new StringBuilder();
						b.append("Saving Region ");
						b.append(rgg);
						b.append(" at pos: {");
						b.append(r.getKey(0));
						b.append(",");
						b.append(r.getKey(1));
						b.append("}");
						System.out.println(b);
						rgg.saveRegion(worldLocation);
						rgg = null;
						rg.remove();
					}
				}
			}
		}).start();
	}
	
	public void renderChunks(VoxelShader shader) {
		if (chunksCopy != null) {
			MapIterator<MultiKey<? extends Integer>, Region> rg = chunksCopy.mapIterator();
			while (rg.hasNext()) {
				MultiKey<? extends Integer> r = rg.next();
				chunks.remove(r);
			}
			MultiKeyMap<Integer, Region> rgs = new MultiKeyMap<Integer, Region>();
			rgs.putAll(chunks);
			queSave(rgs);
			chunks.clear();
			chunks.putAll(chunksCopy);
			System.out.println(chunks.size());
			chunksCopy = null;
		}
		for (int i = -renderDistance; i < renderDistance; i++) {
			for (int k = -renderDistance; k < renderDistance; k++) {
				int cx = ((int) (cam.getPosition().x / Chunk.x)) + i;
				int cz = ((int) (cam.getPosition().z / Chunk.z)) + k;
				//if (!cam.isPointInFrustum(i, cam.getPosition().y, k))
				//	continue;
				Chunk c = getChunk(cx, cz);
				if (c == null) {
					queChunk(cx, cz);
					continue;
				}
				if (!c.getNull())
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
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			Region r = chunks.get(regionPosX, regionPosZ);
			if (r != null) 
				r.setChunk(x, z, c);
			return;
		}
		System.out.println(regionPosX + " " + regionPosZ);
		chunks.put(regionPosX, regionPosZ, new Region(regionPosX, regionPosZ));
		chunks.get(regionPosX, regionPosZ).setChunk(x, z, c);
	}
	
	/**
	 * Note this is in chunk pos and not world pos.
	 */
	public Chunk getChunk(int x, int z) {
		int regionPosX = x / Region.x;
		int regionPosZ = z / Region.z;
		if (x < 0)
			regionPosX -= 1;
		if (z < 0)
			regionPosZ -= 1;
		if (chunks.containsKey(regionPosX, regionPosZ)) {
			Region r = chunks.get(regionPosX, regionPosZ);
			if (r == null)
				return null;
			return r.getChunk(x, z);
		}else
			return null;
	}
	
	public short getBlock(float x, float y, float z) {
		if (y < 0)
			return 0;
		if (y > Chunk.y)
			return 0;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int) (x/Chunk.x + xoff), (int) (z/Chunk.z + zoff));
		if (c == null)
			return -1;
		x%=16;
		z%=16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		return c.getBlock((int)x, (int)y, (int)z);
	}
	
	public void setBlock(float x, float y, float z, short block) {
		if (y < 0)
			return;
		if (y > Chunk.y)
			return;
		int xoff = 0,zoff = 0;
		if (x < 0)
			xoff = -1;
		if (z < 0)
			zoff = -1;
		Chunk c = getChunk((int)(x/(float)Chunk.x) + xoff, (int)(z/(float)Chunk.z) + zoff);
		if (c == null)
			return;
		x %= 16;
		z %= 16;
		if (x < 0)
			x = biasNegative(x, -Chunk.x);
		if (z < 0)
			z = biasNegative(z, -Chunk.z);
		c.setBlock((int)x,(int)y, (int)z, block);
	}
	
	public void setBlockBIAS(float x, float y, float z, short block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	public void setBlockBIAS(float x, float y, float z, int block) {
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		setBlock(x, y, z, block);
	}
	
	public short getBlockBIAS(float x, float y, float z) {
		// fix for something that is not broken but this is needed
		// yes

		// this is actually one of those
		if (x < 0)
			x -= 1;
		if (z < 0)
			z -= 1;
		return getBlock(x, y, z);
	}
	
	public void setBlock(float x, float y, float z, int block) {
		setBlock(x, y, z, (short)block);
	}
	
	private float biasNegative(float f, int unitSize) {
		return unitSize - f;
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
