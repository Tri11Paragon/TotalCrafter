package com.brett.networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.brett.Main;

/**
* @author Brett
* @date 4-Sep-2020
*/

public class ServerConnection extends Socket {

	public DataOutputStream toServer;
	public DataInputStream fromServer;
	public static Thread reciever;
	public static boolean interupt = true;
	private HashMap<Byte, List<NetworkEventReciever>> events = new HashMap<Byte, List<NetworkEventReciever>>();
	
	public ServerConnection(String hostname, int port) throws UnknownHostException, IOException {
		super(hostname, port);
		this.toServer = new DataOutputStream(new BufferedOutputStream(this.getOutputStream()));
		this.fromServer = new DataInputStream(new BufferedInputStream(this.getInputStream()));
		reciever = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(Main.isOpen && interupt) {
					try {
						byte flag = fromServer.readByte();
						List<NetworkEventReciever> el = events.get(flag);
						if (el == null)
							continue;
						for (int i = 0; i < el.size(); i++)
							el.get(i).dataRecieved(fromServer);
					} catch (Exception e) {  }
				}
				
			}
		});
		reciever.start();
	}
	
	public void registerEventReciever(byte flag, NetworkEventReciever event) {
		List<NetworkEventReciever> evl = events.get(flag);
		if (evl == null) {
			evl = new ArrayList<NetworkEventReciever>();
			events.put(flag, evl);
		}
		evl.add(event);
	}
	
	public void sendChunkReq(int x, int y, int z) {
		try {
			toServer.writeByte(Flags.B_CHUNKREQ);
			toServer.writeInt(x);
			toServer.writeInt(y);
			toServer.writeInt(z);
		} catch (Exception e) {
			
		}
	}
	
	public void sendBlockMod(int x, int y, int z, short block) {
		try {
			toServer.writeByte(Flags.B_BLOCKSET);
			toServer.writeInt(x);
			toServer.writeInt(y);
			toServer.writeInt(z);
			toServer.writeShort(block);
		} catch (Exception e) {}
	}
	
	public void sendPlayerSync(double x, double y, double z) {
		try {
			toServer.writeByte(Flags.P_PLYSYNC);
			toServer.writeDouble(x);
			toServer.writeDouble(y);
			toServer.writeDouble(z);
		} catch (Exception e) {}
	}
	
	public static ServerConnection connectToServer(String hostname, int port) {
		interupt = false;
		reciever.interrupt();
		try {
			interupt = true;
			ServerConnection sr = new ServerConnection(hostname, port);
			return sr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
