package com.brett.voxel.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;

import com.brett.datatypes.Tuple;
import com.brett.renderer.MasterRenderer;
import com.brett.tools.Debug;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.inventory.Inventory;
import com.brett.voxel.networking.server.Server;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.chunk.Chunk;
import com.brett.voxel.world.player.Player;

/**
*
* @author brett
* @date May 31, 2020
* If you can't tell, I made this while learning about networking
* So the code sucks and there are many different ways that things are done.
* The client class
*/

public class Client extends Thread {
	
	public HashMap<Integer, Tuple<float[], float[]>> clients = new HashMap<Integer, Tuple<float[], float[]>>();
	public List<Inventory> inventories = new ArrayList<Inventory>();
	
	// server socket
	public DatagramSocket ds;
	// the receive packet.
	public DatagramPacket rp;
	public InetAddress ipadd = null;
	public int id = 0;
	public VoxelWorld world;
	public boolean connected = false;
	
	
	public Client(String ip, String username, VoxelWorld world) {
		this.world = world;
		// conenct to the server
		try {
			try {
				ds = new DatagramSocket();
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			try {
				ipadd = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				ipadd = InetAddress.getLocalHost();
			}
			if (ip.length() == 0 || ipadd == null) {
				ipadd = InetAddress.getLocalHost();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		this.start();
		// send the login to the server
		byte[] user = username.getBytes();
		byte[] login = new byte[user.length+1];
		login[0] = PACKETS.LOGIN;
		for (int i = 0; i < user.length; i++) {
			login[i+1] = user[i];
		}
		sendData(login);
	}
	
	/**
	 * sends a byte[] to the server
	 */
	public void sendData(byte[] buff) {
		DatagramPacket sp = new DatagramPacket(buff, buff.length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * sends a request for a chunk
	 */
	public void sendChunkRequest(int x, int z) {
		ByteBuffer buff = ByteBuffer.allocate(13);
		buff.put(PACKETS.CHUNKREQ);
		buff.putInt(id);
		buff.putInt(x);
		buff.putInt(z);
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * sends a disconnect packet to the server
	 */
	public void disconnect() {
		ByteBuffer buff = ByteBuffer.allocate(5);
		buff.put(PACKETS.DISCONNECT);
		buff.putInt(id);
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * updates the server's copy of the player position.
	 */
	public void updatePosition(Player ply) {
		StringBuilder bu = new StringBuilder();
		Vector3f pos = new Vector3f();
		ply.getPosition().get(pos);
		// send pos as a pos format
		bu.append(pos.x);
		bu.append(";");
		bu.append(pos.y);
		bu.append(";");
		bu.append(pos.z);
		bu.append(";");
		bu.append(ply.getPitch());
		bu.append(";");
		bu.append(ply.getYaw());
		bu.append(";");
		bu.append(ply.getRoll());
		bu.append(";");
		byte[] chars = bu.toString().getBytes();
		ByteBuffer buff = ByteBuffer.allocate(5 + chars.length*2);
		buff.put(PACKETS.POSSYNC);
		// make sure it knows what this is.
		buff.putInt(id);
		for (int i = 0; i < chars.length; i++) {
			buff.put(chars[i]);
		}
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * handles a packet from the server
	 */
	public void handlePacket(byte[] bt, DatagramPacket packet) {
		byte[] idbuff = new byte[0];
		byte[] buff = new byte[0];
		ByteBuffer idb;
		int id = 0;
		switch (bt[0]) {
			case PACKETS.ID:
				// get the clients id
				ByteBuffer wrapped = ByteBuffer.wrap(new byte[] {bt[1], bt[2], bt[3], bt[4]});
				this.id = wrapped.getInt();
				connected = true;
				
				buff = Arrays.copyOfRange(bt, 5, bt.length);
				
				// try to get a position for this player
				String posINIT = dataToString(buff).toString();
				String[] posaINIT = posINIT.split(";");
				try {
					world.ply.getPosition().x = Float.parseFloat(posaINIT[0]);
				}catch (Exception e) {}
				try {
					world.ply.getPosition().y = Float.parseFloat(posaINIT[1]);
				}catch (Exception e) {}
				try {
					world.ply.getPosition().z = Float.parseFloat(posaINIT[2]);
				}catch (Exception e) {}
				
				// decode the rotation
				if (posaINIT.length > 3) {
					try {
						world.ply.setPitch(Float.parseFloat(posaINIT[3]));
					}catch (Exception e) {}
					try {
						world.ply.setYaw(Float.parseFloat(posaINIT[4]));
					}catch (Exception e) {}
					try {
						world.ply.setRoll(Float.parseFloat(posaINIT[5]));
					}catch (Exception e) {}
				}
				
				break;
			case PACKETS.CHUNKREQ:
				// decodes chunk when it is sent to us
				Chunk c = decodeChunk(bt);
				world.chunk.insertChunk(c);
				break;
			case PACKETS.BLOCKMOD:
				// decodes the block mod request
				ByteBuffer blbuff = ByteBuffer.wrap(Arrays.copyOfRange(bt, 1, bt.length));
				int bx =  blbuff.getInt();
				int by = blbuff.getInt();
				int bz = blbuff.getInt();
				short blk = blbuff.getShort();
				
				// modifies without sending an update.
				world.chunk.setBlockServer(bx, by, bz, blk);
				
				break;
			case PACKETS.LOGIN:
				// add a new client to the list if they conenct
				byte[] idbuffd = Arrays.copyOfRange(bt, 1, 5);
				ByteBuffer idbb = ByteBuffer.wrap(idbuffd);
				int idd = idbb.getInt();
				clients.put(idd, new Tuple<float[], float[]>(new float[6], new float[6]));
				break;
			case PACKETS.DISCONNECT:
				// remove them when they disconnect
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				if (clients.containsKey(id))
					clients.remove(id);
				break;
			case PACKETS.POSSYNC:
				// change the data of the other client
				buff = Arrays.copyOfRange(bt, 5, bt.length);
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				Tuple<float[], float[]> cl = clients.get(id);
				if (cl == null) {
					clients.put(id, new Tuple<float[], float[]>(new float[6], new float[6]));
					cl = clients.get(id);
					// something is very broken.
					if (cl == null) {
						System.err.println("We got a big issue with a null user!");
						return;
					}
				}
				String pos = dataToString(buff).toString();
				String[] posa = pos.split(";");
				// update the position data
				try {
					cl.getY()[0] = Float.parseFloat(posa[0]);
					cl.getY()[1] = Float.parseFloat(posa[1]);
					cl.getY()[2] = Float.parseFloat(posa[2]);
					if (posa.length > 3) {
						cl.getY()[3] = Float.parseFloat(posa[3]);
						cl.getY()[4] = Float.parseFloat(posa[4]);
						cl.getY()[5] = Float.parseFloat(posa[5]);
					}
				}catch (Exception e) {}
				break;
			case PACKETS.EXIT:
				// TODO:
				break;
		}
	}
	
	/**
	 * updates a block on the server when it is broken
	 */
	public void updateBlock(int x, int y, int z, short blk) {
		ByteBuffer buff = ByteBuffer.allocate(5 + 4*4 + 2);
		buff.put(PACKETS.BLOCKMOD);
		buff.putInt(id);
		buff.putInt(x);
		buff.putInt(y);
		buff.putInt(z);
		buff.putShort(blk);
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	@Override
	public void run() {
		super.run();
		while (VoxelScreenManager.isOpen) {
			// receive any packets that the server may send
			byte[] recive = new byte[65535]; 
			while (VoxelScreenManager.isOpen) {
				try {
					rp = new DatagramPacket(recive, recive.length);
					ds.receive(rp);
					// handle them
					handlePacket(recive, rp);
					//System.out.println("Server: " + Server.dataToString(recive));
					recive = new byte[65535];
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	/**
	 * converts a byte[] into string
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
    
    /**
     * decodes a chunk
     */
	public Chunk decodeChunk(byte[] bt) {
		short[][][] blocks = new short[Chunk.x][Chunk.y][Chunk.z];
		int xoff = 0;
		int zoff = 0;
		try {
			// same as in the server. decompress the chunk and return it.
			byte[] bytes = new byte[0];
			for (int i = bt.length-1; i > 0; i--) {
				if (bt[i] == PACKETS.CHUNKREQ) {
					bytes = Arrays.copyOfRange(bt, 1, i);
					break;
				}
			}
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
		Chunk c = new Chunk(MasterRenderer.global_loader, world, blocks, xoff, zoff);
		return c;
	}
	
	/**
	 * sends a compressed chunk to the server
	 */
	public void sendCompressedChunk(Chunk c) {
		try {
			// same as server
			// should really use one class for this
			// encodes chunks. pretty simple.
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
			//System.out.println(bos.toByteArray().length);
			sendData(bos.toByteArray());
		} catch (IOException e) {
			Debug.print("error in compressor");
			e.printStackTrace();
		}
	}
	
}
