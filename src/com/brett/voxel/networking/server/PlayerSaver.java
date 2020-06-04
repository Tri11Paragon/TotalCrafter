package com.brett.voxel.networking.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.datatypes.Tuple;

/**
*
* @author brett
* @date Jun. 2, 2020
*/

public class PlayerSaver {
	
	public static List<Tuple<String, Vector3f>> players = new ArrayList<Tuple<String,Vector3f>>();
	
	private static boolean first = true;
	public static Vector3f loadPlayer(String username) {
		if (!new File(ServerWorld.worldLocation + "/players/players.dat").exists())
			return new Vector3f(0,100,0);
		if (first) {
			try {
				BufferedReader wr = new BufferedReader(new FileReader(ServerWorld.worldLocation + "/players/players.dat"), 262144);
				String line = "";
				while ((line = wr.readLine()) != null) {
					String[] data = line.split(";");
					Vector3f pos = new Vector3f();
					try {
						pos.x = Float.parseFloat(data[1]);
						pos.y = Float.parseFloat(data[2]);
						pos.z = Float.parseFloat(data[3]);
					} catch (Exception e) {}
					players.add(new Tuple<String, Vector3f>(data[0], pos));
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
		return new Vector3f(0, 100, 0);
	}
	
	public static void disconnectedPlayer(ConnectedClient cl) {
		try {
			boolean found = false;
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getX().contentEquals(cl.username)) {
					players.get(i).getY().set(cl.plypos);
					found = true;
					break;
				}
			}
			if (!found) {
				players.add(new Tuple<String, Vector3f>(cl.username, cl.plypos));
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
				players.add(new Tuple<String, Vector3f>(clients.get(j).username, clients.get(j).plypos));
		}
		try {
			// its stupid that this has to be called
			new File(ServerWorld.worldLocation + "/players/players.dat").createNewFile();
			// open a writer to the player data file
			BufferedWriter wr = new BufferedWriter(new FileWriter(ServerWorld.worldLocation + "/players/players.dat"), 262144);
			// write all the client positions and usernames to the data file
			for (int i = 0; i < players.size(); i++) {
				Vector3f cl = players.get(i).getY();
				wr.write(players.get(i).getX() + ";" + cl.x + ";" +cl.y + ";" + cl.z);
				wr.newLine();
			}
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
