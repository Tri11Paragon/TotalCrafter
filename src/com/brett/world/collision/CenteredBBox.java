package com.brett.world.collision;

import org.lwjgl.util.vector.Vector3f;

/** 
*	Brett Terpstra
*	Mar 6, 2020
*	
*/
public class CenteredBBox extends BoundingBox {

	public CenteredBBox(float x1, float y1, float z1, float x2, float y2, float z2) {
		super(x1 - x2, y1 - y2, z1 - z2, x2, y2, z2);
	}
	
	public CenteredBBox(Vector3f pos1, Vector3f size) {
		super(pos1.x - size.x, pos1.y - size.y, pos1.z - size.z, size.x, size.y, size.z);
	}

}
