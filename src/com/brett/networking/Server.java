package com.brett.networking;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.brett.networking.Packet.packetTypes;

/**
*
* @author brett
*
*/

public class Server extends Thread {

	public static final int PORT = 1337;
	private boolean running = true;
	
	private List<Client> clients = new ArrayList<Client>();
	private DatagramSocket c;
	
	public Server() {
		try {
			this.c = new DatagramSocket(PORT);
		} catch (BindException e) {
			System.err.println("Port " + PORT + " is already in use!");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				c.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			handlePacket(packet.getData(), packet.getAddress(), packet.getPort());
//			String message = new String(packet.getData()).trim();
//			System.out.println("[" + packet.getAddress().getHostAddress() + "/Client]> " + message);
//			if (message.equalsIgnoreCase("test")) {
//				sendData(new String("welcome client!").getBytes(), packet.getAddress(), packet.getPort());
//			}
		}
	}
	// oh no
	private void handlePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		packetTypes type = Packet.lookupPacket(Integer.parseInt(msg.substring(0, 2)));
		switch(type) {
			default:
				System.out.println("INVALID PACKET");
				break;
			case INVALID:
				System.out.println("INVALID PACKET");
				break;
			case LOGIN:
				Packet00Login packet = new Packet00Login(data);
				System.out.println("[Server]:" + address.getHostAddress() + " : " + port + " > " + packet.getUsername() + " has connected!");
			case DISCONNECT:
				break;
		}
	}
	
	// remove the port thing
	public void sendData(byte[] data, InetAddress address, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			c.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		running = false;
		c.close();
	}

	public DatagramSocket getC() {
		return c;
	}

	public void setC(DatagramSocket c) {
		this.c = c;
	}

	// updates all the clients
	public void sendDataToAllClients(byte[] data) {
		for (Client c : clients) {
			DatagramSocket p = c.getC();
			sendData(data, p.getInetAddress(), p.getPort());
		}
	}
	
}
