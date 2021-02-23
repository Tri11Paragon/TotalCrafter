package com.brett.networking.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3d;

import com.brett.Main;
import com.brett.networking.Client;
import com.brett.utils.RunLengthEncoding;
import com.brett.world.GameRegistry;
import com.brett.world.World;

/**
* @author Brett
* @date 5-Sep-2020
*/

public class Server {

	public static ServerSocket serverSocket;
	public static boolean isOpen = true;
	public static List<Client> connectedClients = new ArrayList<Client>();
	public static BufferedReader sc;
	public static String line = "";
	public static World world;
	
	//server properties
	public static int port = 1337;
	public static int render_distance;
	
	public static void main(String[] args) {
		/**
		 * Assign system variables
		 */
		Main.mx = ManagementFactory.getMemoryMXBean();
		Main.osx = ManagementFactory.getOperatingSystemMXBean();
		Main.rnx = ManagementFactory.getRuntimeMXBean();
		Main.thx = ManagementFactory.getThreadMXBean();
		Main.os = System.getProperty("os.name");
		Main.os_version = System.getProperty("os.version");
		Main.os_arch = System.getProperty("os.arch");
		Main.file_separator = System.getProperty("file.separator");
		Main.path_separator = System.getProperty("path.separator");
		Main.line_separator = System.getProperty("line.separator");
		Main.user_name = System.getProperty("user.name");
		Main.user_home = System.getProperty("user.home");
		Main.user_workingdir = System.getProperty("user.dir");
		Main.processors = Runtime.getRuntime().availableProcessors();
		if (Main.processors < 4)
			Main.processors = 4;
		
		short[][][] blks = new short[16][16][16];
		for (int i = 0; i < 16; i++)
			blks[i][i][i] = 1;
		for (int i = 0; i < 16; i++)
			blks[i][0][i] = 1;
		for (int i = 0; i < 16; i++)
			blks[i][0][0] = 1;
		
		ArrayList<Short> yes = RunLengthEncoding.encode_chunk(blks);
		System.out.print("[");
		for (int i = 0; i < yes.size(); i++) {
			if (i % 2 == 0)
				System.out.print("[" + yes.get(i) + ", ");
			if ((i % 2) == 1)
				System.out.print(yes.get(i) + "] ");
			if (i % 35 == 0 && i > 30)
				System.out.println();
		}
		System.out.print("] \n");
		System.out.println();
		
		short[][][] deblks = RunLengthEncoding.decode_chunk(yes);
		yes = RunLengthEncoding.encode_chunk(deblks);
		
		System.out.print("[");
		for (int i = 0; i < yes.size(); i++) {
			if (i % 2 == 0)
				System.out.print("[" + yes.get(i) + ", ");
			if ((i % 2) == 1)
				System.out.print(yes.get(i) + "] ");
			if (i % 35 == 0 && i > 30)
				System.out.println();
		}
		System.out.print("] \n");
		System.out.println();
		
		
		try {
			ServerProperties.load();
			port = ServerProperties.getPropertyInt("port");
			render_distance = ServerProperties.getPropertyInt("render_distance");
			
			serverSocket = new ServerSocket(port);
			sc = new BufferedReader(new InputStreamReader(System.in));
			GameRegistry.registerBlocks();
			GameRegistry.registerItems();
			world = new World();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (isOpen) {
						try {
							Client nc = new Client(serverSocket.accept());
							nc.start();
							connectedClients.add(nc);
							System.out.println("Client connected [" + nc.socketToClient.getInetAddress() + ":" + nc.socketToClient.getPort() 
								+ " | " + nc.socketToClient.getLocalAddress() + ":" + nc.socketToClient.getLocalPort() + "]");
						} catch (Exception e) {}
					}
				}
			}).start();
			
			while (isOpen) {
				if (sc.ready())
					line = sc.readLine();
				else {
					if ("exit".contentEquals(line) || "stop".contentEquals(line)) {
						isOpen = false;
						for (int i = 0; i < connectedClients.size(); i++)
							connectedClients.get(i).sendClose();
						world.save();
					}
					Thread.sleep(16);
				}
			}
			
			System.out.println("Goodbye");
			
			sc.close();
			isOpen = false;
			serverSocket.close();
		} catch (Exception e) {e.printStackTrace();}
		
	}
	
	public static void sendPositionToAllClientsInRange(Vector3d pos, Client sender) {
		for (int i = 0; i < connectedClients.size(); i++) {
			Client c = connectedClients.get(i);
			if (c.pos.distance(sender.pos) < (render_distance*16)) {
				c.sendPos(pos, sender.username);
			}
		}
	}

}
