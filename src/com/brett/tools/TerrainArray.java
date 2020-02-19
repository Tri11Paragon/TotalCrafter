package com.brett.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.brett.world.terrain.Terrain;

/**
 * 
 * @author brett
 * Users are encouraged to make their own terrain manager
 * 
 */
public class TerrainArray {

	// Bad but convent way of doing terrain
	private List<Terrain> negXnegZTerrain = new ArrayList<Terrain>();
	private List<Terrain> posXnegZTerrain = new ArrayList<Terrain>();
	private List<Terrain> negXposZTerrain = new ArrayList<Terrain>();
	private List<Terrain> posXposZTerrain = new ArrayList<Terrain>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private float SIZE = 0;
	
	public TerrainArray() {
		this.SIZE = Terrain.SIZE;
	}
	
	public void add(Terrain terrain) {
		terrains.add(terrain);
		Vector3f terrainPos = terrain.getPosition();
		int gridX = (int) (terrainPos.x / SIZE);
		int gridZ = (int) (terrainPos.z / SIZE);
		if (gridX < 0 && gridZ < 0)
			negXnegZTerrain.add(terrain);
		else if (gridX >= 0 && gridZ < 0)
			posXnegZTerrain.add(terrain);
		else if (gridX >= 0 && gridZ >= 0)
			posXposZTerrain.add(terrain);
		else if (gridX < 0 && gridZ >= 0)
			negXposZTerrain.add(terrain);
	}
	
	// when i thought of this it was much better in my head
	// now it does not look at good as i thought
	public Terrain get(Vector3f position) {
		float posX = (position.x);
		float posZ = (position.z);
		Terrain terrainToReturn = null;
		if (posX < 0 && posZ < 0) {
			// negXnegZ
			terrainToReturn = checkList(negXnegZTerrain, posX, posZ);
		} else if (posX < 0 && posZ >= 0) {
			// negXposZ
			terrainToReturn = checkList(negXposZTerrain, posX, posZ);
		} else if (posX >= 0 && posZ >= 0) {
			// posXposZ
			terrainToReturn = checkList(posXposZTerrain, posX, posZ);
		} else if (posX >= 0 && posZ < 0) {
			// posXnegZ
			terrainToReturn = checkList(posXnegZTerrain, posX, posZ);
		}
		return terrainToReturn;
	}
	
	// math.ceil was not working so i added offsets
	private Terrain checkList(List<Terrain> terrains, float posX, float posZ) {
		Iterator<Terrain> i = terrains.iterator();
		Vector3f terrainPostion = new Vector3f();
		int gridX = (int) (Math.ceil(posX / SIZE));
		int gridZ = (int) (Math.ceil(posZ / SIZE));
		// its 1AM 2019-08-03. This finally works and the solution was very simple. :(
		while (i.hasNext()) {
			Terrain e = i.next();
			terrainPostion = e.getPosition();
			if ((gridX - 1) == (int) (terrainPostion.x / SIZE) && (gridZ - 1) == (int) (terrainPostion.z / SIZE)){
				return e;
			}
		}
		return null;
	}
	
	public List<Terrain> getAll() {
		return terrains;
	}
	
	public void remove(Terrain terrain) {
		Vector3f terrainPosition = terrain.getPosition();
		int posX = (int) Math.floor(terrainPosition.x / SIZE);
		int posZ = (int) Math.floor(terrainPosition.z / SIZE);
		if (posX < 0 && posZ < 0) {
			// negXnegZ
			negXnegZTerrain.remove(terrain);
		} else if (posX < 0 && posZ >= 0) {
			// negXposZ
			negXposZTerrain.remove(terrain);
		} else if (posX >= 0 && posZ >= 0) {
			// posXposZ
			posXposZTerrain.remove(terrain);
		} else if (posX >= 0 && posZ < 0) {
			// posXnegZ
			posXnegZTerrain.remove(terrain);
		}
		terrains.remove(terrain);
	}

	public List<Terrain> getNegXnegZTerrain() {
		return negXnegZTerrain;
	}

	public List<Terrain> getPosXnegZTerrain() {
		return posXnegZTerrain;
	}

	public List<Terrain> getNegXposZTerrain() {
		return negXposZTerrain;
	}

	public List<Terrain> getPosXposZTerrain() {
		return posXposZTerrain;
	}
	
}
