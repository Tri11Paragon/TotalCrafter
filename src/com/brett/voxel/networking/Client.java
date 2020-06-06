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

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.MasterRenderer;
import com.brett.renderer.datatypes.Tuple;
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
*/

public class Client extends Thread {
	
	public HashMap<Integer, Tuple<float[], float[]>> clients = new HashMap<Integer, Tuple<float[], float[]>>();
	public List<Inventory> inventories = new ArrayList<Inventory>();
	
	public DatagramSocket ds;
	// the receive packet.
	public DatagramPacket rp;
	public InetAddress ipadd = null;
	public List<IReciveEvent> reciveEvents = new ArrayList<IReciveEvent>(); 
	public int id = 0;
	public VoxelWorld world;
	public boolean connected = false;
	
	
	public Client(String ip, String username, VoxelWorld world) {
		this.world = world;
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
		byte[] user = username.getBytes();
		byte[] login = new byte[user.length+1];
		login[0] = PACKETS.LOGIN;
		for (int i = 0; i < user.length; i++) {
			login[i+1] = user[i];
		}
		sendData(login);
	}
	
	public void sendData(byte[] buff) {
		DatagramPacket sp = new DatagramPacket(buff, buff.length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
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
	
	public void disconnect() {
		ByteBuffer buff = ByteBuffer.allocate(5);
		buff.put(PACKETS.DISCONNECT);
		buff.putInt(id);
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void updatePosition(Player ply) {
		StringBuilder bu = new StringBuilder();
		Vector3f pos = ply.getPosition();
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
		buff.putInt(id);
		for (int i = 0; i < chars.length; i++) {
			buff.put(chars[i]);
		}
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void handlePacket(byte[] bt, DatagramPacket packet) {
		byte[] idbuff = new byte[0];
		byte[] buff = new byte[0];
		ByteBuffer idb;
		int id = 0;
		switch (bt[0]) {
			case PACKETS.ID:
				ByteBuffer wrapped = ByteBuffer.wrap(new byte[] {bt[1], bt[2], bt[3], bt[4]});
				this.id = wrapped.getInt();
				connected = true;
				
				buff = Arrays.copyOfRange(bt, 5, bt.length);
				
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
				Chunk c = decodeChunk(bt);
				world.chunk.insertChunk(c);
				break;
			case PACKETS.BLOCKMOD:
				ByteBuffer blbuff = ByteBuffer.wrap(Arrays.copyOfRange(bt, 1, bt.length));
				int bx =  blbuff.getInt();
				int by = blbuff.getInt();
				int bz = blbuff.getInt();
				short blk = blbuff.getShort();
				
				world.chunk.setBlockServer(bx, by, bz, blk);
				
				break;
			case PACKETS.LOGIN:
				byte[] idbuffd = Arrays.copyOfRange(bt, 1, 5);
				ByteBuffer idbb = ByteBuffer.wrap(idbuffd);
				int idd = idbb.getInt();
				clients.put(idd, new Tuple<float[], float[]>(new float[6], new float[6]));
				break;
			case PACKETS.DISCONNECT:
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				if (clients.containsKey(id))
					clients.remove(id);
				break;
			case PACKETS.POSSYNC:
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
				
				break;
			case PACKETS.INVENTORYSEND:
				int p = 0;
				for (int i = 0; i < bt.length; i++) {
					if (bt[i] == Byte.MIN_VALUE)
						p = i;
				}
				String inv = dataToString(Arrays.copyOfRange(bt, p+1, p + 3000)).toString().trim();
				for (int i = 0; i < inventories.size(); i++) {
					if (inventories.get(i).NBTID.contentEquals(inv)) {
						inventories.get(i).deserialize(bt);
					}
				}
				break;
		}
	}
	
	public void sendInventory(Inventory i) {
		sendData(i.serialize());
	}
	
	public void requestInventory(String NBTID) {
		byte[] byt = NBTID.getBytes();
		ByteBuffer buff = ByteBuffer.allocate(5 + byt.length);
		buff.put(PACKETS.INVENTORYREQ);
		buff.putInt(id);
		for (int i = 0; i < byt.length; i++)
			buff.put(byt[i]);
		sendData(buff.array());
	}
	
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
			byte[] recive = new byte[65535]; 
			while (VoxelScreenManager.isOpen) {
				try {
					rp = new DatagramPacket(recive, recive.length);
					ds.receive(rp);
					handlePacket(recive, rp);
					//System.out.println("Server: " + Server.dataToString(recive));
					for (int i = 0; i < reciveEvents.size(); i++) {
						reciveEvents.get(i).recieved(recive);
					}
					recive = new byte[65535];
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
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
    
	public Chunk decodeChunk(byte[] bt) {
		short[][][] blocks = new short[Chunk.x][Chunk.y][Chunk.z];
		int xoff = 0;
		int zoff = 0;
		try {
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
	
	public void sendCompressedChunk(Chunk c) {
		try {
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
