package com.brett.engine.ui.screen;

import java.util.ArrayList;
import java.util.List;

import com.brett.engine.ui.UIElement;

/**
* @author Brett
* @date Jun. 21, 2020
*/

public class MainMenu extends Screen {
	
	private List<UIElement> elements = new ArrayList<UIElement>();
	
	public MainMenu() {
		
	}
	
	@Override
	public List<UIElement> render() {
		return elements;
	}
	
}
