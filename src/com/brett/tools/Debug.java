package com.brett.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
*
* @author brett
* @date Jun. 2, 2020
*/

public class Debug {
	
	private static boolean first = true;
	private static FileWriter fr = null;
	
	public static void print(String line) {
		if (first) {
			try {
				fr = new FileWriter(new File("logs.txt"));
				first = false;
			} catch (IOException e) {e.printStackTrace();}
		}
		try {
			fr.write(line);
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(line);
	}
	
	public static void print(int line) {
		if (first) {
			try {
				fr = new FileWriter(new File("logs.txt"));
				first = false;
			} catch (IOException e) {e.printStackTrace();}
		}
		try {
			fr.write(line);
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(line);
	}
	
	public static void print(short line) {
		if (first) {
			try {
				fr = new FileWriter(new File("logs.txt"));
				first = false;
			} catch (IOException e) {e.printStackTrace();}
		}
		try {
			fr.write(line);
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(line);
	}
	
	public static void print(byte line) {
		if (first) {
			try {
				fr = new FileWriter(new File("logs.txt"));
				first = false;
			} catch (IOException e) {e.printStackTrace();}
		}
		try {
			fr.write(line);
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(line);
	}
	
	public static void print(boolean line) {
		if (first) {
			try {
				fr = new FileWriter(new File("logs.txt"));
				first = false;
			} catch (IOException e) {e.printStackTrace();}
		}
		try {
			fr.write(line + "");
			fr.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(line);
	}
	
}
