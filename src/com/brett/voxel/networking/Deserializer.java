package com.brett.voxel.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.brett.voxel.world.IWorldProvider;
import com.brett.voxel.world.tileentity.TileEntity;

/**
*
* @author brett
* @date Jun. 15, 2020
*/

public class Deserializer {
	
	public static byte[] tileToBytes(TileEntity e, int id) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// write the info packet
		out.write(PACKETS.TILESEND);
		ByteBuffer bu = ByteBuffer.allocate(4);
		bu.putInt(id);
		try {
			// put the client id
			out.write(bu.array());
			// compress the entity object
			GZIPOutputStream gos = new GZIPOutputStream(out);
			ObjectOutputStream os = new ObjectOutputStream(gos);
			// write the object
			os.writeObject(e);
			// flush the stream
			os.flush();
			os.close();
			out.write(PACKETS.TILESEND);
		} catch (Exception err) {err.printStackTrace();}
		return out.toByteArray();
	}
	
	/**
	 * converts a byte[] into a tile entity
	 * first 5 bytes are for id.
	 */
	public static TileEntity bytesToTile(byte[] bytes, IWorldProvider world) {
		byte[] bytcpy = new byte[0];
		for (int i = bytes.length-1; i > 0; i--) {
			if (bytes[i] == PACKETS.TILESEND) {
				// copy only the important data
				bytcpy = Arrays.copyOfRange(bytes, 5, i);
				break;
			}
		}
		TileEntity e = new TileEntity();
		e.setWorld(world);
		ByteArrayInputStream in = new ByteArrayInputStream(bytcpy);
		try {
			// decompress the input stream
			GZIPInputStream gin = new GZIPInputStream(in);
			// read the object
			ObjectInputStream oin = new ObjectInputStream(gin);
			e = (TileEntity) oin.readObject();
			oin.close();
		} catch (Exception ed) {ed.printStackTrace();}
		// return it
		return e;
	}
	
}
