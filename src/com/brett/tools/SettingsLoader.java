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
import com.brett.voxel.world.chunk.ChunkStore;

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
	public static int KEY_CLEAR = Keyboard.KEY_F6;
	
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
				if (name[0].equals("fov"))
					MasterRenderer.FOV = Float.parseFloat(name[1]);
				if (name[0].equals("fps"))
					DisplayManager.FPS_MAX = (int) Float.parseFloat(name[1]);
				if (name[0].equals("renderDistance"))
					ChunkStore.renderDistance = (int) Float.parseFloat(name[1]);
				if (name[0].equals("key_console"))
					KEY_CONSOLE = (int) Float.parseFloat(name[1]);
				if (name[0].equals("key_clear"))
					KEY_CLEAR = (int) Float.parseFloat(name[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			saveSettings();
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
			writeLine(writer, "renderDistance: " + ChunkStore.renderDistance);
			writeLine(writer, "key_console: " + KEY_CONSOLE);
			writeLine(writer, "key_clear: " + KEY_CLEAR);
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
