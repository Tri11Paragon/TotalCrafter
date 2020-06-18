package com.brett.voxel.networking.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.renderer.MasterRenderer;
import com.brett.tools.Debug;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.networking.PACKETS;
import com.brett.voxel.world.chunk.Chunk;
import com.mainclass.ServerTest;

/**
*
* @author brett
* @date May 31, 2020
*/

public class Server extends Thread {
	
	public static Server server;
	
	public static final int PORT = 1337;
	public List<ConnectedClient> clients = new ArrayList<ConnectedClient>();
	public HashMap<Integer, ConnectedClient> clientMap = new HashMap<Integer, ConnectedClient>();
	private int lastID = 0;
	
	public DatagramSocket ds;
	
	public ServerWorld sworld;
	
	public Server() {
		this.sworld = new ServerWorld();
		server = this;
		try {
			ds = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		this.start();
	}
	@Override
	public void run() {
		super.run();
		
		// Receive buffer
		byte[] recive = new byte[65535]; 
		DatagramPacket recivePacket = null;
		while (VoxelScreenManager.isOpen) {
			// receive any packets
			recivePacket = new DatagramPacket(recive, recive.length);
			try {
				ds.receive(recivePacket);
			} catch (IOException e) {e.printStackTrace();}
			// process the received packet.
			handlePacket(recive, recivePacket);
			//Debug.print("Client: " + dataToString(recive));
			recive = new byte[65535];
		}
		
	}
	
	public void handlePacket(byte[] bt, DatagramPacket packet) {
		// various byte arrays for processing because switch statements are very stupid
		byte[] idbuff = new byte[0];
		byte[] buff = new byte[0];
		ByteBuffer idb;
		int id = 0;
		// todo: not use useless switch statements
		switch (bt[0]) {
			case PACKETS.LOGIN:
				// send the id to the players
				buff = Arrays.copyOfRange(bt, 1, bt.length);
				ByteBuffer bty = ByteBuffer.allocate(5);
				bty.put(PACKETS.LOGIN);
				bty.putInt(lastID);
				// tell all connected clients that a new client has connected.
				for (int i = 0; i < clients.size(); i++) {
					if (clients.isEmpty())
						break;
					clients.get(i).sendData(bty.array());
				}
				// create a new conenct client
				ConnectedClient cn = new ConnectedClient(packet.getAddress(), packet.getPort(), dataToString(buff).toString().trim(),
						new float[] {0, 130, 0, 0, 0, 0}, lastID);
				System.out.println("new client! " + dataToString(buff).toString().trim());
				
				// add the client
				clients.add(cn);
				clientMap.put(lastID, cn);
				
				// send the client their loaded position
				StringBuilder bu = new StringBuilder();
				float[] posd = PlayerSaver.loadPlayer(dataToString(buff).toString().trim());
				bu.append(posd[0]);
				bu.append(";");
				bu.append(posd[1]);
				bu.append(";");
				bu.append(posd[2]);
				bu.append(";");
				bu.append(posd[3]);
				bu.append(";");
				bu.append(posd[4]);
				bu.append(";");
				bu.append(posd[5]);
				bu.append(";");
				byte[] chars = bu.toString().getBytes();
				// send the id of the player and his position to the client
				idb = ByteBuffer.allocate(5 + chars.length*2);
				idb.put(PACKETS.ID);
				idb.putInt(lastID);
				
				for (int i = 0; i < chars.length; i++) {
					idb.put(chars[i]);
				}
				
				cn.sendData(idb.array());
				
				// send all the clients to the newly connected client.
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).id == lastID)
						continue;
					ByteBuffer oldclients = ByteBuffer.allocate(5);
					oldclients.put(PACKETS.LOGIN);
					oldclients.putInt(clients.get(i).id);
					cn.sendData(oldclients.array());
				}
				
				// increase the id.
				lastID++;
				break;
			case PACKETS.CHUNKREQ:
				// get the id and chunk pos
				byte[] idbuffd = Arrays.copyOfRange(bt, 1, 5);
				byte[] posbuff = Arrays.copyOfRange(bt, 5, bt.length);
				ByteBuffer posb = ByteBuffer.wrap(posbuff);
				ByteBuffer idbb = ByteBuffer.wrap(idbuffd);
				int idd = idbb.getInt();
				// get the client
				ConnectedClient cll = clientMap.get(idd);
				if (cll == null)
					return;
				int x = posb.getInt();
				int z = posb.getInt();
				// request a chunk from the world
				sworld.queChunk(x, z, cll);
				break;
			case PACKETS.POSSYNC:
				// snycs the position
				buff = Arrays.copyOfRange(bt, 5, bt.length);
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				// get the client
				ConnectedClient cl = clientMap.get(id);
				if (cl == null)
					return;
				// get the position from the []
				String pos = dataToString(buff).toString();
				String[] posa = pos.split(";");
				// set the clients pos
				try {
					cl.plypos[0] = Float.parseFloat(posa[0]);
				}catch (Exception e) {}
				try {
					cl.plypos[1] = Float.parseFloat(posa[1]);
				}catch (Exception e) {}
				try {
					cl.plypos[2] = Float.parseFloat(posa[2]);
				}catch (Exception e) {}
				try {
					cl.plypos[3] = Float.parseFloat(posa[3]);
				}catch (Exception e) {}
				try {
					cl.plypos[4] = Float.parseFloat(posa[4]);
				}catch (Exception e) {}
				try {
					cl.plypos[5] = Float.parseFloat(posa[5]);
				}catch (Exception e) {}
				// update all the clients with the new pos
				for (int i = 0; i < clients.size(); i++) {
					ConnectedClient crm = clients.get(i);
					if (clients.get(i).id == id)
						continue;
					byte[] newbits = Arrays.copyOfRange(bt, 0, 3000);
					crm.sendData(newbits);
				}
				break;
			case PACKETS.DISCONNECT:
				// send to all clients that a player of a id has disconnected.
				byte[] idbuffdd = Arrays.copyOfRange(bt, 1, 5);
				ByteBuffer idbd = ByteBuffer.wrap(idbuffdd);
				int iddd = idbd.getInt();
				// save the player
				PlayerSaver.disconnectedPlayer(clientMap.get(iddd));
				clientMap.remove(iddd);
				sendDataToAllClients(Arrays.copyOfRange(bt, 0, 10));
				for (int i = 0; i < clients.size(); i++) {
					if (clients.get(i).id == iddd) {
						clients.remove(i);
					}
				}
				break;
			case PACKETS.BLOCKMOD:
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				ByteBuffer blbuff = ByteBuffer.wrap(Arrays.copyOfRange(bt, 5, 5 + 4*4 + 3));
				// get the pos of the block mod and the id
				int bx =  blbuff.getInt();
				int by = blbuff.getInt();
				int bz = blbuff.getInt();
				short blk = blbuff.getShort();
				
