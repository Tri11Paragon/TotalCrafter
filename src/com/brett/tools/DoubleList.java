package com.brett.tools;

import java.util.ArrayList;
import java.util.List;

import com.brett.datatypes.Tuple;

/**
*
* @author brett
* @date Feb. 17, 2020
*/

public class DoubleList<X, Y> {
	
	private List<X> x = new ArrayList<X>();
	private List<Y> y = new ArrayList<Y>();
	
	public synchronized void clear() {
		x.clear();
		y.clear();
	}
	
	public synchronized void add(X x, Y y) {
		this.x.add(x);
		this.y.add(y);
	}
	
	public synchronized Tuple<X, Y>get(int x, int y) {
		return new Tuple<X, Y>(this.x.get(x), this.y.get(y));
	}
	
	public int getXSize() {
		return this.x.size();
	}
	
	public int getYSizee() {
		return this.y.size();
	}
	
}
