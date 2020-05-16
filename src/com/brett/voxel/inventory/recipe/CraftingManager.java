package com.brett.voxel.inventory.recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.brett.renderer.datatypes.Tuple;
/**
*
* @author brett
* @date May 14, 2020
*/

public class CraftingManager {
	
	public static final char[] al = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] alu = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	// likely the bigest map you will ever see
	// like this is just stupidly large
	//private static HashMap<Integer, HashMap<Tuple<String, Long>, HashMap<Integer, Character>>> craftingRecipes = new HashMap<Integer, HashMap<Tuple<String, Long>, HashMap<Integer,Character>>>();
	// eh well turns out im not using regex so i guess this smaller map will do.
	// i kept ^ because i thought it was very neat
	
	/*
	 * Stores all the crafting recipes. first int is the # of rows
	 * second int is the output id
	 * the tuple stores the actual recipe along with the output and # of output
	 * first 32 bits is the amount, second 32 bits is the output id
	 */
	private static HashMap<Integer, HashMap<Integer, Tuple<String, Long>>> craftingRecipes = new HashMap<Integer, HashMap<Integer, Tuple<String, Long>>>();
	
	/**
	 * Takes in a recipe in standard format <b>(ex id,id,id;id,id,id;id,id,id)</b>, which would be a crafting TABLE recipe as it has 3 rows. <br>
	 * Each id in a row is separated by a <b>,</b> to denote that this is a different id from the last. <br>
	 * Each column is separated by a <b>;</b> <br>
	 * If you only did id;id then that could be crafted in the inventory or crafting table, and would be 2 items stacked on top of each other. <br>
	 * Note: only air(<b>id 0</b>) is required in crafting table recipes (to insure order). <br>
	 * 
	 * The output is the output id. <br>
	 * And the amount is the amount of material created. <br>
	 */
	public static void registerCrafting(String data, int output, int amount) {
		String[] lines = data.split(";");
		HashMap<Integer, Character> chars = new HashMap<Integer, Character>();
		StringBuilder bild = new StringBuilder();
		buildCharacters(lines, chars, bild);
		long l = 0;
		l = (long)output;
		l |= (((long)amount) << 32);
		HashMap<Integer, Tuple<String, Long>> recipes = craftingRecipes.get(lines.length);
		if (recipes == null)
			recipes = new HashMap<Integer, Tuple<String, Long>>();
		recipes.put(output, new Tuple<String, Long>(data, l));
		craftingRecipes.put(lines.length, recipes);
	}
	
	/*
	 * Returns 0 if the recipe doesn't exist.
	 * Input in standard format.
	 */
	public static long getRecipe(String data) {
		String[] lines = data.split(";");
		HashMap<Integer, Character> chars = new HashMap<Integer, Character>();
		StringBuilder bild = new StringBuilder();
		buildCharacters(lines, chars, bild);
		HashMap<Integer, Tuple<String, Long>> tup = craftingRecipes.get(lines.length);
		if (tup == null)
			return 0;
		Iterator<java.util.Map.Entry<Integer, Tuple<String, Long>>> itr = tup.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, Tuple<String, Long>> entry = itr.next();
			Tuple<String, Long> tuple = entry.getValue();
			String dataToMatch = tuple.getX();
			if (dataToMatch.contentEquals(data)) {
				long undecoded = tuple.getY();
				return undecoded;
			}
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
