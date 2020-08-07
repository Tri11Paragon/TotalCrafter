package com.brett.world.tools;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.brett.engine.cameras.ICamera;
import com.brett.engine.managers.DisplayManager;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.ui.screen.SinglePlayer;

/**
* @author Brett
* @date Jul. 8, 2020
*/

public class RayCasting {
	
	public static Vector3f currentRay = new Vector3f();

	private static Matrix4f protectionProjection = new Matrix4f();
	private static Matrix4f viewProjection = new Matrix4f();
	
	public static void update(ICamera camera) {
		currentRay = calculateMouseRay();
	}
	
	private static synchronized Vector3f calculateMouseRay() {
		float mouseX = DisplayManager.WIDTH/2;
		float mouseY = DisplayManager.HEIGHT/2;
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private static synchronized Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = SinglePlayer.viewMatrix.invert(viewProjection);
		Vector4f rayWorld = invertedView.transform(eyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private static synchronized Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = ProjectionMatrix.projectionMatrix.invert(protectionProjection);
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private static synchronized Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / DisplayManager.WIDTH - 1f;
		float y = (2.0f * mouseY) / DisplayManager.HEIGHT - 1f;
		return new Vector2f(x, y);
	}
	
}
