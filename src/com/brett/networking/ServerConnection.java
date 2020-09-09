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
import com.brett.networking.server.NetworkRecieveEvent;
import com.brett.networking.server.NetworkTransmitEvent;
import com.brett.utils.RunLengthEncoding;
import com.brett.world.World;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.ByteBlockStorage;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
* @author Brett
* @date 4-Sep-2020
*/

public class ServerConnection extends Socket {

	public DataOutputStream toServer;
	public DataInputStream fromServer;
	public static Thread reciever;
	public static Thread transmitter;
	public static boolean interupt = true;
	private HashMap<Byte, NetworkRecieveEvent> recieve_events = new HashMap<Byte, NetworkRecieveEvent>();
	private List<NetworkTransmitEvent> transmit_events = new ArrayList<NetworkTransmitEvent>();
	
	public ServerConnection(String hostname, int port) throws UnknownHostException, IOException {
		super(hostname, port);
		this.toServer = new DataOutputStream(new BufferedOutputStream(this.getOutputStream()));
		this.fromServer = new DataInputStream(new BufferedInputStream(this.getInputStream()));
		registerEventReciever(Flags.B_CHUNKREQ, (DataInputStream dis) -> {
			try {
				int x = dis.readInt();
				int y = dis.readInt();
				int z = dis.readInt();
				ArrayList<Short> blocks_compressed = new ArrayList<Short>();
				int size = dis.readInt();
				for (int i = 0; i < size; i++) {
					blocks_compressed.add(dis.readShort());
				}
				short[][][] blks = RunLengthEncoding.decode_chunk(blocks_compressed);
				World.world.setChunk(new Chunk(World.world, new ShortBlockStorage().setBlocks(blks), new ByteBlockStorage(), x, y, z));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		registerEventReciever(Flags.S_STOP, (DataInputStream dis) -> {
			System.out.println("TODO: add menu for server shutting down");
			System.out.println("Server shutting down");
			interupt = false;
			reciever.interrupt();
			transmitter.interrupt();
		});
		registerEventReciever(Flags.S_BADLOGIN, (DataInputStream dis) -> {
			System.out.println("LOGIN FAILED");
			interupt = false;
			reciever.interrupt();
			transmitter.interrupt();
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(-1);
		});
		registerEventReciever(Flags.S_BADTOKEN, (DataInputStream dis) -> {
			System.out.println("BAD TOKEN");
			interupt = false;
			reciever.interrupt();
			transmitter.interrupt();
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(-1);
		});
		registerEventReciever(Flags.S_NOBUY, (DataInputStream dis) -> {
			System.out.println("Multiplayer is for people who buy the game!");
			interupt = false;
			reciever.interrupt();
			transmitter.interrupt();
			try {
				this.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(-1);
		});
		registerEventReciever(Flags.S_LOGIN, (DataInputStream dis) -> {
			
		});
		registerEventReciever(Flags.P_PLYSYNC, (DataInputStream dis) -> {
			try {
				double x = dis.readDouble();
				double y = dis.readDouble();
				double z = dis.readDouble();
				String username = dis.readUTF();
			} catch (Exception e) {}
		});
		reciever = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(Main.isOpen && interupt) {
					try {
						byte flag = fromServer.readByte();
						NetworkRecieveEvent el = recieve_events.get(flag);
						if (el == null)
							continue;
						el.dataRecieved(fromServer);
					} catch (Exception e) { 
						interupt = false;
						return;
					}
				}
				
			}
		});
		reciever.start();
		transmitter = new Thread(new Runnable() {
			@Override
			public void run() {
				while(Main.isOpen && interupt) {
					try {
						if (transmit_events.size() > 0) {
							NetworkTransmitEvent e1 = transmit_events.get(0);
							transmit_events.remove(0);
							if (e1 == null)
								continue;
							e1.transmit(toServer);
						} else
							Thread.sleep(1);
					} catch (Exception e) {
						e.printStackTrace();
						interupt = false;
						return;
					}
				}
			}
		});
		transmitter.start();
		transmit_events.add((DataOutputStream dos) -> {
			try {
				dos.writeByte(Flags.S_LOGIN);
				dos.writeUTF(Main.username);
				dos.writeUTF(Main.token);
			} catch (Exception e) {}
		});
	}
	
	public void registerEventReciever(byte flag, NetworkRecieveEvent event) {
		recieve_events.put(flag, event);
	}
	
	public void sendChunkReq(int x, int y, int z) {
		transmit_events.add((DataOutputStream dos) -> {
			try {
				toServer.writeByte(Flags.B_CHUNKREQ);
				toServer.writeInt(x);
				toServer.writeInt(y);
				toServer.writeInt(z);
				toServer.writeByte(Flags.B_BLOCKREQ);
			} catch (Exception e) {}
		});
	}
	
	public void sendBlockMod(int x, int y, int z, short block) {
		transmit_events.add((DataOutputStream dos) -> {
			try {
				toServer.writeByte(Flags.B_BLOCKSET);
				toServer.writeInt(x);
				toServer.writeInt(y);
				toServer.writeInt(z);
				toServer.writeShort(block);
			} catch (Exception e) {}
		});
	}
	
	public void sendPlayerSync(double x, double y, double z) {
		transmit_events.add((DataOutputStream dos) -> {
			try {
				toServer.writeByte(Flags.P_PLYSYNC);
				toServer.writeDouble(x);
				toServer.writeDouble(y);
				toServer.writeDouble(z);
				toServer.writeByte(Flags.P_PLYSYNC);
			} catch (Exception e) {}
		});
	}
	
	public static ServerConnection connectToServer(String hostname, int port) {
		interupt = false;
		if (reciever != null)
			reciever.interrupt();
		if (transmitter != null)
			transmitter.interrupt();
		
		try {
			Thread.sleep(5);
			interupt = true;
			ServerConnection sr = new ServerConnection(hostname, port);
			return sr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
