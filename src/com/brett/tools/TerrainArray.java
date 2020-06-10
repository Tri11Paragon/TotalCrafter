package com.brett.tools;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.brett.world.terrain.Terrain;

/**
 * 
 * @author brett
 * Users are encouraged to make their own terrain manager
 * 
 * PLEASE PLEASE IGNORE THIS.
 * I removed most of the code because I am very embarrassed that I wrote it.
 * its a wonder my program ever ran at any FPS
 * it was VERY bad code
 * 
 */
public class TerrainArray {

	// Bad but convent way of doing terrain
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	public void add(Terrain terrain) {
		terrains.add(terrain);
	}
	
	// when i thought of this it was much better in my head
	// now it does not look at good as i thought
	public Terrain get(Vector3f position) {
		return null;
	}
	
	public List<Terrain> getAll() {
		return terrains;
	}
	
	public void remove(Terrain terrain) {
		terrains.remove(terrain);
	}
	
}
