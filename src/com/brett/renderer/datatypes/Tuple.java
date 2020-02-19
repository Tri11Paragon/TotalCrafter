/** 
*	Brett Terpstra
*	Feb 10, 2020
*	
*/ 

package com.brett.renderer.datatypes;

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
