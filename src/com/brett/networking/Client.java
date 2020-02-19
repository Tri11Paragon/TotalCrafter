package com.brett.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
*
* @author brett
* 
* The client is the instance running on the local host. It needs to be
* on a seperate thread, or else the game wont do anything until the 
* client connects to the server.
*
*/

public class Client extends Thread {
	
	private InetAddress ipAddress;
	private DatagramSocket c;
	private boolean running = true;
	
	public Client(String ip) {
		try {
			this.ipAddress = InetAddress.getByName(ip);
			this.c = new DatagramSocket();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (this.running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				c.receive(packet);
			} catch (SocketException e) {
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("[Server]> " + new String(packet.getData()));
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, Server.PORT);
		try {
			c.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		this.running = false;
		c.close();
	}
	
	public DatagramSocket getC() {
		return c;
	}

	public void setC(DatagramSocket c) {
		this.c = c;
	}
	
}
