package com.brett.tools;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.brett.renderer.world.Chunk;
import com.brett.world.VoxelWorld;
import com.brett.world.cameras.Camera;
import com.brett.world.terrain.Terrain;

/**
*
* @author brett
* @date Feb. 19, 2020
*/

public class MouseBlockPicker {

	
	// http://antongerdelan.net/opengl/raycasting.html
	
	// amount of times the binary search can run
	private static final int RECURSION_COUNT = 500;
	private static final float RAY_RANGE = 10;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	private VoxelWorld world;
	private Vector3f currentTerrainPoint;

	public MouseBlockPicker(Camera cam, Matrix4f projection, VoxelWorld terrain) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = Maths.createViewMatrix(camera);
		this.world = terrain;
	}
	
	/**
	 * Returns the current point on the terrain. This should not be used
	 * and will be removed
	 * 
	 * @return the current terrain point
	 */
	@Deprecated
	public Vector3f getCurrentTerrainPointFast() {
		return currentTerrainPoint;
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
			///System.out.println("SEACHING");
			Vector3f pos = binarySearch(0, 0, RAY_RANGE, currentRay);
			System.out.println(pos);
			Chunk c = getTerrain(pos.x, pos.z);
			if (c == null)
				return 0;
			return c.getBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16);
		}else
			return 0;
	}
	
	public void setCurrectBlockPoint(int block) {
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			///System.out.println("SEACHING");
			Vector3f pos = binarySearch(0, 0, RAY_RANGE, currentRay);
			Chunk c = getTerrain(pos.x, pos.z);
			if (c == null)
				return;
			c.setBlock((int)(pos.x)%16, (int)pos.y, (int)(pos.z)%16, block);
			c.remesh();
		}
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
		
		// DEPRECATED
		/*if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}*/
	}

	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
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
			height = terrain.getHeight((int)testPoint.getX() % 16, (int)testPoint.getZ() % 16);
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Chunk getTerrain(float worldX, float worldZ) {
		//System.out.println(world.chunk.getChunk((int) (worldX/16.0), (int) (worldZ/16.0)).getHeight((int)worldX, (int)worldZ));
		return world.chunk.getChunk((int) (worldX/16.0), (int) (worldZ/16.0));
	}
	
	
	
}
