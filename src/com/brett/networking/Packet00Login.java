package com.brett.networking;

/**
*
* @author brett
*
*/

public class Packet00Login extends Packet {
	
	private String username;
	
	public Packet00Login(byte[] data) {
		super(0);
		this.username = readData(data);
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	// telling all the clients that we have connected is a good idea
	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("00" + this.username).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
}
