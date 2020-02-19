package com.brett.renderer.datatypes;

public class WaterTile {

	public static float TILE_SIZE = 60;
    
    private float height;
    private float x,z;
     
    public WaterTile(float centerX, float height, float centerZ){
        this.x = centerX;
        this.z = centerZ;
        this.height = height;
    }
    
    public WaterTile(float centerX, float height, float centerZ, float size){
        this.x = centerX;
        this.z = centerZ;
        this.height = height;
        WaterTile.TILE_SIZE = size;
    }
 
    public float getHeight() {
        return height;
    }
 
    public float getX() {
        return x;
    }
 
    public float getZ() {
        return z;
    }
 
	
}
