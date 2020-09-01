package com.brett.engine.ui.console;

import java.util.HashMap;

/**
* @author Brett
* @date 20-Aug-2020
*/

public class KeyMap {
	
	private HashMap<Character, Character> lowerToUpper = new HashMap<Character, Character>();
	
	public KeyMap() {
		
	}
	
	public KeyMap(char[] lowercase, char[] uppercase) {
		// populates the conversion map, adds lower case if lower case's length is larger then upper case
		for (int i = 0; i < lowercase.length; i++) {
			if (i >= uppercase.length) {
				lowerToUpper.put(lowercase[i], lowercase[i]);
				return;
			}
			lowerToUpper.put(lowercase[i], uppercase[i]);
		}
	}
	
	public void addCaseConversion(char lc, char uc) {
		lowerToUpper.put(lc, uc);
	}
	
	/**
	 * returns the upper case character for a given lower case character. Returns the lower case character if there is no 
	 * upper case character
	 */
	public char getUpperCase(char lc) {
		Character ctm = lowerToUpper.get(lc);
		return ctm == null ? lc : ctm;
	}
	
}
