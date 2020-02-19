package com.brett.renderer.datatypes;

/**
*
* @author brett
* @date Feb. 17, 2020
*/

public class Coord {
	
	public int x;
	public int y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean isYes(Coord c) {
		return (c.x == this.x) && (c.y == this.y);
	}
	
	public boolean isYes(int x, int y) {
		return (this.x == x) && (this.y == y);
	}
	
}
