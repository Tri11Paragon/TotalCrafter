package com.brett.engine.ui.console;

import java.util.Arrays;
import java.util.HashMap;
import org.lwjgl.glfw.GLFW;

import com.brett.engine.Utils;
import com.brett.engine.data.IKeyState;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jul. 5, 2020
*/

public class Console {
	
	private static UIMenu menu;
	private static String textBuffer = "";
	public static int maxCharacters = 32;
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	private static long startTime = 0;
	private static long lastKeyTime = 0;
	private static UIText commandtext;
	
	public static UIMenu init() {
		menu = new UIMenu();
		
		commandtext = new UIText("", 250, "mono", 300, 300, 3000, 1);
		menu.addText(commandtext);
		
		InputMaster.keyboard.add(new IKeyState() {
			
			@Override
			public void onKeyReleased(int keys) {
				if (keys == GLFW.GLFW_KEY_GRAVE_ACCENT)
					menu.toggle();
				if (menu.isEnabled()) {
					startTime = System.currentTimeMillis();
					if (keys == GLFW.GLFW_KEY_BACKSPACE) {
						startTime = System.currentTimeMillis();
						if (textBuffer.length() < 2)
							return;
						if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
							textBuffer = textBuffer.substring(0, textBuffer.length()-1);
						textBuffer = textBuffer.substring(0, textBuffer.length()-1);
						commandtext.changeText(textBuffer);
						return;
					}
					if (textBuffer.length() >= maxCharacters)
						return;
					if (keys == GLFW.GLFW_KEY_ENTER) {
						String[] args = textBuffer.split(" ");
						if (args.length < 1)
							return;
						String command = args[0];
						if (args.length > 1)
							args = Arrays.copyOfRange(args, 1, args.length);
						else
							args = new String[0];
						if (commands.containsKey(command))
							commands.get(command).commandEntered(textBuffer, args);
						textBuffer = "_";
						commandtext.changeText(textBuffer);
						return;
					}
					if (keys < 30 || keys > 100 || keys == GLFW.GLFW_KEY_GRAVE_ACCENT)
						return;
					for (int i = 0; i < Utils.illegalCharacters.size(); i++){
						if (keys == Utils.illegalCharacters.get(i))
							return;
					}
					if (textBuffer.length()-1 > -1)
						if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
							textBuffer = textBuffer.substring(0, textBuffer.length()-1);
					if (keys >= 65 && keys <= 90) {
						if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT] || InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT_SHIFT]) 
							textBuffer+=(char)keys;
						else
							textBuffer+=(char)(keys+32);
					} else 
						textBuffer+=(char)keys;
					
					//textBuffer = "_";
					//commandtext.changeText(textBuffer);
				}
			}
			
			@Override
			public void onKeyPressed(int keys) {
				
			}
		});
		
		return menu;
	}
	
	public static void registerCommand(String sand, Command command) {
		commands.put(sand, command);
	}
	
	public static void registerCommand(String[] sands, Command command) {
		for (int i = 0; i < sands.length; i++)
			commands.put(sands[i], command);
	}
	
	public static String getTextNoSel() {
		if (textBuffer.length() == 0)
			return textBuffer;
		if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
			return textBuffer.substring(0, textBuffer.length()-1);
		return textBuffer;
	}

	
	public static void update() {
		char[] texs = textBuffer.toCharArray();
		if (menu.isEnabled()) {
			if (texs.length == 0) {
				textBuffer = "_";
				commandtext.changeText(textBuffer);
				return;
			}
			if (texs[texs.length-1] != '_') {
				textBuffer += '_';
				commandtext.changeText(textBuffer);
			}
			if (InputMaster.keyDown[GLFW.GLFW_KEY_BACKSPACE]) {
				if (System.currentTimeMillis() - startTime > 800) {
					if (System.currentTimeMillis() - lastKeyTime > 40) {
						if (textBuffer.length() < 2)
							return;
						textBuffer = textBuffer.substring(0, textBuffer.length()-2);
						lastKeyTime = System.currentTimeMillis();
						commandtext.changeText(textBuffer);
					}
				}
			}
		} else {
			if (texs.length == 0)
				return;
			if (texs[texs.length-1] == '_') {
				textBuffer = textBuffer.substring(0, texs.length-1);
				commandtext.changeText(textBuffer);
			}
		}
	}
	
}
