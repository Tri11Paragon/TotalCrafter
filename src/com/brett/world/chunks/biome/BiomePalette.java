package com.brett.world.chunks.biome;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import com.brett.world.GameRegistry;

import java.util.Set;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class BiomePalette {
	
	private byte highest = 0;
	public final HashMap<Byte, Biome> biomes = new HashMap<Byte, Biome>();
	
	public BiomePalette() {
		
	}
	
	public BiomePalette(byte[] ids, Biome[] b) {
		if (ids.length != b.length)
			throw new RuntimeException("id array isn't equal to b array. please ensure that each id has a biome");
		for (int i = 0; i < ids.length; i++) {
			if (biomes.get(ids[i]) == null)
			biomes.put(ids[i], b[i]);
			else {
				biomes.remove(ids[i]);
				biomes.put(ids[i], b[i]);
			}
		}
	}

	public byte assign(Biome b) {
		biomes.put(highest, b);
		highest++;
		return (byte) (highest-1);
	}
	
	public void save(DataOutputStream dos) {
		try {
			Set<Entry<Byte, Biome>> i = biomes.entrySet();
			dos.writeInt(i.size());
 			for (Entry<Byte, Biome> e : i) {
				dos.writeByte(e.getKey());
				dos.writeInt(e.getValue().getId());
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void load(DataInputStream dis) {
		try {
			int size = dis.read();
			for (int i = 0; i < size; i++) {
				byte b = dis.readByte();
				int id = dis.readInt();
				biomes.put(b, GameRegistry.getBiomeById(id));
				highest = (byte) Math.max(b, highest);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void assignBiome(byte id, Biome b) {
		if (biomes.get(id) == null)
			biomes.put(id, b);
		else {
			biomes.remove(id);
			biomes.put(id, b);
		}
	}
	
	
}
