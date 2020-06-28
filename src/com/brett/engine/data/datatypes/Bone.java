package com.brett.engine.data.datatypes;

import org.joml.Matrix4f;

/**
 * @author Brett
 * @date Jun. 27, 2020
 */

public class Bone {

	private final int boneId;

	private final String boneName;

	private Matrix4f offsetMatrix;

	public Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
		this.boneId = boneId;
		this.boneName = boneName;
		this.offsetMatrix = offsetMatrix;
	}

	public int getBoneId() {
		return boneId;
	}

	public String getBoneName() {
		return boneName;
	}

	public Matrix4f getOffsetMatrix() {
		return offsetMatrix;
	}

}
