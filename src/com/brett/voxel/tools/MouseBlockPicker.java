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
	
	// amount of times the binary search can run
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
	
	private int setCurrentBlockPoint(short block) {
		Vector3f pos = camera.getPosition();
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
			Chunk c = getTerrain(posadj.x, posadj.z);
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
			Block b = Block.blocks.get(blockid);
			b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
			c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), (int)posadjUn.x, (int)posadjUn.z, block);
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
	
	public int getCurrentBlockPoint() {
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
			Chunk c = getTerrain(posadj.x, posadj.z);
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
	
	public int[] getCurrentBlockPointPos() {
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
			Chunk c = getTerrain(posadj.x, posadj.z);
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
	
	public int[] getCurrentBlockPoF() {
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
			Chunk c = getTerrain(posadj.x, posadj.z);
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
	
	public int mineBlock() {
		if (Mouse.isGrabbed())
			return this.setCurrentBlockPoint((short)0);
		return 0;
	}
	
	public boolean placeBlock(short block) {
		if (!Mouse.isGrabbed())
			return false;
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(MouseBlockPicker.currentRay.x/10, MouseBlockPicker.currentRay.y/10,MouseBlockPicker.currentRay.z/10);
		Vector3f currentRay = findRange();
		float xStep = (currentRay.x-pointRay.x)/RECURSION_AMOUNT;
		float yStep = (currentRay.y-pointRay.y)/RECURSION_AMOUNT;
		float zStep = (currentRay.z-pointRay.z)/RECURSION_AMOUNT;
		
		int[] pointf = getCurrentBlockPointPos();
		if (!Block.blocks.get((short)pointf[3]).onBlockInteract(pointf[0], pointf[1], pointf[2], world, i)) {
			if (!Item.itemBlocks.containsKey(Item.items.get(block)) || block == 0)
				return false;
			Vector3f walked = new Vector3f(currentRay.x, currentRay.y, currentRay.z);
			for (int i = 0; i < RECURSION_AMOUNT; i++) {
				walked.x -= xStep;
				walked.y -= yStep;
				walked.z -= zStep;
				Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
				Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
				Chunk c = getTerrain(posadj.x, posadj.z);
				if (c == null)
					continue;
				posadj.x %= Chunk.x;
				posadj.z %= Chunk.z;
				if (posadj.x < 0)
					posadj.x = biasNegative(posadj.x, -Chunk.x);
				if (posadj.z < 0)
					posadj.z = biasNegative(posadj.z, -Chunk.z);
				short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
				if (blockid == 0 && !ply.hasCollision(posadjUn)) {
					//b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
					//b.onBlockBreaked((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z), world);
					c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), (int)posadjUn.x, (int)posadjUn.z, block);
					world.updateBlocksAround((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
					c.remesh();
					return true;
				}
			}
		}
		return false;
	}
	
	private Vector3f findRange() {
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(MouseBlockPicker.currentRay.x/10, MouseBlockPicker.currentRay.y/10,MouseBlockPicker.currentRay.z/10);
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
			Vector3f posadjUn = new Vector3f(walked.x, walked.y, walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z);
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
	
	private float q = 200;
	private float mq = 200;
	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		int[] current = getCurrentBlockPoF();
		renderer.renderOverlay(current);
		// TODO: cleanup this code and stop using so much ray tracing calls.
		if (Mouse.isButtonDown(0)) {
			if (!Mouse.isGrabbed())
				return;
			int id = getCurrentBlockPoint();
			Block b = Block.blocks.get((short)id);
			float hardness = b.getHardness();
			int toolType = b.getEffectiveTool();
			int mlevel = b.getMiningLevel();
			Item it = null;
			if (i.getItemInSelectedSlot() != null)
				it = i.getItemInSelectedSlot().getItem();
			float miningspeed = 0;
			int milevel = 0;
			int tool = 0;
			if (it == null) {
				milevel = 0;
				miningspeed = 0.18f;
			} else {
				milevel = it.getMiningLevel();
				miningspeed = it.getMiningSpeed();
				if (it instanceof ItemTool)
					tool = ((ItemTool) it).getToolType();
				else
					tool = 0;
			}
			if (mlevel > milevel) {
				// do partile stuff
			} else {
				if (blockChanged(current)) {
					mq = hardness * 10 * DisplayManager.getFrameTimeSeconds();
					// makes it harder to mine if not using proper tool.
					if (toolType != tool)
						mq *= 10;
					q = mq;
					renderer.changeOverlayProgress(q, mq);
				} else {
					q -= miningspeed * DisplayManager.getFrameTimeSeconds();
					renderer.changeOverlayProgress(q, mq);
					if (q <= 0) {
						int bid = mineBlock();
						q = mq;
						renderer.changeOverlayProgress(q, mq);
						if (bid != 0) {
							if (Block.blocks.get((short)id).getBlockDropped() > 0) {
								i.addItemToInventory(new ItemStack(
										Item.items.get(Block.blocks.get((short)id).getBlockDropped()), 
												Block.blocks.get((short)id).getAmountDropped()));
							}
							if (it instanceof ItemTool)
								((ItemTool)it).onBlockMined(current[0], current[1], current[2], b, world, camera, i);
						}
					}
				}
			}
		} else {
			q = mq;
			renderer.changeOverlayProgress(q, mq);
		}
	}
	
	int[] last = {0,0,0};
	private boolean blockChanged(int[] c) {
		if (isYes(last, c)) {
			return false;
		} else {
			last = c;
			return true;
		}
	}
	
	private boolean isYes(int[] x, int[] y) {
		if (x[0] == y[0] && x[1] == y[1] && x[2] == y[2])
			return true;
		return false;
	}

	private Chunk getTerrain(float worldX, float worldZ) {
		int xoff = 0,zoff = 0;
		if (worldX < 0)
			xoff = -1;
		if (worldZ < 0)
			zoff = -1;
		return world.chunk.getChunk((int)(worldX/(float)Chunk.x) + xoff, (int)(worldZ/(float)Chunk.z) + zoff);
	}
	
	private Chunk getTerrain(float worldX, float worldZ, int xof, int zof) {
		int xoff = 0,zoff = 0;
		if (worldX < 0)
			xoff = -1;
		if (worldZ < 0)
			zoff = -1;
		return world.chunk.getChunk((int)(worldX/(float)Chunk.x) + xof + xoff, (int)(worldZ/(float)Chunk.z) + zof + zoff);
	}
	
	private Vector3f biasVector(Vector3f v, float x) {
		return new Vector3f(v.x * x, v.y * x, v.z * x);
	}
	
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
