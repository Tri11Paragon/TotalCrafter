package com.brett.engine.ui;

import java.util.List;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class Screen {
	
	/**
	 * called when the game switches to the screen
	 */
	public void onSwitch() {
	
	}
	
	public List<UIElement> render(){
		return null;
	}
	
	public void update() {
		
	}
	
	/**
	 * Called when the game switches from this screen. this is also called when the game is closed.
	 */
	public void onLeave() {
		
	}
	
	/**
	 * called when the game is closed
	 */
	public void close() {
		
	}
	
}
