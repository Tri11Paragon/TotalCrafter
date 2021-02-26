package com.brett.voxel.world;

import java.util.HashMap;
import java.util.Stack;

import org.joml.Vector3f;

import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;

/**
*
* @author brett
* @date Mar. 8, 2020
*/
public class Explosion {
	
	// chunks that have been remeshed. (Prevents issues with remeshing)
	private HashMap<Integer, Integer> meshed = new HashMap<Integer, Integer>();
	
	// recursion amount.
	private static int RE_MNT = 12;
	
	// world position and size of explosion.
	private float x,y,z,size;
	// reference to world
	private VoxelWorld world;
	// all the normalized vectors for this explosion.
	private Stack<Vector3f> points = new Stack<Vector3f>();
	
	public Explosion(float x, float y , float z, float power, VoxelWorld world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = power;
		RE_MNT = (int) (size * 2);
		this.world = world;
		// generates all the direction vectors that will be used to find blocks.
		for (int i = -RE_MNT; i <= RE_MNT; i++) {
			for (int j =  -RE_MNT; j <= RE_MNT; j++) {
				for (int k =  -RE_MNT; k <= RE_MNT; k++) {
					Vector3f vec = new Vector3f(i,j,k);
					// put the vector into normalized coords
					vec.normalize();
					// add it to the stack.
					points.push(vec);
				}
			}
		}
	}
	
	public void explode() {
		// clear all chunks that have been meshed.
		meshed.clear();
		while (!points.empty()) {
			// direction vector for this ray;
			Vector3f vec = points.pop();
			// the end point for the ray
			Vector3f ray = biasVector(vec, size);
			// amount to step in each axis for every recursion time
			float xStep = (ray.x-vec.x)/RE_MNT;
			float yStep = (ray.y-vec.y)/RE_MNT;
			float zStep = (ray.z-vec.z)/RE_MNT;
			
			// current pos we have walked to. (non world coord)
			Vector3f walked = new Vector3f(vec.x, vec.y, vec.z);
			for (int i = 0; i < RE_MNT; i++) {
				walked.x += xStep;
				walked.y += yStep;
				walked.z += zStep;
				// convert the walked into world coords
				float posx = x + walked.x;
				float posy = y + walked.y;
				float posz = z + walked.z;
				// calculate the strength based on distance
				float strength = (float) ((size*size) / (Math.sqrt((walked.x*walked.x) + (walked.y*walked.y) + (walked.z*walked.z))));
				// get the blockid for this pos
				short blockId = world.chunk.getBlock(posx, posy, posz);
				// get the block
				Block b = Block.blocks.get(blockId);
				// if its air or if its null we don't need to do anything
				if (b == null || blockId == 0)
					continue;
				float hardness = b.getHardness();
				// check to make sure the strength is high enough.
				// if it is destory the block.
				if (strength > hardness) {
					world.chunk.setBlock(posx, posy, posz, 0);
					// update the chunk mesh
					getChunkRemesh(posx, posz);
					// tell the block it was destroyed.
					b.onBlockBreaked((int)posx, (int)posy, (int)posz, world);
				}
			}
		}
	}
	
	/**
	 * returns a chunk based on its world position.
	 */
	private void getChunkRemesh(float worldX, float worldZ) {
		int xoff = 0,zoff = 0;
		// adjust for integers truncating towards zero.
		if (worldX < 0)
			xoff = -1;
		if (worldZ < 0)
			zoff = -1;
		// return the chunk.
		int cpx = (int)(worldX/(float)Chunk.x) + xoff;
		int cpz = (int)(worldZ/(float)Chunk.z) + zoff;
		// make sure that this chunk hasn't been remeshed.
		if (meshed.containsKey(cpx)) {
			if (meshed.get(cpx) == cpz)
				return;
		}
		meshed.put(cpx, cpz);
		// remesh chunks.
		world.chunk.getChunk(cpx, cpz).remeshPRI();
		world.chunk.getChunk(cpx + 1, cpz).remeshPRI();
		world.chunk.getChunk(cpx - 1, cpz).remeshPRI();
		world.chunk.getChunk(cpx, cpz + 1).remeshPRI();
		world.chunk.getChunk(cpx, cpz - 1).remeshPRI();
	}
	
	/*
	 * Multiplies the vector. (by f)
	 */
	private Vector3f biasVector(Vector3f v, float f) {
		return new Vector3f(v.x * f, v.y * f, v.z * f);
	}
	
}
