package com.brett.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.console.commands.ClearCommand;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.GUITexture;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.GUIDynamicText;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIElement;
import com.brett.tools.SettingsLoader;

/**
*
* @author brett
* This is a big mess
* please don't judge
*
*/

public class Console {

	public static String lineStart = ">";
	public static float fontSize = 0.8f;
	
	private List<UIElement> textures = new ArrayList<UIElement>();
	private List<GUIDynamicText> texts = new ArrayList<GUIDynamicText>(); 
	private HashMap<String, Command> commands = new HashMap<String, Command>();
	
	private int grey;
	private int darkgrey;
	private int lightgrey;
	private GUIRenderer renderer;
	private String textBuffer = "";
	private String inputTextBuffer = lineStart + "";
	
	private boolean isOpen = false;
	
	public Console(Loader loader, FontType font,GUIRenderer renderer) {
		this.grey = loader.loadTexture("grey");
		this.darkgrey = loader.loadTexture("darkgrey");
		this.lightgrey = loader.loadTexture("lightgrey");
		this.renderer = renderer;
		this.textures.add(new GUITexture(grey, new Vector2f(-1f + 0.01f, -0.8f - 0.035f), new Vector2f(0.9f, 1.80f)));
		this.textures.add(new GUITexture(darkgrey, new Vector2f(-1f + 0.01f, 1f - 0.035f), new Vector2f(0.9f, 0.02f)));
		this.textures.add(new GUITexture(lightgrey, new Vector2f(-1f + 0.01f, -0.8f - 0.035f), new Vector2f(0.9f, 0.05f)));
		texts.add(new GUIDynamicText(inputTextBuffer, fontSize, font, new Vector2f(0.007f,0.9f - 0.005f), 0.45f, false, 1));
		texts.add(new GUIDynamicText(textBuffer, fontSize, font, new Vector2f(0.007f, 0.020f), 0.45f, false));
		this.registerCommand("clear", new ClearCommand(this));
	}
	
	public void update() {
		if (Keyboard.isKeyDown(SettingsLoader.KEY_CONSOLE) && Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				Mouse.setGrabbed(isOpen);
				isOpen = !isOpen;
				if(isOpen)
					for(GUIDynamicText t : texts)
						t.enableText();
				else
					for(GUIDynamicText t : texts)
						t.disableText();
			}
		}
		
		while (Keyboard.next()) {
			if (isOpen) {
				if(Keyboard.getEventKeyState()) {
					char c = Keyboard.getEventCharacter();
					if (c == 8) {
						if (inputTextBuffer.length() > 1)
							inputTextBuffer = inputTextBuffer.substring(0, inputTextBuffer.length() - 1);
					} else if (c == 10 || c == 13) {
						if (texts.get(1).getHeight() < 7.8)
							enterCommand();
						else {
							texts.get(1).changeText(""); 
							textBuffer = "";
							texts.get(1).setHeight(0);
						}
					} else if (c < 32) {
							
					} else
						inputTextBuffer += c;
					texts.get(0).changeText(inputTextBuffer);
				}
			}
		}
		if (isOpen)
			renderer.render(textures);
	}
	
	public void registerCommand(String name, Command command) {
		this.commands.put(name, command);
	}
	
	private void enterCommand() {
		String body = "COMMAND NOT FOUND";
		String term = inputTextBuffer.toLowerCase().substring(1, inputTextBuffer.length()).split(" ")[0];
		textBuffer += inputTextBuffer + '\n';
		if (commands.get(term) != null)
			body = commands.get(term).run(inputTextBuffer.substring(1, inputTextBuffer.length()));
		textBuffer += body + '\n';
		inputTextBuffer = lineStart;
		// don't even.
		texts.get(1).changeText(textBuffer);
	}
	
	public void clear() {
		this.textBuffer = "";
		this.texts.get(1).changeText(textBuffer);
	}
	
	public boolean getIsOpen() {
		return isOpen;
	}
	
	public void setIsOpen(boolean b) {
		this.isOpen = b;
	}
	
	public List<UIElement> getTextures(){
		return textures;
	}
	
	public List<GUIDynamicText> getTexts(){
		return texts;
	}
	
}
