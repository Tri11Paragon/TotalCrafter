package com.brett.world.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
*
* @author brett
*
*/

public class HeightMap extends HeightData {

	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private BufferedImage image = null;
	private int xOffset = 0, zOffset = 0;
	
	public HeightMap(String heightmap) {
		try {
			image = ImageIO.read(new File("resources/textures/terrain/" + heightmap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		size = image.getHeight();
	}
	
	public HeightMap(String heightmap, int gridX, int gridZ, int vertexCount) {
		this.size = vertexCount;
		xOffset = gridX * (vertexCount-1);
        zOffset = gridZ * (vertexCount-1);
		try {
			image = ImageIO.read(new File("resources/textures/terrain/" + heightmap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public float getHeight(int x, int z) {
		float x_ = Math.abs(x + xOffset);
		float z_ = Math.abs(z + zOffset);
		return getHeight((int)x_, (int)z_, image);
	}
	
	public float getHeight(int x, int y, BufferedImage image) {
		if (image == null)
			return 0;
		if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, y);
		height += MAX_PIXEL_COLOR / 2;
		height /= MAX_PIXEL_COLOR / 2;
		height *= Terrain.MAX_HEIGHT;
		return height;
	}
	
}
