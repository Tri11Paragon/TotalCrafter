package com.brett.voxel.world.tileentity;

import java.io.File;
import java.io.Serializable;

import com.brett.tools.IKeyState;
import com.brett.voxel.inventory.Inventory;
import com.brett.voxel.nbt.NBTStorage;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.chunk.ChunkStore;

/**
* Non physical entity for storing individual block data to the disk.
* @author brett
* @date Apr. 17, 2020
*/

public class TileEntity implements IKeyState, Serializable {
	
	private static final long serialVersionUID = 142599435776416417L;
	
	private int x,y,z;
	protected transient IWorldProvider world;
	private NBTStorage nbt;
	private String location = "";
	protected boolean hasChanged = false;
	// inventory for this entity
	public Inventory i;
	
	public void spawnTileEntity(int x, int y, int z, IWorldProvider world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		location = ChunkStore.worldLocation + "tile/";
		// loads inventory
		if (i == null)
			i = new Inventory((int)LevelLoader.seed, "tile/inv_" + x + "_" + y + "_" + z + "_");
		// make sure the file location exists.
		new File(this.location).mkdirs();
		nbt = new NBTStorage(location+"nbt-"+x+"_"+y+"_"+z);
		this.load();
		hasChanged = true;
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
	 * ran in main thread. not timer safe.
	 */
	public void renderUpdate() {
		
	}
	
	/**
	 * returns true if the tile entitiy has changed in some way
	 */
	public boolean getTileChanged() {
		if (hasChanged) {
			hasChanged = false;
			return true;
		}
		return hasChanged;
	}
	
	/**
	 * ran 20 times per second. # here is > 0, and is the number of (updates) ticks skiped since last called
	 */
	public void tick(long skiped) {

	}
	
	/**
	 * destroys the tile entity
	 */
	public void destroy() {
		try {
			nbt.destory();
			nbt = null;
		} catch (Exception e) {}
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
	
	public boolean isSame(TileEntity e) {
		return e.x == x && e.y == y && e.z == z;
	}

	public IWorldProvider getWorld() {
		return world;
	}

	@Override
	public void onKeyPressed(int key) {
		
	}

	@Override
	public void onKeyReleased(int key) {
		
	}
	
	public void setWorld(IWorldProvider world) {
		this.world = world;
	}
	
}
