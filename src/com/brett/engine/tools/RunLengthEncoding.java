package com.brett.engine.tools;

import java.util.ArrayList;
import java.util.List;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class RunLengthEncoding {
	
	public static byte[] encode(byte[] array) {
		int i = 0;
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		while (i < array.length) {
			byte type = array[i];
			byte count = 0;
			for (int j = i; j < array.length; j++) {
				if (array[j] != type)
					break;
				if (count >= Byte.MAX_VALUE)
					break;
				i = j+1;
				count++;
			}
			bytes.add(type);
			bytes.add(count);
		}
		return fromList(bytes);
	}
	
	public static short[] encode(short[] array) {
		int i = 0;
		ArrayList<Short> bytes = new ArrayList<Short>();
		while (i < array.length) {
			short type = array[i];
			short count = 0;
			for (int j = i; j < array.length; j++) {
				if (array[j] != type)
					break;
				if (count >= Short.MAX_VALUE)
					break;
				i = j+1;
				count++;
			}
			bytes.add(type);
			bytes.add(count);
		}
		return fromList(bytes);
	}
	
	public static short[] encodeDouble(short[] array) {
		int i = 0;
		ArrayList<Short> bytes = new ArrayList<Short>();
		while (i < array.length) {
			short type = array[i];
			short count = 0;
			for (int j = i; j < array.length; j++) {
				if (array[j] != type)
					break;
				if (count >= Short.MAX_VALUE)
					break;
				i = j+1;
				count++;
			}
			bytes.add(type);
			bytes.add(count);
		}
		short[] doubled = fromList(bytes);
		ArrayList<Short> shorts = new ArrayList<Short>();
		for (int s = 0; s < doubled.length; s+=8) {
			try {
				int index1 = s;
				int index2 = s+1;
				
				int index3 = s+2;
				int index4 = s+3;
				
				int index5 = s+4;
				int index6 = s+5;
				
				int index7 = s+6;
				int index8 = s+7;
				if (doubled[index1] == doubled[index5] && doubled[index2] == doubled[index6] && doubled[index2] * 2 <= Short.MAX_VALUE) {
					shorts.add(doubled[index1]);
					shorts.add((short)(doubled[index2]*2));
					if (doubled[index3] == doubled[index7] && doubled[index4] == doubled[index8] && doubled[index4] * 2 <= Short.MAX_VALUE) {
						shorts.add(doubled[index3]);
						shorts.add((short)(doubled[index4]*2));
					} else {
						shorts.add(doubled[index3]);
						shorts.add(doubled[index4]);
						shorts.add(doubled[index7]);
						shorts.add(doubled[index8]);
					}
				} else {
					shorts.add(doubled[index1]);
					shorts.add(doubled[index2]);
					shorts.add(doubled[index5]);
					shorts.add(doubled[index6]);
				}
			} catch (Exception e) {
				for (int d = s; d < doubled.length; d++) {
					shorts.add(doubled[d]);
				}
				return fromList(shorts);
			}
		}
		if (shorts.size() < doubled.length)
			return fromList(shorts);
		return doubled;
	}
	
	public static byte[] fromList(List<Byte> list) {
		byte[] trt = new byte[list.size()];
		for (int i = 0; i < trt.length; i++)
			trt[i]=list.get(i);
		return trt;
	}
	
	public static short[] fromList(ArrayList<Short> list) {
		short[] trt = new short[list.size()];
		for (int i = 0; i < trt.length; i++)
			trt[i]=list.get(i);
		return trt;
	}
	
}
