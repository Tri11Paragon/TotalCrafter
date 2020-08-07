package com.brett.engine.ui.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class Screen {
	
	public List<UIElement> elements = new ArrayList<UIElement>();
	public List<UIMenu> menus = new ArrayList<UIMenu>();
	public Map<String, List<UIText>> textMap = new HashMap<String, List<UIText>>();
	
	/**
	 * called when the game switches to the screen
	 */
	public void onSwitch() {
	
	}
	
	public List<UIElement> render(){
		for (int i = 0; i < menus.size(); i++)
			ScreenManager.uiRenderer.render(menus.get(i).render());
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).update();
		return elements;
	}
	
	public Map<String, List<UIText>> renderText(){
		for (int i = 0; i < menus.size(); i++)
			ScreenManager.fontrenderer.render(menus.get(i).renderText());
		return textMap;
	}
	
	public void update() {
		
	}
	
	/**
	 * Called when the game switches from this screen. this is also called when the game is closed.
	 */
	public void onLeave() {
		for (int i = 0; i < menus.size(); i++)
			menus.get(i).destroy();
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).destroy();
		for (Entry<String, List<UIText>> texts : textMap.entrySet())
			for (int i = 0; i < texts.getValue().size(); i++)
				texts.getValue().get(i).destroy();
		menus.clear();
		elements.clear();
		textMap.clear();
	}
	
	/**
	 * called when the game is closed
	 */
	public void close() {
		
	}
	
	public void addText(UIText text) {
		if (!textMap.containsKey(text.getFont()))
			textMap.put(text.getFont(), new ArrayList<UIText>());
		textMap.get(text.getFont()).add(text);
	}
	
}
