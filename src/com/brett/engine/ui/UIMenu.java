package com.brett.engine.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class UIMenu {
	
	protected List<UIElement> elements = new ArrayList<UIElement>();
	protected static Map<String, List<UIText>> textMap = new HashMap<String, List<UIText>>();
	private boolean enabled = false;
	
	public List<UIElement> render() {
		if (enabled) {
			for (int i = 0; i < elements.size(); i++)
				elements.get(i).update();
			ScreenManager.fontrenderer.render(textMap);
			return elements;
		} else
			return null;
	}
	
	public void enable() {
		if (enabled)
			return;
		enabled = true;
	}
	
	public void disable() {
		if (!enabled)
			return;
		enabled = false;
	}
	
	public void destroy() {
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).destroy();
		textMap.clear();
		elements.clear();
	}
	
	public void toggle() {
		if (enabled)
			disable();
		else
			enable();
	}
	
	public void addText(UIText text) {
		if (!textMap.containsKey(text.getFont()))
			textMap.put(text.getFont(), new ArrayList<UIText>());
		textMap.get(text.getFont()).add(text);
	}
	
}
