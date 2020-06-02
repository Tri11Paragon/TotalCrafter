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
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.MasterRenderer;
import com.brett.tools.Debug;
import com.brett.voxel.VoxelScreenManager;
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
	
	public DatagramSocket ds;
	// the receive packet.
	public DatagramPacket rp;
	public InetAddress ipadd = null;
	public List<IReciveEvent> reciveEvents = new ArrayList<IReciveEvent>(); 
	public int id = 0;
	public VoxelWorld world;
	public boolean connected = false;
	
	
	public Client(String ip, String username) {
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
		char[] chars = bu.toString().toCharArray();
		ByteBuffer buff = ByteBuffer.allocate(5 + chars.length*2);
		buff.put(PACKETS.POSSYNC);
		buff.putInt(id);
		for (int i = 0; i < chars.length; i++) {
			buff.putChar(chars[i]);
		}
		DatagramPacket sp = new DatagramPacket(buff.array(), buff.array().length, ipadd, Server.PORT);
		try {
			ds.send(sp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void handlePacket(byte[] bt, DatagramPacket packet) {
		switch (bt[0]) {
			case PACKETS.ID:
				ByteBuffer wrapped = ByteBuffer.wrap(new byte[] {bt[1], bt[2], bt[3], bt[4]});
				this.id = wrapped.getInt();
				connected = true;
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
		}
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
	
}
