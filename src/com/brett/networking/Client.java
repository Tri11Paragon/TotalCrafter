package com.brett.networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.brett.networking.server.Server;
import com.brett.world.chunks.Chunk;

/**
 * @author Brett
 * @date 5-Sep-2020
 * reference to client connected to the server
 */

public class Client extends Thread {

	public Socket socketToClient;
	public DataInputStream dis;
	public DataOutputStream dos;
	
	public Client(Socket s) {
		this.socketToClient = s;
		try {
			this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			this.dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
		} catch (Exception e) {}
	}

	@Override
	public void run() {
		super.run();
		while (Server.isOpen) {
			if (socketToClient.isClosed() || socketToClient.isOutputShutdown()) {
				Server.connectedClients.remove(this);
				break;
			}
			try {
				byte flag = dis.readByte();
				if (flag == Flags.B_CHUNKREQ) {
					int x = dis.readInt();
					int y = dis.readInt();
					int z = dis.readInt();
					
					Chunk c = Server.world.getChunk(x, y, z);
					if (c != null) {
						sendChunk(c);
						continue;
					}
					
					List<Client> cli = Server.world.playerRequestedChunks.get(x, y, z);
					if (cli == null) {
						cli = new ArrayList<Client>();
						Server.world.playerRequestedChunks.set(x, y, z, cli);
					}
					cli.add(this);
					Server.world.queueChunk(x, y, z);
				}
			} catch (Exception e) {
				Server.connectedClients.remove(this);
				System.out.println("Client disconnected [" + socketToClient.getInetAddress() + ":" + socketToClient.getPort() + "]");
				break;
			} 
		}
	}
	
	public void sendClose() {
		try {
			dos.writeByte(Flags.S_STOP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendChunk(Chunk c) {
		try {
			dos.writeByte(Flags.B_CHUNKREQ);
			dos.writeInt(c.x_pos);
			dos.writeInt(c.y_pos);
			dos.writeInt(c.z_pos);
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					for (int k = 0; k < 16; k++) {
						dos.writeShort(c.blocks.blocks[i][j][k]);
					}
				}
			}
		} catch (Exception e) {
			
		}
	}

}
