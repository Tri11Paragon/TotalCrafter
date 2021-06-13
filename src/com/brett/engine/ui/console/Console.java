package com.brett.engine.ui.console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import org.lwjgl.glfw.GLFW;

import com.brett.engine.Utils;
import com.brett.engine.data.IKeyState;
import com.brett.engine.data.IScrollState;
import com.brett.engine.managers.InputMaster;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.UIMenu;
import com.brett.engine.ui.UITexture;
import com.brett.engine.ui.font.UIText;

/**
* @author Brett
* @date Jul. 5, 2020
*/

public class Console {
	
	private static UIMenu menu;
	private static String textBuffer = "";
	private static String bodyBuffer = "";
	private static double bodyHeight = 0;
	public static int maxCharacters = 48;
	public static HashMap<String, Command> commands = new HashMap<String, Command>();
	public static KeyMap keysMap;
	private static long startTime = 0;
	private static long lastKeyTime = 0;
	private static UIText commandtext;
	private static UIText pastCommands;
	private static PrintWriter his;
	//private static int SCALE = DisplayManager.HEIGHT;
	
	public static UIMenu init() {
		menu = new UIMenu();
		keysMap = new KeyMap();
		try {
			his = new PrintWriter( new BufferedWriter(new FileWriter("history.txt", true)));
		} catch (Exception e) {}
		
		commandtext = new UIText("", 175, "mono", 10, 600, 3000, 1);
		pastCommands = new UIText("", 175, "mono", 10, 10, 600, 100);
		pastCommands.setBoundingBox(10, 10, 455, 595);
		menu.addText(commandtext);
		menu.addText(pastCommands);
		menu.addElement(new UITexture(ScreenManager.loader.loadTexture("gui/darkgrey"), -1, -1, 5, 5, 445, 620));
		menu.addElement(new UITexture(ScreenManager.loader.loadTexture("gui/lightgrey"), -1, -1, 5, 10, 445, 84));
		
		InputMaster.scroll.add(new IScrollState() {
			@Override
			public void scroll(int dir) {
				double d = dir * 5;
				Console.pastCommands.ry += d;
			}
		});
		
		InputMaster.keyboard.add(new IKeyState() {
			
			@Override
			public void onKeyReleased(int keys) {
				if (keys == GLFW.GLFW_KEY_GRAVE_ACCENT)
					menu.toggle();
				if (menu.isEnabled()) {
					startTime = 0;
					if (keys == GLFW.GLFW_KEY_BACKSPACE) {
						if (textBuffer.length() < 2)
							return;
						if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
							textBuffer = textBuffer.substring(0, textBuffer.length()-1);
						textBuffer = textBuffer.substring(0, textBuffer.length()-1);
						commandtext.changeText(textBuffer);
						return;
					}
					if (keys == GLFW.GLFW_KEY_ENTER) {
						if (bodyHeight + (pastCommands.ry+38) > 588) {
							pastCommands.ry -= 42;
						}
						if (textBuffer.length() < 1)
							return;
						if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
							textBuffer = textBuffer.substring(0, textBuffer.length()-1);
						String[] args = textBuffer.split(" ");
						if (args.length < 1)
							return;
						String command = args[0];
						if (command.length() < 1)
							return;
						if (command.toCharArray()[command.length()-1] == '_')
							command = command.substring(0, command.length()-1);
						String tmp = ">" + textBuffer + "\n";
						String time = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss z] ").format(new Date(System.currentTimeMillis()));
						his.print(time + tmp);
						bodyBuffer += tmp;
						if (args.length > 1)
							args = Arrays.copyOfRange(args, 1, args.length);
						else
							args = new String[0];
						String responce = "";
						if (commands.containsKey(command)) {
							responce = commands.get(command).commandEntered(textBuffer, args) + "\n";
							his.print(time + responce);
						} else {
							bodyBuffer += "COMMAND NOT FOUND\n";
							his.println(time + "COMMAND NOT FOUND");
						}
						his.flush();
						bodyBuffer += responce + ""; 
						textBuffer = "_";
						commandtext.changeText(textBuffer);
						pastCommands.changeText(bodyBuffer);
						return;
					}
					// TODO: Character Scroll
					if (textBuffer.length() >= maxCharacters)
						return;
					if (keys < 30 || keys > 100 || keys == GLFW.GLFW_KEY_GRAVE_ACCENT)
						return;
					for (int i = 0; i < Utils.illegalCharacters.size(); i++){
						if (keys == Utils.illegalCharacters.get(i))
							return;
					}
					if (textBuffer.length()-1 > -1)
						if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
							textBuffer = textBuffer.substring(0, textBuffer.length()-1);
					//if (keys >= 65 && keys <= 90) {
					if (InputMaster.keyDown[GLFW.GLFW_KEY_LEFT_SHIFT] || InputMaster.keyDown[GLFW.GLFW_KEY_RIGHT_SHIFT]) 
						textBuffer+=Character.toUpperCase((char)(keys));
					else
						textBuffer+=Character.toLowerCase((char)(keys));
					//} else 
					//	textBuffer+=(char)keys;
					//textBuffer = "_";
					//commandtext.changeText(textBuffer);
				}
			}
			
			@Override
			public void onKeyPressed(int keys) {
				if (keys == GLFW.GLFW_KEY_BACKSPACE) {
					startTime = System.currentTimeMillis();
				}
			}
		});
		
		return menu;
	}
	
	public static void registerCommand(String sand, Command command) {
		commands.put(sand, command);
	}
	
	public static void registerCommand(String[] sands, Command command) {
		for (int i = 0; i < sands.length; i++) {
			if (commands.containsKey(sands[i]))
				commands.remove(sands[i]);
			commands.put(sands[i], command);
		}
	}
	
	public static String getTextNoSel() {
		if (textBuffer.length() == 0)
			return textBuffer;
		if (textBuffer.toCharArray()[textBuffer.length()-1] == '_')
			return textBuffer.substring(0, textBuffer.length()-1);
		return textBuffer;
	}

	public static void close() {
		his.close();
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
			int tmp = ((pastCommands.getNumberOfLines()-1)/2);
			bodyHeight = tmp*39 + (3 * tmp);
			if (InputMaster.keyDown[GLFW.GLFW_KEY_BACKSPACE]) {
				if (startTime != 0) {
					long time = System.currentTimeMillis() - startTime;
					if (time > 800) {
						if (System.currentTimeMillis() - lastKeyTime > 40) {
							if (textBuffer.length() < 2)
								return;
							textBuffer = textBuffer.substring(0, textBuffer.length()-2);
							lastKeyTime = System.currentTimeMillis();
							commandtext.changeText(textBuffer);
						}
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
