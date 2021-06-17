package com.brett.networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3d;

import com.brett.networking.server.NetworkTransmitEvent;
import com.brett.networking.server.Server;
import com.brett.utils.Auth;
import com.brett.utils.RunLengthEncoding;
import com.brett.world.chunks.Chunk;

/**
 * @author Brett
 * @date 5-Sep-2020
 * reference to client connected to the server
 * 'Olvia is a drunk; send videos while drunk with friend
 * Zev liked her 13 months, thought about her when he was high on mushrooms
 * Don't tell anyone, kicked her from bcg.. Olivia made him want to die
 */

public class Client extends Thread {

	public Socket socketToClient;
	public Vector3d pos = new Vector3d();
	public String username = null;
	private DataInputStream dis;
	private DataOutputStream dos;
	public List<NetworkTransmitEvent> transmit_events = new ArrayList<NetworkTransmitEvent>();
	public boolean client_open = true;
	
	public Client(Socket s) {
		this.socketToClient = s;
		try {
			this.dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			this.dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
		} catch (Exception e) {}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (Server.isOpen && client_open) {
					try {
						if (transmit_events.size() > 0) {
							NetworkTransmitEvent e1 = transmit_events.get(0);
							transmit_events.remove(0);
							if (e1 == null)
								continue;
							e1.transmit(dos);
						} else
							Thread.sleep(1);
					} catch (Exception e) {
						try {
							dis.close();
							dos.close();
						} catch (IOException er) {}
						e.printStackTrace();
						disconnect();
						return;
					}
				}
			}
		}).start();
	}

	@Override
	public void run() {
		super.run();
		while (Server.isOpen && client_open) {
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
					
					byte ret = dis.readByte();
					System.out.println(ret);
					if (ret != Flags.B_BLOCKREQ)
						continue;
					
					Chunk c = Server.world.getChunk(x, y, z);
					if (c != null) {
						sendChunk(c);
						continue;
					}
					
					List<Client> cli = Server.world.playerRequestedChunks.get(x, y, z);
					if (cli == null) {
						cli = new ArrayList<Client>();
						Server.world.playerRequestedChunks.set(x, y, z, cli);
						System.out.println("Generating Chunk [" + x + ' ' + y + ' ' + z + "]");
					}
					cli.add(this);
					Server.world.queueChunk(x, y, z);
				}
				if (flag == Flags.P_PLYSYNC) {
					double x = dis.readDouble();
					double y = dis.readDouble();
					double z = dis.readDouble();
					byte ret = dis.readByte();
					if (ret != Flags.P_PLYSYNC)
						continue;
					pos.x = x;
					pos.y = y;
					pos.z = z;
				}
				if (flag == Flags.S_LOGIN) {
					username = dis.readUTF();
					String token = dis.readUTF();
					
					int level = Auth.check_auth_token(username, token);
					
					if (level == 0) {
						transmit_events.add((DataOutputStream dos) -> {
							try {
								dos.writeByte(Flags.S_NOBUY);
								dos.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							disconnect();
						});
					}
					
					if (level == -1) {
						System.out.println("Client with bad login information attempted to connect");
						transmit_events.add((DataOutputStream dos) -> {
							try {
								dos.writeByte(Flags.S_BADLOGIN);
								dos.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							disconnect();
						});
					}
					
					if (level == -2) {
						System.out.println("Client with bad token attempted to connect");
						transmit_events.add((DataOutputStream dos) -> {
							try {
								dos.writeByte(Flags.S_BADTOKEN);
								dos.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							disconnect();
						});
					}
					
				}
			} catch (Exception e) {
				try {
					dis.close();
					dos.close();
				} catch (IOException er) {}
				disconnect();
				break;
			} 
		}
	}
	
	public void sendClose() {
		transmit_events.add((DataOutputStream dos) -> {
			try {
				dos.writeByte(Flags.S_STOP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	// TODO: this
	public void sendChunk(Chunk c) {
		return;
		/*transmit_events.add((DataOutputStream dos) -> {
			try {
				ArrayList<Short> compressed_blocks = RunLengthEncoding.encode_chunk(c.blocks.blocks);
				dos.writeByte(Flags.B_CHUNKREQ);
				dos.writeInt(c.x_pos);
				dos.writeInt(c.y_pos);
				dos.writeInt(c.z_pos);
				int size = compressed_blocks.size();
				dos.writeInt(size);
				for (int i = 0; i < compressed_blocks.size(); i++) {
					dos.writeShort(compressed_blocks.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});*/
	}
	
	public void sendPos(Vector3d pos, String username) {
		transmit_events.add((DataOutputStream dos) -> {
			try {
				dos.writeByte(Flags.P_PLYSYNC);
				dos.writeDouble(pos.x);
				dos.writeDouble(pos.y);
				dos.writeDouble(pos.z);
				dos.writeUTF(username);
			} catch (Exception e) {}
		});	
	}
	
	public void disconnect() {
		Server.connectedClients.remove(this);
		this.client_open = false;
		System.out.println("Client disconnected [" + socketToClient.getInetAddress() + ":" + socketToClient.getPort() + "]");
		try {
			socketToClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
