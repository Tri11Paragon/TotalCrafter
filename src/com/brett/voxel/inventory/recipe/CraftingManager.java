package com.brett.voxel.inventory.recipe;

import java.util.HashMap;
import com.brett.renderer.datatypes.Tuple;

/**
*
* @author brett
* @date May 14, 2020
*/

public class CraftingManager {
	
	public static final char[] al = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] alu = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	//private static HashMap<Integer, HashMap<Tuple<String, Long>, HashMap<Integer, Character>>> craftingRecipes = new HashMap<Integer, HashMap<Tuple<String, Long>, HashMap<Integer,Character>>>();
	private static HashMap<Integer, Tuple<String, Long>> craftingRecipes = new HashMap<Integer, Tuple<String, Long>>();
	
	/**
	 * 
	 */
	public static void registerCrafting(String data, int output, int amount) {
		String[] lines = data.split(";");
		HashMap<Integer, Character> chars = new HashMap<Integer, Character>();
		StringBuilder bild = new StringBuilder();
		buildCharacters(lines, chars, bild);
		//HashMap<Tuple<String, Long>, HashMap<Integer, Character>> sizes = craftingRecipes.get(lines.length);
		//if (sizes == null)
		//	sizes = new HashMap<Tuple<String, Long>, HashMap<Integer, Character>>();
		long l = 0;
		l = (long)output;
		l |= (((long)amount) << 32);
		craftingRecipes.put(lines.length, new Tuple<String, Long>(data, l));
	}
	
	public static long getRecipe(String data) {
		String[] lines = data.split(";");
		HashMap<Integer, Character> chars = new HashMap<Integer, Character>();
		StringBuilder bild = new StringBuilder();
		buildCharacters(lines, chars, bild);
		Tuple<String, Long> tup = craftingRecipes.get(lines.length);
		if (tup == null)
			return 0;
		String dataToMatch = tup.getX();
		if (dataToMatch.contentEquals(data)) {
			long undecoded = tup.getY();
			return undecoded;
		}
		return 0;
	}
	
	private static StringBuilder buildCharacters(String[] lines, HashMap<Integer, Character> chars, StringBuilder bild) {
		int used = 0;
		for (String s : lines) {
			String[] id = s.split(",");
			for (String sid : id) {
				int iid = Integer.parseInt(sid);
				Character ch = chars.get(iid);
				if (ch == null) {
					ch = al[used];
					used++;
					chars.put(iid, ch);
				}
				bild.append(ch);
			}
		}
		return bild;
	}
	
}
