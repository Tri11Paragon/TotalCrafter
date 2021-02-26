package com.brett.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import com.brett.DisplayManager;
import com.brett.console.commands.ClearCommand;
import com.brett.renderer.Loader;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.UIDynamicText;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.tools.IKeyState;
import com.brett.tools.InputMaster;
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
	private List<UIDynamicText> texts = new ArrayList<UIDynamicText>(); 
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
	public static boolean isOpen = false;
	
	public Console(Loader loader, FontType font,GUIRenderer renderer) {
		// load up the textures
		this.grey = loader.loadTexture("grey");
		this.darkgrey = loader.loadTexture("darkgrey");
		this.lightgrey = loader.loadTexture("lightgrey");
		this.renderer = renderer;
		// add the texts that we will be using for drawing.
		texts.add(new UIDynamicText(inputTextBuffer, fontSize, font, new Vector2f(0.007f,0.9f - 0.005f), 0.45f, false, 1));
		texts.add(new UIDynamicText(textBuffer, fontSize, font, new Vector2f(0.007f, 0.020f), 0.45f, false));
		// add a clear command to clear the body.
		this.registerCommand("clear", new ClearCommand(this));
		InputMaster.keyboard.add(this);
	}
	
	public void update() {
		// this makes sure that it only runs once per button press.
		// render if we are open.
		if (isOpen) {
			// enables the shaders
			renderer.startrender();
			// renders the menu
			renderer.render(grey, 5, 10, 400, 700);
			// stops the shaders.
			renderer.stoprender();
		}
	}
	
	/**
	 * registers a command with the console
	 */
	public void registerCommand(String name, Command command) {
		this.commands.put(name, command);
	}
	
	/**
	 * registers a command with the console with multiple aliases
	 */
	public void registerCommand(String[] alias, Command command) {
		for (int i = 0; i < alias.length; i++) {
			this.commands.put(alias[i], command);
		}
	}
	
	private void enterCommand() {
		// default response is that the command isn't found
		String body = "COMMAND NOT FOUND";
		// this is the first object entered in the console (ie the command), removing the line start.
		String term = inputTextBuffer.toLowerCase().substring(1, inputTextBuffer.length()).split(" ")[0];
		// add what we typed to the body text.
		textBuffer += inputTextBuffer + '\n';
		// all the variables.
		String[] vars = {};
		try {
			// create the list of list of variables, adjusting for the first line plus the space between the command and variables.
			vars = inputTextBuffer.substring(term.length()+2, inputTextBuffer.length()).split(" ");
		} catch (Exception e) {}
		// tries to get the command and if its found then execute it.
		try {
			if (commands.get(term) != null)
				body = commands.get(term).run(inputTextBuffer.substring(1, inputTextBuffer.length()), vars);
		} catch (Exception e) {}
		// add the body to the main body text buffer
		textBuffer += body + '\n';
		// reset the line to default
		inputTextBuffer = lineStart;
		// forces text to change.
		texts.get(1).changeText(textBuffer);
	}
	
	public void clear() {
		// clears the console body.
		this.textBuffer = "";
		// forces the text to change.
		this.texts.get(1).changeText(textBuffer);
	}
	
	public static boolean getIsOpen() {
		// returns if the console is open.
		return isOpen;
	}
	
	/**
	 * sets the console's open state.
	 */
	public void setIsOpen(boolean b) {
		isOpen = b;
	}
	
	/**
	 * gets all the texts
	 */
	public List<UIDynamicText> getTexts(){
		return texts;
	}

	@Override
	public void onKeyPressed(int key) {
		// open the console if we press the console key.
		if (key == SettingsLoader.KEY_CONSOLE) {
			System.out.println("HELLO " + isOpen);
			// grab the mouse if its open
			DisplayManager.setMouseGrabbed(isOpen);
			// invert the console state
			isOpen = !isOpen;
			// enable / disable texts.
			if(isOpen)
				for(UIDynamicText t : texts)
					t.enableText();
			else
				for(UIDynamicText t : texts)
					t.disableText();
		}
	}

	@Override
	public void onKeyReleased(int keys) {
		if (isOpen) {
			char c = (char) keys;
			// make sure we don't add if we are pressing the console open key.
			if (keys == SettingsLoader.KEY_CONSOLE)
				return;
			// this is backspace. we use it to remove text.
			if (keys == GLFW.GLFW_KEY_BACKSPACE) {
				if (inputTextBuffer.length() > 1)
					inputTextBuffer = inputTextBuffer.substring(0, inputTextBuffer.length() - 1);
			// enters the command when enter is pressed.
			} else if (c == 10 || c == 13 || keys == GLFW.GLFW_KEY_ENTER) {
				// clears the body text if it is larger then the console window pane.
				if (texts.get(1).getHeight() > 7.8) {
					texts.get(1).changeText(""); 
					textBuffer = "";
					texts.get(1).setHeight(0);
				}
				// enters the comamnd
				enterCommand();
			// we don't want to write anything if we are pressing a key that isn't a character.
			} else if (c < 32) {
			// add in the character to the input line
			} else {
				char[] dhr = (c + "").replaceAll("[^a-zA-Z0-9\\- ]", "").toCharArray();
				if (dhr == null || dhr.length == 0)
					return;
				char cd = dhr[0];
				if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT] || InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT_SHIFT])
					inputTextBuffer += cd;
				else
					inputTextBuffer += Character.toLowerCase(cd);
			}
			// change the text
			texts.get(0).changeText(inputTextBuffer);
		}
	}

}
