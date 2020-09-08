package com.brett.utils;

import java.util.ArrayList;

/**
* @author Brett
* @date Sep 6, 2020
*/

public class RunLengthEncoding {
	
	public static ArrayList<Short> encode_chunk(short[][][] blocks) {
		int i = 0;
		ArrayList<Short> shorts = new ArrayList<Short>();
		
		while (i < 4096) {
			int col1index1 = i % 16;
			int col2index1 = i / 16 % 16;
			int col3index1 = i / 16 / 16;
			short type = blocks[col1index1][col2index1][col3index1];
			short count = 0;
			for (int j = i; j < 4096; j++) {
				int col1index2 = j % 16;
				int col2index2 = j / 16 % 16;
				int col3index2 = j / 16 / 16;
				short type2 = blocks[col1index2][col2index2][col3index2];
				if (type != type2)
					break;
				i = j + 1;
				count++;
			}
			shorts.add(type);
			shorts.add(count);
		}
		
		return shorts;
	}
	
	public static short[][][] decode_chunk(ArrayList<Short> shorts) {
		short[][][] blocks = new short[16][16][16];
		
		for (int i = 0; i < shorts.size(); i+=2) {
			int index1 = i;
			int index2 = i+1;
			for (int j = 0; j < shorts.get(index2); j++) {
				int col1 = j % 16;
				int col2 = j / 16 % 16;
				int col3 = j / 16 / 16;
				blocks[col1][col2][col3] = shorts.get(index1);
			}
		}
		
		return blocks;
	}
	
	public static short[] fromList(ArrayList<Short> list) {
		short[] trt = new short[list.size()];
		for (int i = 0; i < trt.length; i++)
			trt[i] = list.get(i);
		return trt;
	}
	
}
