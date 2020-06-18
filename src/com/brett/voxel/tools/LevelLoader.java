package com.brett.voxel.tools;

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
import com.brett.voxel.world.VoxelWorld;
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
	
	/**
	 * loads data about the level
	 */
	public static void loadLevelData(String topLevelWorldLocation) {
		DataInputStream is = null;
		
		try {
			is = new DataInputStream(new BufferedInputStream(new FileInputStream(topLevelWorldLocation + "level.dat")));
			// load the seed
			seed = is.readLong();
			if (ply == null || VoxelWorld.isRemote) {
				is.close();
				return;
			}
			// load position
			ply.setPosition(new Vector3f(is.readFloat(), is.readFloat(), is.readFloat()));
			// load rotation
			ply.setPitch(is.readFloat());
			ply.setYaw(is.readFloat());
			is.close();
		} catch (Exception e1) {return;}
	}
	
	/**
	 * saves the level data
	 */
	public static void saveLevelData(String topLevelWorldLocation) {
		if (VoxelWorld.isRemote || !MainMenu.ingame) {
			return;
		}
		DataOutputStream os = null;
		try {
			// I don't use text for my files
			// thats stupid
			// I write raw binary to my files as it is most efficient on space.
			os = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(topLevelWorldLocation + "level.dat")));
		} catch (FileNotFoundException e1) {return;}
		
		try {
			// write all the important data about this level.
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
