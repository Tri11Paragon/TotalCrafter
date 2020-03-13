package com.brett.renderer.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.Loader;

/**
*
* @author brett
*
*/

public class EscapeMenu implements IMenu {
	
	private List<UIElement> elements = new ArrayList<UIElement>();
	public boolean enabled = false;
	
	public EscapeMenu(UIMaster master, Loader loader) {
		elements.add(master.createCenteredTexture(-1, -1, -1, 0, 0, 200, 200, new Vector3f(0,0,0)));
	}
	
	@Override
	public List<UIElement> render(UIMaster ui) {
		if (enabled)
			return elements;
		else
			return null;
	}

	@Override
	public void update() {
		
	}

	@Override
	public List<UIElement> secondardRender(UIMaster ui) {
		return null;
	}
	
}
