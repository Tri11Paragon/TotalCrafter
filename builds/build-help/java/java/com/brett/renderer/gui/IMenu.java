package com.brett.renderer.gui;

import java.util.List;

/**
*
* @author brett
* UI menu interface.
*/

public interface IMenu {
	
	/**
	 * called to render the base textures
	 */
	public List<UIElement> render(UIMaster ui);
	
	/**
	 * called to render any overlay textures
	 */
	public List<UIElement> secondardRender(UIMaster ui);
	
	/**
	 * some implementations may not be thread friendly of this function.
	 * if you need to draw stuff then please use one of the renderer methods
	 */
	public void update();
	
}
