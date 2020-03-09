package com.brett.voxel.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/** 
*	Brett Terpstra
*	Mar 6, 2020
*	
*/
public class LevelLoader {

	public static long seed = 694;
	
	public static void loadLevelData(String topLevelWorldLocation) {
		DataInputStream is = null;
		try {
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(topLevelWorldLocation + "level.dat")));
		} catch (FileNotFoundException e) {return;}
		
		try {
			seed = is.readLong();
		} catch (IOException e1) {}
		
		try {
			is.close();
		} catch (IOException e) {}
	}
	
	public static void saveLevelData(String topLevelWorldLocation) {
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(topLevelWorldLocation + "level.dat")));
		} catch (FileNotFoundException e1) {return;}
		
		try {
			os.writeLong(seed);
		} catch (IOException e1) {}
		
		try {
			os.close();
		} catch (IOException e) {}
	}
	
}
