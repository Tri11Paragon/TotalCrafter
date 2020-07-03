package com.brett.world.chunks.data;

/**
* @author Brett
* @date Jul. 2, 2020
*/

public class Counter {
	
	private int count = 0;
	
	public void increment() {
		count++;
	}
	
	public int count() {
		return count; 
	}
	
	public String toString() {
		return Integer.toString(count);
	}
	
}
