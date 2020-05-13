package com.brett.tools;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.brett.world.cameras.ICamera;

public class Maths {
	
	public static enum colors { NULL, COLOR_RED, COLOR_BLUE, COLOR_GREEN };
	
	public static Vector3f rx = new Vector3f(1,0,0);
	public static Vector3f ry = new Vector3f(0,1,0);
	public static Vector3f rz = new Vector3f(0,0,1);
	
	static Matrix4f mtx = new Matrix4f();
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		mtx.setIdentity();
		Matrix4f.translate(translation, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(rx), Maths.rx, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(ry), Maths.ry, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(rz), Maths.rz, mtx, mtx);
		Matrix4f.scale(new Vector3f(scale,scale,scale), mtx, mtx);
		return mtx;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		mtx.setIdentity();
		Matrix4f.translate(translation, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), Maths.rx, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), Maths.ry, mtx, mtx);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), Maths.rz, mtx, mtx);
		Matrix4f.scale(scale, mtx, mtx);
		return mtx;
	}
	
	static Matrix4f mrx = new Matrix4f();
	public static Matrix4f createTransformationMatrix(Vector3f translation, float scale) {
		mrx.setIdentity();
		Matrix4f.translate(translation, mrx, mrx);
		Matrix4f.scale(new Vector3f(scale,scale,scale), mrx, mrx);
		return mrx;
	}
	
	// i use different matrix for each function to make sure that
	// there is no concurrent modification errors.
	// i know it won't happen but just in case
	static Matrix4f mrxx = new Matrix4f();
	public static Matrix4f createTransformationMatrix(Vector3f translation) {
		mrxx.setIdentity();
		Matrix4f.translate(translation, mrxx, mrxx);
		return mrxx;
	}
	
	static Matrix4f matrix = new Matrix4f();
	/**
	 * Creates a transformation matrix for a cube based on its X, Y, Z pos in the world.
	 */
	public static Matrix4f createTransformationMatrixCube(float x, float y, float z) {
		matrix.setIdentity();
		// the 0.5 is added to adjust for cube scale.
		// took way to long to figure out this.
		matrix.m30 += matrix.m00 * (x+0.5f) + matrix.m10 * (y+0.5f) + matrix.m20 * (z+0.5f);
		matrix.m31 += matrix.m01 * (x+0.5f) + matrix.m11 * (y+0.5f) + matrix.m21 * (z+0.5f);
		matrix.m32 += matrix.m02 * (x+0.5f) + matrix.m12 * (y+0.5f) + matrix.m22 * (z+0.5f);
		matrix.m33 += matrix.m03 * (x+0.5f) + matrix.m13 * (y+0.5f) + matrix.m23 * (z+0.5f);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrixCube(int x, int y, int z) {
		matrix.setIdentity();
		// the 0.5 is added to adjust for cube scale.
		// took way to long to figure out this.
		matrix.m30 += matrix.m00 * (x+0.5f) + matrix.m10 * (y+0.5f) + matrix.m20 * (z+0.5f);
		matrix.m31 += matrix.m01 * (x+0.5f) + matrix.m11 * (y+0.5f) + matrix.m21 * (z+0.5f);
		matrix.m32 += matrix.m02 * (x+0.5f) + matrix.m12 * (y+0.5f) + matrix.m22 * (z+0.5f);
		matrix.m33 += matrix.m03 * (x+0.5f) + matrix.m13 * (y+0.5f) + matrix.m23 * (z+0.5f);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float SWIDTH, float SHEIGHT, float x, float y, float width, float height) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(new Vector2f(x / SWIDTH, y / SHEIGHT), matrix, matrix);
		Matrix4f.scale(new Vector3f(width / SWIDTH, height / SHEIGHT, 1f), matrix, matrix);
		return matrix;
	}
	
	/**
	 * 
	 */
	public static Matrix4f createTransformationMatrixCenteredSTATIC(float SWIDTH, float SHEIGHT, float width, float height) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(new Vector2f(SWIDTH/2 - width/2, SHEIGHT/2 - height/2), matrix, matrix);
		Matrix4f.scale(new Vector3f(width / SWIDTH, height / SHEIGHT, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrixCenteredSTATIC(float SWIDTH, float SHEIGHT, float width, float height, float rot) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(new Vector2f(SWIDTH/2 - width/2, SHEIGHT/2 - height/2), matrix, matrix);
		Matrix4f.rotate(rot, rx, matrix, matrix);
		Matrix4f.scale(new Vector3f(width / SWIDTH, height / SHEIGHT, 1f), matrix, matrix);
		return matrix;
	}
	
	public static double dotProduct(double[] a, double[] b) {
	    double sum = 0;
	    for (int i = 0; i < a.length; i++) {
	      sum += a[i] * b[i];
	    }
	    return sum;
	}
	
	// Thanks to wikipedia for this
	// matrix maths is not fun
	// this took 3 hours to make work. turns out i needed to disable face culling
	// :(
	public static Matrix4f ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
		Matrix4f m = new Matrix4f();

	    m.m00 = 2 / (right - left);
		m.m11 = 2 / (top - bottom);
		m.m22 = -2 / (zFar - zNear);
		m.m30 = -(right + left) / (right - left);
		m.m31 = -(top + bottom) / (top - bottom);
		m.m32 = -(zFar + zNear) / (zFar - zNear);
			
		return m;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,
			float rz, float sx, float sy, float sz) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(sx,sy,sz), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f rotation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), matrix, matrix);
		return matrix;
	}
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	static Matrix4f viewMatrix = new Matrix4f();
	public static Matrix4f createViewMatrix(ICamera camera) {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static Matrix4f createViewMatrixOTHER(ICamera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	
	public static float lerp(float point1, float point2, float alpha){
	    return point1 + alpha * (point2 - point1);
	}
	
	public static Vector3f lerp(Vector3f point1, Vector3f point2, float alpha){
		float x = point1.x + alpha * (point2.x - point1.x);
		float y = point1.y + alpha * (point2.y - point1.y);
		float z = point1.z + alpha * (point2.z - point1.z);
	    return new Vector3f(x, y, z);
	}
	public static Vector3f lerpA(Vector3f point1, Vector3f point2, float alpha){
		float x = point1.x + alpha * (point2.x - point1.x);
		float y = point1.y + alpha * (point2.y - point1.y);
		float z = point1.z + alpha * (point2.z - point1.z);
		point1.x += x;
		point1.y += y;
		point1.z += z;
		
	    return new Vector3f(x, y, z);
	}
	public static Vector3f distance(Vector3f point1, Vector3f point2) {
		return new Vector3f(point1.x - point2.x, point1.y - point2.y, point1.z - point2.z);
	}
	public static Vector3f distanceABS(Vector3f point1, Vector3f point2) {
		return new Vector3f(Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y), Math.abs(point1.z - point2.z));
	}
	
	public static Vector3f decodeRGB255(int r, int g, int b) {
		return new Vector3f(r/255,g/255,b/255);
	}
	
	public static float decodeRGB255(int b) {
		return b / 255;
	}
	
	public static double roundDown5(double d) {
	    return (long) (d * 1e5) / 1e5;
	}
	public static double roundDown4(double d) {
	    return (long) (d * 1e4) / 1e4;
	}
	public static double roundDown3(double d) {
	    return (long) (d * 1e3) / 1e3;
	}
	public static double roundDown2(double d) {
	    return (long) (d * 1e2) / 1e2;
	}
	// Don't use!
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	public static int randomInt(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randomNum;
	}
	
	public static colors getEntityColor(Vector3f color) {
		if (color.x > color.y && color.x > color.z){
			return colors.COLOR_RED;
		} else if (color.y > color.x && color.y > color.z) {
			return colors.COLOR_GREEN;
		} else if (color.z > color.y && color.z > color.x) {
			return colors.COLOR_BLUE;
		}
		System.err.println("We are at the end of the line \n Please fix this method!");
		return colors.NULL;
	}
	
	public static Vector3f distance2(Vector3f first, Vector3f last) {
		return new Vector3f(first.x - last.x, first.y - last.y, first.z - last.z);
	}
	
	public static float randomFloat(float min, float max) {
		return (float) (min + Math.random() * (max - min));
	}
	
	public static float randomFloat(float min, float max, Random rand) {
		return (float) (min + rand.nextDouble() * (max - min));
	}
	
	public static Vector3f addVectors(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
	}
	
	// do not run at runtime. only at startup.
	public static long convertToBytes(String s) {
		long t = 0;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			t += (byte) chars[i];
		}
		
		return t;
	}
	
	public static long preventNegs(Long num) {
		if (num < 0)
			num = 0l;
		return num;
	}

	/*// Or this. Slightly slower, but faster than creating objects. ;)
	public static double roundDown5(double d) {
	    return Math.floor(d * 1e5) / 1e5;
	}*/
	
}