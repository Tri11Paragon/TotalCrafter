package com.brett.engine;

import java.util.ArrayList;
import java.util.List;

/**
* @author Brett
* @date Jun. 25, 2020
*/

public class Utils {
	
	public static boolean isSelected = false;
	
	public static List<Character> illegalCharacters = new ArrayList<Character>();
	
	public static float[] listToArrayF(List<Float> list) {
		float[] floats = new float[list.size()];
		for (int i = 0; i < list.size(); i++)
			floats[i] = list.get(i);
		return floats;
	}
	
	public static int[] listToArrayI(List<Integer> list) {
		int[] floats = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			floats[i] = list.get(i);
		return floats;
	}
	
}
