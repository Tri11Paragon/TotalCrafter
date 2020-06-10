package com.brett.datatypes;

/** 
*	Brett Terpstra
*	Feb 10, 2020
*	Holds two types of data as a single data
*/ 
public class Tuple<X,Y> {
	
	private X x;
	private Y y;
	
	public Tuple(X x, Y y){
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

	public void setX(X x) {
		this.x = x;
	}

	public void setY(Y y) {
		this.y = y;
	}
	
}
