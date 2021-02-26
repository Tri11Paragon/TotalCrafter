package com.brett.datatypes;

/** 
*	Brett Terpstra
*	Feb 10, 2020
*	UNUSED CLASS PLEASE IGNORE
*   I'm only keeping this because its funny.
*   this entire class could be replaced with a single byte.
*   (when I was using it and what I was using it for)
*/ 

public class SixBoolean {
	
	private boolean top;
	private boolean bottom;
	private boolean left;
	private boolean right;
	private boolean front;
	private boolean back;
	
	public SixBoolean(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back) {
		this.top = top;
		this.bottom = bottom;
		this.back = back;
		this.left = left;
		this.right = right;
		this.front = front;
	}

	public boolean isYes(boolean top, boolean bottom, boolean left, boolean right, boolean front, boolean back) {
		return (this.top == top) && (this.bottom == bottom) && (this.left == left) && (this.right == right) && (this.front == front) && (this.back == back);
	}
	
	public boolean isYes(SixBoolean b) {
		return (this.top == b.top) && (this.bottom == b.bottom) && (this.left == b.left) && (this.right == b.right) && (this.front == b.front) && (this.back == b.back);
	}
	
	public boolean isTop() {
		return top;
	}

	public boolean isBottom() {
		return bottom;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return right;
	}

	public boolean isFront() {
		return front;
	}

	public boolean isBack() {
		return back;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setFront(boolean front) {
		this.front = front;
	}

	public void setBack(boolean back) {
		this.back = back;
	}
	
}
