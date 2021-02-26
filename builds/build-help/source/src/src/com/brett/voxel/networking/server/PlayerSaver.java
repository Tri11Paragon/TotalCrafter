package com.brett.voxel.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.brett.datatypes.Tuple;

/**
*
* @author brett
* @date Jun. 2, 2020
* Client sync data is a simple float[] defined as {x, y, z, pitch, yaw, roll}
*/

public class PlayerSaver {
	
	public static List<Tuple<String, float[]>> players = new ArrayList<Tuple<String,float[]>>();
	
	private static boolean first = true;
	/**
	 * loads player based on user name
	 */
	public static float[] loadPlayer(String username) {
		// return default array if there is no file
		if (!new File(ServerWorld.worldLocation + "/players/players.dat").exists())
			return new float[] {0, 130, 0, 0, 0, 0};
		// load all the player data if this is the first time we are loading a player
		if (first) {
			try {
				// read all the lines in the file
				BufferedReader wr = new BufferedReader(new FileReader(ServerWorld.worldLocation + "/players/players.dat"), 262144);
				String line = "";
				while ((line = wr.readLine()) != null) {
					String[] data = line.split(";");
					float[] pos = new float[6];
					// decode position data from this line.
					try {
						pos[0] = Float.parseFloat(data[1]);
						pos[1] = Float.parseFloat(data[2]);
						pos[2] = Float.parseFloat(data[3]);
						if (data.length > 4) {
							pos[3] = Float.parseFloat(data[4]);
							pos[4] = Float.parseFloat(data[5]);
							pos[5] = Float.parseFloat(data[6]);
						}
					} catch (Exception e) {}
					// add the loaded player to the list
					players.add(new Tuple<String, float[]>(data[0], pos));
				}
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			first = false;
		}
		for (int i = 0; i < players.size(); i++) {
			// find the player name and return it
			if (players.get(i).getX().contentEquals(username)) {
				return players.get(i).getY();
			}
		}
		// otherwise return a default array.
		return new float[] {0, 130, 0, 0, 0, 0};
	}
	
	/**
	 * saves the player position of the disconnecting client
	 */
	public static void disconnectedPlayer(ConnectedClient cl) {
		try {
			boolean found = false;
			// finds the player and changes its position in the player list
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getX().contentEquals(cl.username)) {
					players.get(i).setY(cl.plypos);
					found = true;
					break;
				}
			}
			if (!found) {
				// add a new one if this dude isn't found
				// he should be found but whatever.
				players.add(new Tuple<String, float[]>(cl.username, cl.plypos));
			}
		} catch (Exception e) {}
	}
	
	public static void savePlayers(List<ConnectedClient> clients) {
		// loops through all the players that are connected and makes sure that all thier
		// positions in this list are correct, then saves it.
		for (int j = 0; j < clients.size(); j++) {
			boolean found = false;
			for (int i = 0; i < players.size(); i++){
				if (clients.get(j).username.contentEquals(players.get(i).getX())) {
					players.get(i).setY(clients.get(j).plypos);
					found = true;
				}
			}
			if (!found)
				players.add(new Tuple<String, float[]>(clients.get(j).username, clients.get(j).plypos));
		}
		// save the updated data.
		try {
			// its stupid that this has to be called
			new File(ServerWorld.worldLocation + "/players/players.dat").createNewFile();
			// open a writer to the player data file
			BufferedWriter wr = new BufferedWriter(new FileWriter(ServerWorld.worldLocation + "/players/players.dat"), 262144);
			// write all the client positions and usernames to the data file
			for (int i = 0; i < players.size(); i++) {
				float[] cl = players.get(i).getY();
				wr.write(players.get(i).getX() + ";" + cl[0] + ";" +cl[1] + ";" + cl[2] + ";" + cl[3] + ";" + cl[4] + ";" + cl[5]);
				wr.newLine();
			}
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
