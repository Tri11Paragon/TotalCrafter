package com.brett.world.chunks.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Brett
* @date 15-Jun-2021
*/

public class BlockPalette implements Serializable {

	private byte highest = Byte.MIN_VALUE;
	private static final long serialVersionUID = 5829920672774508546L;
	private HashMap<Integer, Byte> palette = new HashMap<Integer, Byte>();
	private HashMap<Byte, Integer> revPalette = new HashMap<Byte, Integer>();
	private ArrayList<Byte> taken = new ArrayList<Byte>();
	
	public byte checkAndAssign(int id) {
		if (palette.containsKey(id))
			return palette.get(id);
		if (highest >= Byte.MAX_VALUE) {
			for (int i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; i++) {
				boolean found = false;
				for (int j = 0; j < taken.size(); j++) {
					if (i == taken.get(j)) {
						found = true;
						break;
					}
				}
				if (!found) {
					byte b = (byte) i;
					palette.put(id, b);
					revPalette.put(b, id);
					taken.add(b);
					return b;
				}
			}
			// should never reach this point.
			throw new RuntimeException("issues assigning values in the block palette");
		} else {
			palette.put(id, highest);
			revPalette.put(highest, id);
			taken.add(highest);
			highest++;
			return (byte) (highest - 1);
		}
	}
	
	public byte get(int id) {
		return palette.get(id);
	}
	
	public int get(byte id) {
		return revPalette.get(id);
	}
	
	public void remove(int id) {
		taken.remove(palette.get(id));
		revPalette.remove(palette.get(id));
		palette.remove(id);	
	}
	
}
