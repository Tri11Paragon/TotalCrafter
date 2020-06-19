package com.brett.voxel.world.chunk;

import org.lwjgl.util.vector.Matrix4f;

import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.shaders.VoxelShader;

/**
*
* @author brett
* @date Jun. 1, 2020
* World base class
*/

public interface IChunkProvider {
	
	public void setChunk(Chunk c, int x, int z);
	
	public Chunk getChunk(int x, int z);
	
	public short getBlock(float x, float y, float z);
	
	public byte getBlockState(float x, float y, float z);
	
	public COLLISIONTYPE getBlockCollision(float x, float y, float z);
	
	public void setBlock(float x, float y, float z, short block);
	
	public void setBlockState(float x, float y, float z, byte state);
	
	public void setBlockStateBIAS(float x, float y, float z, byte state);
	
	public void setLightLevel(float x, float y, float z, byte level);
	
	public void setLightLevelTorch(float x, float y, float z, int level);
	
	public void setLightLevelSun(float x, float y, float z, int level);
	
	public byte getLightLevel(float x, float y, float z);
	
	public byte getLightLevelSun(float x, float y, float z);
	
	public byte getLightLevelTorch(float x, float y, float z);
	
	public void setBlockBIAS(float x, float y, float z, short block);
	
	public void setBlockBIAS(float x, float y, float z, int block);
	
	public short getBlockBIAS(float x, float y, float z);
	
	public void setLightLevelBIAS(float x, float y, float z, byte level);
	
	public byte getLightLevelBIAS(float x, float y, float z);
	
	public void setBlockServer(float x, float y, float z, short block);
	
	public void setBlock(float x, float y, float z, int block);
	
	public Chunk getChunkUn(int x, int z);
	
	public void renderChunks(VoxelShader shader, Matrix4f project);
	
	public void updateChunks();
	
	public void insertChunk(Chunk c);
	
	public void cleanup();
	
}
