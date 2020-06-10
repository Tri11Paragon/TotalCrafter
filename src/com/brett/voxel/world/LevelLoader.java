package com.brett.voxel.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import com.brett.voxel.gui.MainMenu;
import com.brett.voxel.world.player.Player;

/** 
*	Brett Terpstra
*	Mar 6, 2020
*	
*/
public class LevelLoader {

	public static long seed = 694;
	public static Player ply;
	public static Vector3f pos;
	
	public static void loadLevelData(String topLevelWorldLocation) {
		DataInputStream is = null;
		try {
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(topLevelWorldLocation + "level.dat")));
		} catch (FileNotFoundException e) {return;}
		
		try {
			seed = is.readLong();
			if (ply == null || VoxelWorld.isRemote) {
				is.close();
				return;
			}
			ply.setPosition(new Vector3f(is.readFloat(), is.readFloat(), is.readFloat()));
			ply.setPitch(is.readFloat());
			ply.setYaw(is.readFloat());
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
			if (VoxelWorld.isRemote || !MainMenu.ingame) {
				os.close();
				return;
			}
			os.writeLong(seed);
			os.writeFloat(ply.getPosition().x);
			os.writeFloat(ply.getPosition().y);
			os.writeFloat(ply.getPosition().z);
			os.writeFloat(ply.getPitch());
			os.writeFloat(ply.getYaw());
		} catch (IOException e1) {}
		
		try {
			os.close();
		} catch (IOException e) {}
	}
	
}
