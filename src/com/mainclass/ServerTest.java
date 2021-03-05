package com.mainclass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.networking.server.Server;

/**
*
* @author brett
* @date May 31, 2020
*/

public class ServerTest {

	public static String line = "";
	public static BufferedReader sc;
	
	public static void main(String[] args) {
		Server sr = new Server();
		sc = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			while (!line.contentEquals("exit") && VoxelScreenManager.isOpen) {
				if (sc.ready())
					line = sc.readLine();
				else
					Thread.sleep(16);
			}
		} catch (Exception e) {System.err.println("small error. Closing."); System.err.println(e.getCause());}
		
		sr.sworld.saveChunks();
		VoxelScreenManager.isOpen = false;
		
		
		try {
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
