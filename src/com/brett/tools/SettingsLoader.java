package com.brett.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import com.brett.DisplayManager;
import com.brett.renderer.MasterRenderer;

/**
*
* @author brett
*
* saves and loads settings from the settings file
*
*/

public class SettingsLoader {

	private static final String SETTINGS_LOCATION = "settings.txt";
	private static final HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	public static int KEY_CONSOLE = Keyboard.KEY_GRAVE;
	
	private static int readLines = 1;
	public static void loadSettings() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_LOCATION));
			String line;
			while((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					comments.put(readLines, line);
					readLines++;
					continue;
				}
				String[] name = line.split(":");
				name[0] = name[0].toLowerCase();
				if (name[0].equals("fov")) {
					MasterRenderer.FOV = Float.parseFloat(name[1]);
				}
				if (name[0].equals("fps")) {
					DisplayManager.FPS_MAX = (int) Float.parseFloat(name[1]);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void saveSettings() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_LOCATION));
			// clears the file
			writer.write("");
			writeLine(writer, "FOV: " + MasterRenderer.FOV);
			writeLine(writer, "FPS: " + DisplayManager.FPS_MAX);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int writeLines = 1;
	private static void writeLine(BufferedWriter writer, String s) throws IOException {
		String comment = comments.get(writeLines);
		if (comment != null) {
			writer.append(comment);
			writer.newLine();
			writeLines++;
			writeLine(writer, s);
			return;
		}
		writer.append(s);
		writer.newLine();
		writeLines++;
	}
	
}
