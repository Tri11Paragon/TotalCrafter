package com.brett.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import com.brett.IKeyState;
import com.brett.KeyMaster;
import com.brett.console.commands.ClearCommand;
import com.brett.renderer.Loader;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.GUIDynamicText;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.tools.SettingsLoader;

/**
*
* @author brett
* This is a big mess
* please don't judge
* 
* The developer console class (this class) is used for testing, however
* users are more then welcome to use it but should be warned it can crash your game
* <b>WITHOUT</b> saving it.
* 
* Console commands are easy to integrate and allow you to do things like
* teleport the player or give items to them. It may also serve in the future to
* allow users to chat to each other when connected to multiplayer.
* 
*/
@SuppressWarnings("unused")
public class Console implements IKeyState {

	// what does the line start with?
	public static String lineStart = ">";
	// size of the console font.
	public static float fontSize = 0.8f;
	
	// list of the texts that are used to display the console.
	private List<GUIDynamicText> texts = new ArrayList<GUIDynamicText>(); 
	// map of the (object) commands and the (String) commands that call them.
	private HashMap<String, Command> commands = new HashMap<String, Command>();
	
	// grey texture
	private int grey;
	// dark grey texture
	private int darkgrey;
	// light grey texture
	private int lightgrey;
	// reference to the GUI renderer.
	private GUIRenderer renderer;
	// body text buffer
	private String textBuffer = "";
	// input text
	private String inputTextBuffer = lineStart + "";
	
	// is the console open or not
	private static boolean isOpen = false;
	
	public Console(Loader loader, FontType font,GUIRenderer renderer) {
		// load up the textures
		this.grey = loader.loadTexture("grey");
		this.darkgrey = loader.loadTexture("darkgrey");
		this.lightgrey = loader.loadTexture("lightgrey");
		this.renderer = renderer;
		texts.add(new GUIDynamicText(inputTextBuffer, fontSize, font, new Vector2f(0.007f,0.9f - 0.005f), 0.45f, false, 1));
		texts.add(new GUIDynamicText(textBuffer, fontSize, font, new Vector2f(0.007f, 0.020f), 0.45f, false));
		this.registerCommand("clear", new ClearCommand(this));
	}
	
	public void update() {
		if (KeyMaster.state) {
			if (isOpen) {
				if(Keyboard.getEventKeyState()) {
					char c = Keyboard.getEventCharacter();
					if (c == 96)
						return;
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
						if (c != SettingsLoader.KEY_CONSOLE)
							inputTextBuffer += c;
					texts.get(0).changeText(inputTextBuffer);
				}
			}
		}
		if (isOpen) {
			renderer.startrender();
			renderer.render(grey, 5, 10, 400, 700);
			renderer.stoprender();
		}
	}
	
	public void registerCommand(String name, Command command) {
		this.commands.put(name, command);
	}
	
	public void registerCommand(String[] alias, Command command) {
		for (int i = 0; i < alias.length; i++) {
			this.commands.put(alias[i], command);
		}
	}
	
	private void enterCommand() {
		String body = "COMMAND NOT FOUND";
		String term = inputTextBuffer.toLowerCase().substring(1, inputTextBuffer.length()).split(" ")[0];
		textBuffer += inputTextBuffer + '\n';
		String[] vars = {};
		try {
			vars = inputTextBuffer.substring(term.length()+2, inputTextBuffer.length()).split(" ");
		} catch (Exception e) {}
		if (commands.get(term) != null)
			body = commands.get(term).run(inputTextBuffer.substring(1, inputTextBuffer.length()), vars);
		textBuffer += body + '\n';
		inputTextBuffer = lineStart;
		// don't even.
		texts.get(1).changeText(textBuffer);
	}
	
	public void clear() {
		this.textBuffer = "";
		this.texts.get(1).changeText(textBuffer);
	}
	
	public static boolean getIsOpen() {
		return isOpen;
	}
	
	public void setIsOpen(boolean b) {
		isOpen = b;
	}
	
	public List<GUIDynamicText> getTexts(){
		return texts;
	}

	@Override
	public void onKeyPressed() {
		if (Keyboard.isKeyDown(SettingsLoader.KEY_CONSOLE)) {
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

	@Override
	public void onKeyReleased() {
	}
	
}
