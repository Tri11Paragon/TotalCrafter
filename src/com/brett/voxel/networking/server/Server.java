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

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.MasterRenderer;
import com.brett.tools.Debug;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.networking.IReciveEvent;
import com.brett.voxel.networking.PACKETS;
import com.brett.voxel.world.chunk.Chunk;

/**
*
* @author brett
* @date May 31, 2020
*/

public class Server extends Thread {
	
	public static Server server;
	
	public static final int PORT = 1337;
	public List<IReciveEvent> reciveEvent = new ArrayList<IReciveEvent>();
	public List<ConnectedClient> clients = new ArrayList<ConnectedClient>();
	public HashMap<Integer, ConnectedClient> clientMap = new HashMap<Integer, ConnectedClient>();
	public HashMap<String, ConnectedClient> clientMapAddr = new HashMap<String, ConnectedClient>();
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
		
		byte[] recive = new byte[65535]; 
		DatagramPacket recivePacket = null;
		while (VoxelScreenManager.isOpen) {
			recivePacket = new DatagramPacket(recive, recive.length);
			try {
				ds.receive(recivePacket);
			} catch (IOException e) {e.printStackTrace();}
			handlePacket(recive, recivePacket);
			Debug.print("Client: " + dataToString(recive));
			for (int i = 0; i < reciveEvent.size(); i++) {
				reciveEvent.get(i).recieved(recive);
			}
			recive = new byte[65535];
		}
		
	}
	
	public void handlePacket(byte[] bt, DatagramPacket packet) {
		byte[] idbuff = new byte[0];
		ByteBuffer idb;
		int id = 0;
		switch (bt[0]) {
			case PACKETS.LOGIN:
				ConnectedClient cn = new ConnectedClient(packet.getAddress(), packet.getPort(), dataToString(bt).toString().substring(1),
						new Vector3f(0,0,0), lastID);
				clients.add(cn);
				clientMap.put(lastID, cn);
				cn.sendData(ByteBuffer.allocate(5).put(PACKETS.ID).putInt(lastID).array());
				lastID++;
				break;
			case PACKETS.CHUNKREQ:
				byte[] idbuffd = Arrays.copyOfRange(bt, 1, 5);
				byte[] posbuff = Arrays.copyOfRange(bt, 5, bt.length);
				ByteBuffer posb = ByteBuffer.wrap(posbuff);
				ByteBuffer idbb = ByteBuffer.wrap(idbuffd);
				int idd = idbb.getInt();
				ConnectedClient cll = clientMap.get(idd);
				if (cll == null)
					return;
				int x = posb.getInt();
				int z = posb.getInt();
				sworld.queChunk(x, z, cll);
				break;
			case PACKETS.POSSYNC:
				byte[] buff = Arrays.copyOfRange(bt, 5, bt.length);
				idbuff = Arrays.copyOfRange(bt, 1, 5);
				idb = ByteBuffer.wrap(idbuff);
				id = idb.getInt();
				ConnectedClient cl = clientMap.get(id);
				if (cl == null)
					return;
				String pos = dataToString(buff).toString();
				String[] posa = pos.split(";");
				try {
					cl.plypos.x = Float.parseFloat(posa[0]);
					cl.plypos.y = Float.parseFloat(posa[1]);
					cl.plypos.z = Float.parseFloat(posa[2]);
				}catch (Exception e) {}
				break;
			case PACKETS.DISCONNECT:
				byte[] idbuffdd = Arrays.copyOfRange(bt, 1, 5);
				ByteBuffer idbd = ByteBuffer.wrap(idbuffdd);
				int iddd = idbd.getInt();
				clientMap.remove(iddd);
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
				int bx =  blbuff.getInt();
				int by = blbuff.getInt();
				int bz = blbuff.getInt();
				short blk = blbuff.getShort();
				
				sworld.setBlock(bx, by, bz, blk);
				
				ByteBuffer blmodbuff = ByteBuffer.allocate(blbuff.capacity()+1);
				blmodbuff.put(PACKETS.BLOCKMOD);
				blmodbuff.put(blbuff.array());
				
				for (int i = 0; i < clients.size(); i++) {
					ConnectedClient ccl = clients.get(i);
					if (ccl.id != id) {
						ccl.sendData(blmodbuff.array());
					}
				}
				
				break;
		}
	}
	
	public void sendDataToAllClients(byte[] buff) {
		for (int i = 0; i < clients.size(); i++) {
			ConnectedClient cc = clients.get(i);
			cc.sendData(buff);
		}
	}
	
	public void sendCompressedChunk(Chunk c, ConnectedClient cc) {
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
			cc.sendData(bos.toByteArray());
		} catch (IOException e) {
			Debug.print("error in compressor");
			e.printStackTrace();
		}
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
		Chunk c = new Chunk(MasterRenderer.global_loader, sworld, blocks, xoff, zoff);
		return c;
	}
	
    public static StringBuilder dataToString(byte[] a) 
    { 
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