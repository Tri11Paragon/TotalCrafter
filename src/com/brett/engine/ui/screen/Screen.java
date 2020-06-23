package com.brett.engine.ui.screen;

import java.util.ArrayList;
import java.util.List;

import com.brett.engine.ui.UIElement;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class Screen {
	
	protected List<UIElement> elements = new ArrayList<UIElement>();
	
	/**
	 * called when the game switches to the screen
	 */
	public void onSwitch() {
	
	}
	
	public List<UIElement> render(){
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).update();
		return elements;
	}
	
	public void update() {
		
	}
	
	/**
	 * Called when the game switches from this screen. this is also called when the game is closed.
	 */
	public void onLeave() {
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).destroy();
		elements.clear();
	}
	
	/**
	 * called when the game is closed
	 */
	public void close() {
		
	}
	
}
