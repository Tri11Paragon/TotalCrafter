package com.brett.world.cameras;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * View frustums help:
 * http://www8.cs.umu.se/kurser/5DV051/HT12/lab/plane_extraction.pdf
 * 
 * @author brett
 */
public class Camera {
	
	/*
	 * Left: 0
	 * Right: 1
	 * Bottom: 2
	 * Top: 3
	 * Near: 4
	 * Far: 5
	 * 
	 * a: 0
	 * b: 1
	 * c: 2
	 * d: 3
	 */
	private float[][] clippingPlanes = new float[6][4];
	
	protected Vector3f position = new Vector3f(0, 0, 0);
	protected float pitch;
	protected float yaw;
	protected float roll;
	
	public Camera() {
		
	}
	
	public void move() {
		
	}
	
	Matrix4f ins = new Matrix4f();
	/**
	 * Generates the view frustum of the camera
	 * this should be threadsafe.
	 */
	public synchronized void generateFrustum(Matrix4f project, Matrix4f view) {
		ins.setIdentity();
		Matrix4f.mul(project, view, ins);
		clippingPlanes[0][0] = ins.m30 + ins.m00;
		clippingPlanes[0][1] = ins.m31 + ins.m01;
		clippingPlanes[0][2] = ins.m32 + ins.m02;
		clippingPlanes[0][3] = ins.m33 + ins.m03;
		
		clippingPlanes[1][0] = ins.m30 - ins.m00;
		clippingPlanes[1][1] = ins.m31 - ins.m01;
		clippingPlanes[1][2] = ins.m32 - ins.m02;
		clippingPlanes[1][3] = ins.m33 - ins.m03;
		
		clippingPlanes[2][0] = ins.m30 + ins.m10;
		clippingPlanes[2][1] = ins.m31 + ins.m11;
		clippingPlanes[2][2] = ins.m32 + ins.m12;
		clippingPlanes[2][3] = ins.m33 + ins.m13;
		
		clippingPlanes[3][0] = ins.m30 - ins.m10;
		clippingPlanes[3][1] = ins.m31 - ins.m11;
		clippingPlanes[3][2] = ins.m32 - ins.m12;
		clippingPlanes[3][3] = ins.m33 - ins.m13;
		
		clippingPlanes[4][0] = ins.m30 + ins.m20;
		clippingPlanes[4][1] = ins.m31 + ins.m21;
		clippingPlanes[4][2] = ins.m32 + ins.m22;
		clippingPlanes[4][3] = ins.m33 + ins.m23;
		
		clippingPlanes[5][0] = ins.m30 - ins.m20;
		clippingPlanes[5][1] = ins.m31 - ins.m21;
		clippingPlanes[5][2] = ins.m32 - ins.m22;
		clippingPlanes[5][3] = ins.m33 - ins.m23;
		
		/*normalizePlane(clippingPlanes, 0);
		normalizePlane(clippingPlanes, 1);
		normalizePlane(clippingPlanes, 2);
		normalizePlane(clippingPlanes, 3);
		normalizePlane(clippingPlanes, 4);
		normalizePlane(clippingPlanes, 5);*/
		//http://www.java-gaming.org/index.php?action=pastebin&hex=8c3850c73171d
	}
	
	private void normalizePlane(float[][] frustum, int side) {
		float mag = (float) Math.sqrt(frustum[side][0] * frustum[side][0] + frustum[side][1] * frustum[side][1] + frustum[side][2] * frustum[side][2]);
		frustum[side][0] = frustum[side][0] / mag;
		frustum[side][1] = frustum[side][1] / mag;
		frustum[side][2] = frustum[side][2] / mag;
		frustum[side][3] = frustum[side][3] / mag;
	}
	
	private float distanceToPoint(float[][] frustum, int side, Vector3f point) {
		return (frustum[side][0]*point.x) + (frustum[side][1]*point.y) + (frustum[side][2]*point.z) + frustum[side][3];
	}
	
	private float distanceToPoint(float[][] frustum, int side, float x, float y, float z) {
		return (frustum[side][0]*x) + (frustum[side][1]*y) + (frustum[side][2]*z) + frustum[side][3];
	}
	
	private float distanceToPoint(float[][] frustum, int side, float x, float z) {
		return (frustum[side][0]*x) + (frustum[side][2]*z) + frustum[side][3];
	}
	
	public boolean planeIntersection(Vector3f pos, float radius) {
		for (int i = 0; i < 6; i++) {
			if ((distanceToPoint(clippingPlanes, i, pos) + radius) <= 0)
				return false;
		}
		
		return true;
	}
	
	public boolean planeIntersection(float x, float y, float z, float radius) {
		for (int i = 0; i < 6; i++) {
			//System.out.println(distanceToPoint(clippingPlanes, i, x, z) + radius);
			if ((distanceToPoint(clippingPlanes, i, x,y,z) + radius) <= 0)
				return false;
		}
		
		return true;
	}
	
	public boolean planeIntersection(float x, float z, float radius) {
		for (int i = 0; i < 6; i++) {
			if ((distanceToPoint(clippingPlanes, i, x,z) + radius) <= 0)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Getters and Setters below
	 * --------------------------
	 */
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setYawPitchRoll(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	public void invertPitch() {
		pitch = -pitch;
	}
	
}
