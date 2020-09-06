package com.brett.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

/**
* @author Brett
* @date 5-Sep-2020
*/

public class ServerProperties {
	
	private static HashMap<String, String> properties = new HashMap<String, String>();
	
	public static void load() {
		File f = new File("server.properties");
		if (f.exists()) {
			try {
				BufferedReader read = new BufferedReader(new FileReader(f));
				
				String line = "";
				while ((line = read.readLine()) != null) {
					String[] datas = line.split("=");
					if (datas == null)
						continue;
					if (datas.length < 2)
						continue;
					properties.put(datas[0].replaceAll("\\s",""), datas[1].replaceAll("\\s",""));
				}
				
				read.close();
			} catch (Exception e) {}
		} else {
			try {
				BufferedWriter write = new BufferedWriter(new FileWriter(f));
				write.write("port=1337\n");
				write.write("render_distance=10");
				write.close();
			} catch (Exception e) {}
		}
	}
	
	public static String getProperty(String name) {
		if (properties.containsKey(name))
			return properties.get(name);
		else
			return "";
	}
	
	public static int getPropertyInt(String name) {
		if (properties.containsKey(name)) {
			try {
				return Integer.parseInt(properties.get(name));
			} catch (Exception e) {
				return 0;
			}
		} else
			return 0;
	}
	
	public static int getPropertyInt(String name, int d) {
		if (properties.containsKey(name)) {
			try {
				return Integer.parseInt(properties.get(name));
			} catch (Exception e) {
				return d;
			}
		} else
			return d;
	}
	
	public static float getPropertyFloat(String name) {
		if (properties.containsKey(name)) {
			try {
				return Float.parseFloat(properties.get(name));
			} catch (Exception e) {
				return 0;
			}
		} else
			return 0;
	}
	
	public static float getPropertyFloat(String name, float d) {
		if (properties.containsKey(name)) {
			try {
				return Float.parseFloat(properties.get(name));
			} catch (Exception e) {
				return d;
			}
		} else
			return d;
	}
	
	public static double getPropertyDouble(String name) {
		if (properties.containsKey(name)) {
			try {
				return Double.parseDouble(properties.get(name));
			} catch (Exception e) {
				return 0;
			}
		} else
			return 0;
	}
	
	public static double getPropertyDouble(String name, double d) {
		if (properties.containsKey(name)) {
			try {
				return Double.parseDouble(properties.get(name));
			} catch (Exception e) {
				return d;
			}
		} else
			return d;
	}
	
}
