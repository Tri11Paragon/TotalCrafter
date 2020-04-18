package com.brett.voxel.world.chunk;

import com.brett.renderer.datatypes.ModelTexture;
import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;

/**
*
* @author brett
* @date Apr. 8, 2020
*/

public class ChunkSub {
	
	public static int y = Chunk.y / 8;
	
	private short size;
	private VoxelWorld world;
	private short[][][] blocks = new short[Chunk.x][y][Chunk.z];
	private byte[][][] lightLevel = new byte[Chunk.x][y][Chunk.z];
	private RawBlockModel[][][] blocksModels = new RawBlockModel[Chunk.x][y][Chunk.z];
	private float xoff, yoff, zoff;
	
	public ChunkSub(VoxelWorld world, float xoff, float yoff, float zoff) {
		this.world = world;
		this.xoff = xoff;
		this.yoff = yoff;
		this.zoff = zoff;
	}
	
	public void render() {
		// don't draw if there are no blocks in this chunk.
		if (size == 0)
			return;
		int xz = world.random.nextInt(Chunk.x);
		int yz = world.random.nextInt(Chunk.y);
		int zz = world.random.nextInt(Chunk.z);
		if (blocks[xz][yz][zz] != 0)
			Block.blocks.get(blocks[xz][yz][zz]).onBlockTick(xz, yz, zz, world);
		int un = MeshStore.boolEmpty;
		for (int i =0; i < Chunk.x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < Chunk.z; k++) {
					if (blocks[i][j][k] == 0)
						continue;
					
					// make this based on texture id?
					RawBlockModel rawModel = blocksModels[i][j][k];
					
					if (rawModel == null)
						continue;
					
					if (rawModel.getVaoID() == un)
						continue;
					
					ModelTexture model = Block.blocks.get(blocks[i][j][k]).model;
					
					world.addBlock(rawModel, model.getID(), new float[] {i+(Chunk.x*xoff), j+(y*yoff) ,k+(Chunk.z*zoff)});
				}
			}
		}
	}
	
	public void setBlock(int x, int y, int z, short block) {
		if (blocks[x][y][z] == block)
			return;
		if (block == (short)0) {
			size--;
			if (size < 0)
				size = 0;
		} else 
			size++;
		blocks[x][y][z] = block;
	}
	
	public void setLightLevel(int x, int y, int z, byte level) {
		lightLevel[x][y][z] = level;
	}
	
	public void setBlockModel(int x, int y, int z, RawBlockModel model) {
		blocksModels[x][y][z] = model;
	}
	
	public short getBlock(int x, int y, int z) {
		return blocks[x][y][z];
	}
	
	public byte getBlockLight(int x, int y, int z) {
		return lightLevel[x][y][z];
	}
	
	public RawBlockModel getBlockModel(int x, int y, int z) {
		return blocksModels[x][y][z];
	}
	
}
