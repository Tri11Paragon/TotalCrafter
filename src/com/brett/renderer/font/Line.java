package com.brett.renderer.font;

import java.util.ArrayList;
import java.util.List;

public class Line {

	private double maxLength;
	private double spaceSize;

	private List<Word> words = new ArrayList<Word>();
	private double currentLineLength = 0;

	protected Line(double spaceWidth, double fontSizeX, double fontSizeY, double maxLength) {
		this.spaceSize = spaceWidth * fontSizeX;
		this.maxLength = maxLength;
	}

	protected boolean attemptToAddWord(Word word) {
		double additionalLength = word.getWordWidth();
		additionalLength += !words.isEmpty() ? spaceSize : 0;
		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
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
