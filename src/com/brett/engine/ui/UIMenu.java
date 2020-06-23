package com.brett.engine.ui;

import java.util.ArrayList;
import java.util.List;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class UIMenu {
	
	protected List<UIElement> elements = new ArrayList<UIElement>();
	
	public List<UIElement> render() {
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).update();
		return elements;
	}
	
}
