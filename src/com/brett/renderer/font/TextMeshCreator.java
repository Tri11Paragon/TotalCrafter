package com.brett.renderer.font;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author unknown
 *
 * I didn't make it, its pretty neat but if I remade mc3 I wouldn't use it. 
 * I did make changes to it so that way it can handle having a max height and length
 * and a max number of lines but other then that I didn't make this class.
 *
 */

public class TextMeshCreator {

	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private MetaFile metaData;

	protected TextMeshCreator(File metaFile) {
		metaData = new MetaFile(metaFile);
	}

	protected TextMeshData createTextMesh(UIText text) {
		List<Line> lines = createStructure(text);
		TextMeshData data = createQuadVertices(text, lines);
		return data;
	}

	@SuppressWarnings("unused")
	private double totalWidth = 0;
	private float maxHeight = 0;
	
	private List<Line> createStructure(UIText text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSizeX(), text.getFontSizeY(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSizeX());
		for (char c : chars) {
			int ascii = (int) c;
			if (ascii == 10) {
				// this is what I added
				if (lines.size() < text.getMaxNumberOfLines()) {
					currentLine.attemptToAddWord(currentWord);
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSizeX(), text.getFontSizeY(), text.getMaxLineSize()); 
					text.setHeight(text.getHeight() +  maxHeight);
					maxHeight = 0;
					currentWord = new Word(text.getFontSizeX());
				} else
					text.setAtMax(true);
			}
			if (metaData.getCharacter(ascii) != null) {
				totalWidth += metaData.getCharacter(ascii).getActualWidth();
				maxHeight = (float) Math.max(maxHeight, metaData.getCharacter(ascii).getSizeY());
			}
			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					// this too
					if (lines.size() < text.getMaxNumberOfLines()) {
						lines.add(currentLine);
						currentLine = new Line(metaData.getSpaceWidth(), text.getFontSizeX(), text.getFontSizeY(), text.getMaxLineSize());
						text.setHeight(text.getHeight() +  maxHeight);
						maxHeight = 0;
						currentLine.attemptToAddWord(currentWord);
					} else
						text.setAtMax(true);
				}
				currentWord = new Word(text.getFontSizeX());
				continue;
			}
			Character character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, UIText text) {
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSizeX(), text.getFontSizeY(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(UIText text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0f;
		double curserY = 0f;
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		for (Line line : lines) {
			if (text.isCentered()) {
				curserX = (line.getMaxLength() - line.getLineLength()) / 2;
			}
			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSizeX(), text.getFontSizeY(), vertices);
					addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					curserX += letter.getxAdvance() * text.getFontSizeX();
				}
				curserX += metaData.getSpaceWidth() * text.getFontSizeX();
			}
			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSizeY();
		}		
		return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
	}
	
	private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSizeX, double fontSizeY,
			List<Float> vertices) {
		double x = curserX + (character.getxOffset() * fontSizeX);
		double y = curserY + (character.getyOffset() * fontSizeY);
		double maxX = x + (character.getSizeX() * fontSizeX);
		double maxY = y + (character.getSizeY() * fontSizeY);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}


	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}

	
	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

}
