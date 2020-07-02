package com.brett.engine.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
* @author Brett
* @date Jun. 21, 2020
*/

public class Settings {
	
	private static final String SETTINGS_LOCATION = "settings.txt";
	private static final HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	/*
	 * some static variable definitions for classes that need them.
	 */
	public static int SAMPLES = 4;
	public static int AF = 4;
	public static double SENSITIVITY = 0.5d;
	public static double MUSIC = 0.5d;
	public static int FPS = 0;
	public static int VSYNC = 0;
	public static int RENDER_DISTANCE = 12;
	
	private static int readLines = 1;
	public static void load() {
		try {
			// load the settings file
			new File(SETTINGS_LOCATION).createNewFile();
			BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_LOCATION));
			String line;
			while((line = reader.readLine()) != null) {
				// don't process lines that are commented.
				// Python, java and Lua comment prefixes.
				if (line.startsWith("#") || line.startsWith("//") || line.startsWith("--")) {
					// put the comments in memory so that way when we save settings
					// it keeps the comments
					comments.put(readLines, line);
					// a marker to what line pos we put the comment at.
					readLines++;
					continue;
				}
				String[] name = line.split(":");
				// make sure we have a good comparison
				name[0] = name[0].toLowerCase();
				if (name[0].equals("sensitivity"))
					SENSITIVITY = Double.parseDouble(name[1]);
				if (name[0].equals("samples"))
					SAMPLES = (int) Float.parseFloat(name[1]);
				if (name[0].equals("anisotropy"))
					AF = (int) Float.parseFloat(name[1]);
				if (name[0].equals("music"))
					MUSIC = Float.parseFloat(name[1]);
				if (name[0].equals("fps"))
					FPS = (int)Float.parseFloat(name[1]);
				if (name[0].equals("vsync"))
					VSYNC = (int)Float.parseFloat(name[1]);
				if (name[0].equals("rd"))
					RENDER_DISTANCE = (int)Float.parseFloat(name[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void save() {
		try {
			// reads file
			BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_LOCATION));
			// clears the file
			writer.write("");
			// write all the important data that needs saving
			writeLine(writer, "sensitivity: " + SENSITIVITY);
			writeLine(writer, "samples: " + SAMPLES);
			writeLine(writer, "anisotropy: " + AF);
			writeLine(writer, "music: " + MUSIC);
			writeLine(writer, "fps: " + FPS);
			writeLine(writer, "vsync: " + VSYNC);
			writeLine(writer, "rd: " + RENDER_DISTANCE);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int writeLines = 1;
	private static void writeLine(BufferedWriter writer, String s) throws IOException {
		// get the comment for this current line
		String comment = comments.get(writeLines);
		if (comment != null) {
			// write the comment
			writer.append(comment);
			writer.newLine();
			// Increase the line we are writing
			writeLines++;
			// try to write the line again
			writeLine(writer, s);
			return;
		}
		// write this line
		writer.append(s);
		writer.newLine();
		writeLines++;
	}
	
}