				// update the block
				sworld.setBlock(bx, by, bz, blk);
				
				ByteBuffer blmodbuff = ByteBuffer.allocate(blbuff.capacity()+1);
				// send the mod to all clients.
				blmodbuff.put(PACKETS.BLOCKMOD);
				blmodbuff.put(blbuff.array());
				
				for (int i = 0; i < clients.size(); i++) {
					ConnectedClient ccl = clients.get(i);
					if (ccl.id != id) {
						ccl.sendData(blmodbuff.array());
					}
				}
				break;
			case PACKETS.EXIT:
				// send a message to all clients that the server has exited.
				sendDataToAllClients(new byte[] {PACKETS.EXIT, PACKETS.EXIT, PACKETS.EXIT});
				ServerTest.line = "exit";
				VoxelScreenManager.isOpen = false;
				try {
					ServerTest.sc.close();
				} catch (IOException e) {e.printStackTrace();}
				break;
		}
	}
	
	public void cleanup() {
		
	}
	
	/**
	 * sends the byte[] to all clients connected to the server
	 */
	public void sendDataToAllClients(byte[] buff) {
		for (int i = 0; i < clients.size(); i++) {
			ConnectedClient cc = clients.get(i);
			cc.sendData(buff);
		}
	}
	
	/**
	 * sends a compressed chunk to the client
	 */
	public void sendCompressedChunk(Chunk c, ConnectedClient cc) {
		try {
			// write all the data in the chunk to a compressed byte[]
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(PACKETS.CHUNKREQ);
			GZIPOutputStream os = new GZIPOutputStream(bos);
			DataOutputStream dos = new DataOutputStream(os);
			
			short[][][] blocks = c.getBlocks();
			for (int i = 0; i < Chunk.x; i++) {
				for (int j = 0; j < Chunk.y; j++) {
					for (int k = 0; k < Chunk.z; k++) {
						dos.writeShort(blocks[i][j][k]);
					}
				}
			}
			
			dos.writeInt(c.getX());
			dos.writeInt(c.getZ());
			
			dos.flush();
			dos.close();
			bos.write(PACKETS.CHUNKREQ);
			// send the compressed written data to the client
			//System.out.println(bos.toByteArray().length);
			cc.sendData(bos.toByteArray());
		} catch (IOException e) {
			Debug.print("error in compressor");
			e.printStackTrace();
		}
	}
	
	/**
	 * decompresses a chunk from a byte stream.
	 */
	public Chunk decodeChunk(byte[] bt) {
		short[][][] blocks = new short[Chunk.x][Chunk.y][Chunk.z];
		int xoff = 0;
		int zoff = 0;
		try {
			byte[] bytes = new byte[0];
			for (int i = bt.length-1; i > 0; i--) {
				if (bt[i] == PACKETS.CHUNKREQ) {
					// reduces the size to smaller
					bytes = Arrays.copyOfRange(bt, 1, i);
					break;
				}
			}
			// decode the chunk
			DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)));
			
			for (int i = 0; i < Chunk.x; i++) {
				for (int j = 0; j < Chunk.y; j++) {
					for (int k = 0; k < Chunk.z; k++) {
						try {
							blocks[i][j][k] = dis.readShort();
						} catch (Exception e) {} 
					}
				}
			}
			
			xoff = dis.readInt();
			zoff = dis.readInt();
			
			dis.close();
		} catch (IOException e) {e.printStackTrace();}
		// create the new chunk
		Chunk c = new Chunk(MasterRenderer.global_loader, sworld, blocks, xoff, zoff);
		return c;
	}
	
	/**
	 * converts a byte[] to string
	 */
    public static StringBuilder dataToString(byte[] a) { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        for (int i = 0; i < a.length; i++) {
        	if (a[i] == 0)
        		break;
            ret.append((char) a[i]); 
        } 
        return ret; 
    } 
	
}
