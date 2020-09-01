package com.brett.engine.ui.font;

import java.util.ArrayList;
import java.util.List;

/**
 * line data type
 */
public class Line {

	private double maxLength;
	private double spaceSize;
	private double lineHeight;

	private List<Word> words = new ArrayList<Word>();
	private double currentLineLength = 0;

	protected Line(double spaceWidth, double fontSizeX, double fontSizeY, double maxLength) {
		this.spaceSize = spaceWidth * fontSizeX;
		this.maxLength = maxLength;
	}

	/**
	 * tries to add a word to the line
	 * returns false if word can't be added
	 */
	protected boolean attemptToAddWord(Word word) {
		double additionalLength = word.getWordWidth();
		additionalLength += !words.isEmpty() ? spaceSize : 0;
		// makes sure we don't go above the max length
		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
	}
	
	public double getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(double lineHeight) {
		this.lineHeight = lineHeight;
	}

	protected double getMaxLength() {
		return maxLength;
	}

	protected double getLineLength() {
		return currentLineLength;
	}

	protected List<Word> getWords() {
		return words;
	}

}
