package com.brett.voxel.networking.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
*
* @author brett
* @date May 31, 2020
*/

public class ConnectedClient {
	
	public String username;
	public int port;
	public InetAddress ipadd = null;
	public DatagramSocket ds;
	public float[] plypos;
	public int id;
	
	public ConnectedClient(InetAddress ip, int port, String username, float[] plypos, int id) {
		this.port = port;
		this.username = username;
		this.ipadd = ip;
		this.plypos = plypos;
		this.id = id;
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void sendData(byte[] buff) {
		DatagramPacket sp = new DatagramPacket(buff, buff.length, ipadd, port);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
}
