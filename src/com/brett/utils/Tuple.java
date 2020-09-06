package com.brett.utils;

/**
* @author Brett
* @date 5-Sep-2020
*/

public class Tuple<X, Y> {
	
	private X x;
	private Y y;
	
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Tuple<X, Y> setX(X x) {
		this.x = x;
		return this;
	}

	public Y getY() {
		return y;
	}

	public Tuple<X, Y> setY(Y y) {
		this.y = y;
		return this;
	}
	
}
