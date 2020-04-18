package com.brett.voxel.world.tileentity;

import java.io.File;
import com.brett.voxel.nbt.NBTStorage;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.ChunkStore;

/**
* Non physical entity for storing individual block data to the disk.
* @author brett
* @date Apr. 17, 2020
*/

public class TileEntity {
	
	private int x,y,z;
	private VoxelWorld world;
	private NBTStorage nbt;
	private String location = "";
	
	public void spawnTileEntity(int x, int y, int z, VoxelWorld world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		location = ChunkStore.worldLocation + "tile/";
		// make sure the file location exists.
		new File(this.location).mkdirs();
		nbt = new NBTStorage(location+"nbt-"+x+"_"+y+"_"+z);
		this.load();
	}
	
	/**
	 * Save NBT to disk
	 */
	public void save() {
		nbt.saveAll();
	}
	
	/**
	 * Load NBT from disk
	 */
	public void load() {
		
	}
	
	/**
	 * ran once per frame.
	 */
	public void tick() {
		
	}
	
	public void destroy() {
		nbt.destory();
		nbt = null;
	}
	
	public String getLocation() {
		return location;
	}
	
	public NBTStorage getNBT() {
		return nbt;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public VoxelWorld getWorld() {
		return world;
	}
	
}
