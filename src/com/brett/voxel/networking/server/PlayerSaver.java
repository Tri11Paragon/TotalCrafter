package com.brett.voxel.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.brett.renderer.datatypes.Tuple;

/**
*
* @author brett
* @date Jun. 2, 2020
*/

public class PlayerSaver {
	
	public static List<Tuple<String, float[]>> players = new ArrayList<Tuple<String,float[]>>();
	
	private static boolean first = true;
	public static float[] loadPlayer(String username) {
		if (!new File(ServerWorld.worldLocation + "/players/players.dat").exists())
			return new float[] {0, 100, 0, 0, 0, 0};
		if (first) {
			try {
				BufferedReader wr = new BufferedReader(new FileReader(ServerWorld.worldLocation + "/players/players.dat"), 262144);
				String line = "";
				while ((line = wr.readLine()) != null) {
					String[] data = line.split(";");
					float[] pos = new float[6];
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
					players.add(new Tuple<String, float[]>(data[0], pos));
				}
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			first = false;
		}
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getX().contentEquals(username)) {
				return players.get(i).getY();
			}
		}
		return new float[] {0, 100, 0, 0, 0, 0};
	}
	
	public static void disconnectedPlayer(ConnectedClient cl) {
		try {
			boolean found = false;
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getX().contentEquals(cl.username)) {
					players.get(i).setY(cl.plypos);
					found = true;
					break;
				}
			}
			if (!found) {
				players.add(new Tuple<String, float[]>(cl.username, cl.plypos));
			}
		} catch (Exception e) {}
	}
	
	public static void savePlayers(List<ConnectedClient> clients) {
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
