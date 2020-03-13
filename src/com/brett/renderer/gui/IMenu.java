package com.brett.renderer.gui;

import java.util.List;

/**
*
* @author brett
*
*/

public interface IMenu {
	
	public List<UIElement> render(UIMaster ui);
	
	public List<UIElement> secondardRender(UIMaster ui);
	
	public void update();
	
}
