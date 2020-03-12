package com.brett.voxel.tools;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.tools.Maths;
import com.brett.voxel.world.Chunk;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.blocks.Block;
import com.brett.world.cameras.Camera;

/**
*
* @author brett
* @date Feb. 19, 2020
*/

public class MouseBlockPicker {

	
	// http://antongerdelan.net/opengl/raycasting.html
	
	// amount of times the binary search can run
	private static final int RECURSION_COUNT = 200;
	private static final int RE_MNT = 12;
	private static final float RAY_RANGE = 6;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private VoxelWorld world;

	public MouseBlockPicker(Camera cam, Matrix4f projection, VoxelWorld terrain) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.world = terrain;
	}
	
	public Vector3f getCurrentTerrainPoint() {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			///System.out.println("SEACHING");
			return binarySearch(0, 0, RAY_RANGE, currentRay);
		}else
			return null;
	}
	
	public int getCurrentBlockPoint() {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			Vector3f pos = accountForFloatErrors(binarySearch(0, 0, RAY_RANGE, currentRay));
			Chunk c = getTerrain(pos.x, pos.z);
			if (c == null)
				return 0;
			return c.getBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16);
		}else
			return 0;
	}
	
	public void setCurrentBlockPoint(int block) {
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(this.currentRay.x/10, this.currentRay.y/10,this.currentRay.z/10);
		Vector3f currentRay = biasVector(this.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RE_MNT;
		float yStep = (currentRay.y-pointRay.y)/RE_MNT;
		float zStep = (currentRay.z-pointRay.z)/RE_MNT;
		
		Vector3f walked = new Vector3f(pointRay.x, pointRay.y, pointRay.z);
		for (int i = 0; i < RE_MNT; i++) {
			walked.x += xStep;
			walked.y += yStep;
			walked.z += zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z);
			if (c == null)
				continue;
			posadj.x %= 16;
			posadj.z %= 16;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0 || blockid == 3)
				continue;
			Block b = Block.blocks.get(blockid);
			b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
			b.onBlockBreaked((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z), world);
			c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), block);
			world.updateBlocksAround((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
			c.remesh();
			return;
		}
	}
	
	public void mineBlock() {
		this.setCurrentBlockPoint(0);
	}
	
	public void placeBlock(int block) {
		Vector3f pos = camera.getPosition();
		Vector3f pointRay = new Vector3f(this.currentRay.x/10, this.currentRay.y/10,this.currentRay.z/10);
		Vector3f currentRay = biasVector(this.currentRay, RAY_RANGE);
		float xStep = (currentRay.x-pointRay.x)/RE_MNT;
		float yStep = (currentRay.y-pointRay.y)/RE_MNT;
		float zStep = (currentRay.z-pointRay.z)/RE_MNT;
		
		Vector3f walked = new Vector3f(currentRay.x, currentRay.y, currentRay.z);
		for (int i = 0; i < RE_MNT; i++) {
			walked.x -= xStep;
			walked.y -= yStep;
			walked.z -= zStep;
			Vector3f posadj = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Vector3f posadjUn = new Vector3f(pos.x + walked.x, pos.y + walked.y, pos.z + walked.z);
			Chunk c = getTerrain(posadj.x, posadj.z);
			if (c == null)
				continue;
			posadj.x %= 16;
			posadj.z %= 16;
			if (posadj.x < 0)
				posadj.x = biasNegative(posadj.x, -Chunk.x);
			if (posadj.z < 0)
				posadj.z = biasNegative(posadj.z, -Chunk.z);
			short blockid = c.getBlock((int)(posadj.x),(int)posadj.y, (int)(posadj.z));
			if (blockid == 0) {
				Block b = Block.blocks.get(blockid);
				b.playBreakSound((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
				b.onBlockBreaked((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z), world);
				c.setBlock((int)(posadj.x), (int)posadj.y,  (int)(posadj.z), block);
				world.updateBlocksAround((int) (posadjUn.x), (int) posadjUn.y, (int) (posadjUn.z));
				c.remesh();
				return;
			}
		}
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}

	/**
	 * NOT MY CODE BELOW
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
	
	// terrain stuff beyond this point
	//**********************************************************
	
	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}
	
	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Chunk terrain = getTerrain(endPoint.getX(), endPoint.getZ());
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Chunk terrain = getTerrain(testPoint.getX(), testPoint.getZ());
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeight((int)testPoint.getX() % 16, (int)testPoint.getZ() % 16) - 0.0001f;
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Chunk getTerrain(float worldX, float worldZ) {
		int xoff = 0,zoff = 0;
		if (worldX < 0)
			xoff = -1;
		if (worldZ < 0)
			zoff = -1;
		//System.out.println(worldX+" // " + ((int)(worldX/(float)Chunk.x) + xoff) + " - " + worldZ + " // " + ((int)(worldZ/(float)Chunk.z) + zoff));
		return world.chunk.getChunk((int)(worldX/(float)Chunk.x) + xoff, (int)(worldZ/(float)Chunk.z) + zoff);
	}
	
	private Vector3f biasVector(Vector3f v, float x) {
		return new Vector3f(v.x * x, v.y * x, v.z * x);
	}
	
	private float biasNegative(float f, float unitSize) {
		return unitSize - f;
	}
	
	private Vector3f accountForFloatErrors(Vector3f x) {
		if (x == null)
			return x;
		x.x = Math.round(x.x);
		x.y = Math.round(x.y);
		x.z = Math.round(x.z);
		return x;
	}
	
	
}
