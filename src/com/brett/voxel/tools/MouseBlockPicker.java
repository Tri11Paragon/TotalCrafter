package com.brett.voxel.tools;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.DisplayManager;
import com.brett.cameras.Camera;
import com.brett.tools.Maths;
import com.brett.voxel.inventory.PlayerInventory;
import com.brett.voxel.renderer.VOverlayRenderer;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.items.Item;
import com.brett.voxel.world.items.ItemStack;
import com.brett.voxel.world.items.ItemTool;
import com.brett.voxel.world.player.Player;

/**
*
* @author brett
* @date Feb. 19, 2020
*/

public class MouseBlockPicker {
	
	// amount of times the search can run
	private static final int RECURSION_AMOUNT = 12;
	private static final float RAY_RANGE = 6;

	public static Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	private PlayerInventory i;
	private Player ply;
	private VOverlayRenderer renderer;
	
	private VoxelWorld world;

	public MouseBlockPicker(Camera cam, Matrix4f projection, VoxelWorld terrain, Player ply, VOverlayRenderer renderer) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.world = terrain;
		this.i = ply.getInventory();
		this.ply = ply;
		this.renderer = renderer;
	}
	
	/*
	 * a lot of these functions are the same, well actually they pretty much are the same
	 * except they return different variables. I'm not going to do the same thing over and over for each of them
	 */
	
	/**
	 * sets the current block to the input block id
	 */
	private int setCurrentBlockPoint(short block) {
		// generate our rays
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(currentRay.x/10, currentRay.y/10,currentRay.z/10);
		Vector3f currentRay = biasVector(MouseBlockPicker.currentRay, RAY_RANGE);
		// generate the amount we step in each dir
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		// amount we have walked so far
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RECURSION_AMOUNT; i++) {
			// increase the walked amount
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			// position of the block
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			// unadjusted position
			Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			// get the terrain for this position
			Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
			if (c == null)
				continue;
			// put the pos into block space
			posadj.x %= Chunk.x;
			posadj.z %= Chunk.z;
			// make the negatives work out. This is required or else you get weird stuff in the negatives.
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			// get the block at this pos
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			// we can't mine air and we can't mine Will blocks
			if (blockid == Block.AIR || blockid == Block.WILL)
				continue;
			// get the actual block
			Block b = Block.blocks.get(blockid);
			// play a break sound
			b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
			// update the block
			c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), (int)posadjUn.x, (int)posadjUn.z, block);
			b.onBlockBreaked((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z), world);
			// send block update around a pos
			world.updateBlocksAround((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
			// update the local chunk meshes
			// using a nice new thread as we don't want a new one per chunk.
			c.remeshPRI();
			Chunk cn = getTerrain(posadjUn.x, posadjUn.z, 1, 0);
			cn.remeshPRI();
			Chunk cp = getTerrain(posadjUn.x, posadjUn.z, -1, 0);
			cp.remeshPRI();
			Chunk cg = getTerrain(posadjUn.x, posadjUn.z, 0, -1);
			cg.remeshPRI();
			Chunk ce = getTerrain(posadjUn.x, posadjUn.z, 0, 1);
			ce.remeshPRI();
			
			return blockid;
		}
		return 0;
	}
	
	/**
	 * returns the current block id the player is looking at
	 */
	public int getCurrentBlockPoint() {
		// again this stuff is exactly the same as ^ but with a different return
		
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(currentRay.x/10, currentRay.y/10, currentRay.z/10);
		Vector3f currentRay = biasVector(MouseBlockPicker.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RECURSION_AMOUNT; i++) {
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
			if (c == null)
				continue;
			posadj.x %= Chunk.x;
			posadj.z %= Chunk.z;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0 || blockid == 3)
				continue;
			return blockid;
		}
		return 0;
	}
	
	/**
	 * returns the current block position that the player is looking at <br>
	 * 		0 - x <br>
	 * 		1 - y <br>
	 * 		2 - z <br>
	 * 		3 - id
	 */
	public int[] getCurrentBlockPointPos() {
		// again this stuff is exactly the same as ^ but with a different return
		Vector3f pos = camera.getPosition();
		int[] blockPos = {(int) pos.x, (int) pos.y, (int) pos.z, 0};
		Vector3f pointRay = new Vector3f(currentRay.x/10, currentRay.y/10,currentRay.z/10);
		Vector3f currentRay = biasVector(MouseBlockPicker.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RECURSION_AMOUNT; i++) {
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
			if (c == null)
				continue;
			posadj.x %= Chunk.x;
			posadj.z %= Chunk.z;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0 || blockid == 3)
				continue;
			blockPos[0] = (int)(posadjUn.x);
			blockPos[1] = (int)posadjUn.y;
			blockPos[2] = (int)(posadjUn.z);
			blockPos[3] = blockid;
			return blockPos;
		}
		return blockPos;
	}
	
	/**
	 * returns block pos but like with some biasing in the negatives? but id is always 0
	 */
	public int[] getCurrentBlockPoF() {
		// again this stuff is exactly the same as ^ but with a different return
		// I actually don't currently know what this is used for
		// guess I'll find out when I find it.
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(MouseBlockPicker.currentRay.x/10, MouseBlockPicker.currentRay.y/10,MouseBlockPicker.currentRay.z/10);
		Vector3f currentRay = biasVector(MouseBlockPicker.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		int[] posi = {(int)currentRay.x, (int)currentRay.y, (int)currentRay.z, 1};
		
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RECURSION_AMOUNT; i++) {
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
			if (c == null)
				continue;
			posadj.x %= Chunk.x;
			posadj.z %= Chunk.z;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0 || blockid == 3)
				continue;
			posi[0] = (int)posadjUn.x;
			posi[1] = (int)posadjUn.y;
			posi[2] = (int)posadjUn.z;
			posi[3] = 0;
			if (posadjUn.x < 0)
				posi[0]-=1;
			if (posadjUn.z < 0)
				posi[2]-=1;
			return posi;
		}
		return posi;
	}
	
	/**
	 * Mines a block
	 * Returns the mined id
	 */
	public int mineBlock() {
		if (Mouse.isGrabbed())
			return this.setCurrentBlockPoint((short)0);
		return 0;
	}
	
	/**
	 * Places a block at the current point the player is looking at
	 */
	public boolean placeBlock(short block) {
		if (!Mouse.isGrabbed())
			return false;
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(MouseBlockPicker.currentRay.x/10, MouseBlockPicker.currentRay.y/10,MouseBlockPicker.currentRay.z/10);
		// makes the ray only what we can go
		Vector3f currentRay = findRange();
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		int[] pointf = getCurrentBlockPointPos();
		if (!Block.blocks.get((short)pointf[3]).onBlockInteract(pointf[0], pointf[1], pointf[2], world, i)) {
			if (!Item.itemBlocks.containsKey(Item.items.get(block)) || block == 0)
				return false;
			// since we want to start at the farthest place we are looking at
			// we start at the longest position
			Vector3f walked = new Vector3f(currentRay.x, currentRay.y, currentRay.z);
			for (int i = 0; i < RECURSION_AMOUNT; i++) {
				// walk negative
				walked.x -= xStep;
				walked.y -= yStep;
				walked.z -= zStep;
				// real position
				Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
				Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
				Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
				if (c == null)
					continue;
				// convert to block pos
				posadj.x %= Chunk.x;
				posadj.z %= Chunk.z;
				// bias for negative numbers
				if (posadj.x < 0)
					posadj.x = biasNegative(posadj.x, -Chunk.x);
				if (posadj.z < 0)
					posadj.z = biasNegative(posadj.z, -Chunk.z);
				// get the block
				short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
				// make sure we don't try to place in stone
				if (blockid == 0 && !ply.hasCollision(posadjUn)) {
					// maybe we should play a sound?
					// not the break sound
					//b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
					//b.onBlockBreaked((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z), world);
					// place the block
					c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), (int)posadjUn.x, (int)posadjUn.z, block);
					// update blocks around
					world.updateBlocksAround((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
					// remesh this chunk
					c.remesh();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * finds the max range the ray can go
	 */
	private Vector3f findRange() {
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(MouseBlockPicker.currentRay.x/10, MouseBlockPicker.currentRay.y/10,MouseBlockPicker.currentRay.z/10);
		Vector3f currentRay = biasVector(MouseBlockPicker.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		// walk the numbers up until we find a block that isn't air
		// then return the largest ray we got to.
		// just like the other ones but this time we return the ray with the largest magnitude
		// without going through walls or anything
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RECURSION_AMOUNT; i++) {
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Vector3f posadjUn = new Vector3f(walked.x, walked.y, walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z, 0, 0);
			if (c == null)
				continue;
			posadj.x %= Chunk.x;
			posadj.z %= Chunk.z;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0)
				continue;
			return posadjUn;
		}
		return currentRay;
	}
	
	private float prog = 200;
	private float totalProg = 200;
	public void update() {
		// calculate some math
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		// hey i found this thing
		int[] current = getCurrentBlockPoF();
		renderer.renderOverlay(current);
		// TODO: cleanup this code and stop using so much ray tracing calls.
		/*
		 * If your looking at this wondering; what is this?!?!
		 * Yeah me too
		 * If I recall correctly I did this late at night.
		 * Like I kind of get it but its a mess of random variables like q and mq (Changed these)
		 */
		if (Mouse.isButtonDown(0)) {
			if (!Mouse.isGrabbed())
				return;
			// get some stuff we need
			int id = getCurrentBlockPoint();
			Block b = Block.blocks.get((short)id);
			float hardness = b.getHardness();
			int toolType = b.getEffectiveTool();
			int mlevel = b.getMiningLevel();
			Item it = null;
			// get the item
			if (i.getItemInSelectedSlot() != null)
				it = i.getItemInSelectedSlot().getItem();
			float miningspeed = 0;
			int milevel = 0;
			int tool = 0;
			// set our values based on the stack
			if (it == null) {
				milevel = 0;
				// mining speed of hand, it is slightly more then any random item
				miningspeed = 0.21f;
			} else {
				milevel = it.getMiningLevel();
				miningspeed = it.getMiningSpeed();
				if (it instanceof ItemTool)
					tool = ((ItemTool) it).getToolType();
				else
					tool = 0;
			}
			// only mine if the tool mine level is higher then the block mine level
			if (mlevel > milevel) {
				// do partile stuff
			} else {
				// if the block has changed then we need to reset the values
				if (blockChanged(current)) {
					totalProg = hardness * 10 * DisplayManager.getFrameTimeSeconds();
					// makes it harder to mine if not using proper tool.
					if (toolType != tool)
						totalProg *= 10;
					// reset the progress
					prog = totalProg;
					// change overlay to represent this
					renderer.changeOverlayProgress(prog, totalProg);
				} else {
					// reduce the amount of progress left
					prog -= miningspeed * DisplayManager.getFrameTimeSeconds();
					// update overlay to reflect this
					renderer.changeOverlayProgress(prog, totalProg);
					// if we are done then remove block
					if (prog <= 0) {
						// remove the block
						int bid = mineBlock();
						// reset the progress
						prog = totalProg;
						// update the overlay progress
						renderer.changeOverlayProgress(prog, totalProg);
						// add item to inventory if it exists and drops stuff
						if (bid != 0) {
							if (Block.blocks.get((short)id).getBlockDropped() > 0) {
								i.addItemToInventory(new ItemStack(
										Item.items.get(Block.blocks.get((short)id).getBlockDropped()), 
												Block.blocks.get((short)id).getAmountDropped()));
							}
							// call the tools on block mined.
							if (it instanceof ItemTool)
								((ItemTool)it).onBlockMined(current[0], current[1], current[2], b, world, camera, i);
						}
					}
				}
			}
		} else {
			// reset progress
			prog = totalProg;
			renderer.changeOverlayProgress(prog, totalProg);
		}
	}
	
	// last block pos
	int[] last = {0,0,0};
	/**
	 * returns true if the block has changed.
	 */
	private boolean blockChanged(int[] c) {
		// check if the last array is the same as this array
		if (isYes(last, c)) {
			return false;
		} else {
			last = c;
			return true;
		}
	}
	
	/**
	 * makes sure the supplied 3d integer vectors are the same
	 */
	private boolean isYes(int[] x, int[] y) {
		if (x[0] == y[0] && x[1] == y[1] && x[2] == y[2])
			return true;
		return false;
	}
	
	/**
	 * returns a chunk from a world coordinate, adjusts for negatives
	 * offset values are applied last after the math. (along with negative offset)
	 */
	private Chunk getTerrain(float worldX, float worldZ, int xof, int zof) {
		int xoff = 0,zoff = 0;
		if (worldX < 0)
			xoff = -1;
		if (worldZ < 0)
			zoff = -1;
		return world.chunk.getChunk((int)(worldX/(float)Chunk.x) + xof + xoff, (int)(worldZ/(float)Chunk.z) + zof + zoff);
	}
	
	/**
	 * multiplies vector by constant
	 */
	private Vector3f biasVector(Vector3f v, float x) {
		return new Vector3f(v.x * x, v.y * x, v.z * x);
	}
	
	/**
	 * does negative things that aren't needed but i had a hard time setting up and getting
	 * right so I keep it.
	 */
	private float biasNegative(float f, float unitSize) {
		return unitSize - f;
	}
	
	/**
	 * NOT MY CODE BELOW
	 * http://antongerdelan.net/opengl/raycasting.html
	 */
	private Vector3f calculateMouseRay() {
		float mouseX = Display.getWidth()/2;
		float mouseY = Display.getHeight()/2;
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
}
