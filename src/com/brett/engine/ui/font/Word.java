package com.brett.engine.ui.font;

import java.util.ArrayList;
import java.util.List;

/**
 * holds a single word.
 *
 */
public class Word {
	
	private List<Character> characters = new ArrayList<Character>();
	private double width = 0;
	private double fontSize;
	
	protected Word(double fontSizeX){
		this.fontSize = fontSizeX;
	}
	
	protected void addCharacter(Character character){
		characters.add(character);
		width += character.getxAdvance() * fontSize;
	}
	
	protected List<Character> getCharacters(){
		return characters;
	}
	
	protected double getWordWidth(){
		return width;
	}

}
