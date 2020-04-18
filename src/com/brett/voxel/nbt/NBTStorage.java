package com.brett.voxel.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
*
*	NBT:
*		The way of storing information about a block to disk
*	What is NBT?
*		NBT stands for named binary tag system. It uses a String(name) as a tag for data stored in a binary format to the disk.
*		NBT is not stored in a compressed way.
*
* @author brett
* @date Apr. 17, 2020
*/

public class NBTStorage {
	
	private String location;
	private DataOutputStream os = null;
	
	private HashMap<String, String> nbtData = new HashMap<String, String>();
	private HashMap<String, String> saveData = new HashMap<String, String>();
	
	/**
	 * Loads data into NBT.
	 */
	public NBTStorage(String location) {
		this.location = location;
		DataInputStream is = null;
		try {
			// data loaders
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(this.location), 4096));
			os = new DataOutputStream(new BufferedOutputStream( new FileOutputStream(this.location), 4096));
			// registed flag.
			int flag = 0;
			// loop while data is in the file
			while(is.available() > 0) {
				// expanding list
				List<Integer> tag = new ArrayList<Integer>();
				// read the tag
				while ((flag = is.readInt()) != -1) {
					tag.add(flag);
				}
				// convert the tag
				String name = deconvertNBT(tag.toArray());
				tag.clear();
				// read the data
				while ((flag = is.readInt()) != -1) {
					tag.add(flag);
				}
				// convert data
				String data = deconvertNBT(tag.toArray());
				// save data with the tag.
				nbtData.put(name, data);
			}
			is.close();
		} catch (IOException e) {}
	}
	
	/**
	 * saves all the data that we have defined to be saved.
	 */
	public void saveAll() {
		// loop through the data inside save data.
		for (Entry<String, String> s : saveData.entrySet()) {
			// convert the strings into nbt.
			int[] tag = convertNBT(s.getKey());
			int[] data = convertNBT(s.getValue());
			// save the nbt to disk.
			for (int i = 0 ; i < tag.length; i++) {
				try {os.write(tag[i]);} catch (IOException e) {}
			}
			for (int i = 0 ; i < tag.length; i++) {
				try {os.write(data[i]);} catch (IOException e) {}
			}
		}
		try {
			os.close();
		} catch (IOException e) {}
	}
	
	/*
	 * The below functions are simple String -> int[] or int[] -> String converters.
	 */
	
	/**
	 * Converts a String into an int[] with a terminator at the last position in the array.
	 */
	private int[] convertNBT(String str) {
		char[] nbt = str.toCharArray();
		int[] inbt = new int[nbt.length+1];
		for (int i = 0; i < nbt.length; i++)
			inbt[i] = nbt[i];
		inbt[nbt.length] = -1;
		return inbt;
	}
	
	/**
	 * Converts an int[] into a String. This won't work if the terminator is included.
	 */
	private String deconvertNBT(Object[] tag) {
		char[] nbt = new char[tag.length];
		for (int i = 0; i < tag.length; i++)
			nbt[i] = (char) tag[i];
		return new String(nbt);
	}
	
	/*
	 * Functions to save data-types to the disk.
	 */
	public void saveNBT(String name, byte b) {
		saveData.put(name, Byte.toString(b));
	}
	
	public void saveNBT(String name, int i) {
		saveData.put(name, Integer.toString(i));
	}
	
	public void saveNBT(String name, short s) {
		saveData.put(name, Short.toString(s));
	}
	public void saveNBT(String name, String s) {
		saveData.put(name, s);
	}
	
	/**
	 * returns NBT data
	 */
	public String loadNBT(String name) {
		if (nbtData.containsKey(name))
			return nbtData.get(name);
		return null;
	}
	
	/**
	 * DELETES THE NBT FROM DISK
	 * THERE IS NO UNDOING THIS.
	 */
	public void destory() {
		try {
			new File(location).delete();
		} catch (Exception e) {}
	}
	
}
