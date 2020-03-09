package com.brett.renderer.gui.screens;

import java.util.ArrayList;
import java.util.List;

import com.brett.renderer.Loader;
import com.brett.renderer.gui.IMenu;
import com.brett.renderer.gui.UIElement;
import com.brett.renderer.gui.UIMaster;

/**
*
* @author brett
* @date Mar. 2, 2020
*/

public class MainMenu implements IMenu {
	
	private boolean displayMainMenu = true;
	private List<UIElement> elements = new ArrayList<UIElement>();
	
	public MainMenu(UIMaster master, Loader loader) {
		
	}

	@Override
	public List<UIElement> render() {
		if (displayMainMenu)
			return elements;
		else			
			return null;
	}

	@Override
	public void update() {
		
	}

}
