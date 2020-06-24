package com.brett.engine.ui.font;

import java.io.Serializable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.engine.managers.ScreenManager;

/**
 * 
 * @author brett
 * Holds the information about the text
 *
 */
public class UIText implements Serializable {

	private static final long serialVersionUID = 463395900565370628L;
	protected String textString;
	private float fontSizeX;
	private float fontSizeY;

	private int textMeshVao;
	private int vertexCount;
	// inner color of the text
	private Vector3f color = new Vector3f(0f, 0f, 0f);
	// outline color of the text
	private Vector3f outlineColor = new Vector3f(1f, 1f, 1f);

	protected Vector2f position;
	// this should be in some kind of NDC, starting at 0 I think
	private float lineMaxSize;
	private int numberOfLines;
	private float height = 0;
	
	// how many lines will this text allow
	private int maxNumberOfLines = Integer.MAX_VALUE;

	private String font;

	private boolean atMax = false;
	private boolean centerText = false;

	public UIText(String text, float fontSize, String font, Vector2f position, float maxLineLength, boolean centered) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
	}
	
	public UIText(String text, float fontSize, String font, Vector2f position, float maxLineLength, boolean centered, int maxNumberOfLines) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		this.maxNumberOfLines = maxNumberOfLines;
		//TextMaster.loadText(this);
	}
	
	public UIText(String text, float fontSizeX, float fontSizeY, String font, Vector2f position, float maxLineLength, boolean centered, int maxNumberOfLines) {
		this.textString = text;
		this.fontSizeX = fontSizeX;
		this.fontSizeY = fontSizeY;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		this.maxNumberOfLines = maxNumberOfLines;
		//TextMaster.loadText(this);
	}

	/*
	 * Getters and setters
	 */
	
	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public boolean isAtMax() {
		return atMax;
	}

	public void setAtMax(boolean atMax) {
		this.atMax = atMax;
	}
	
	public void setText(String text) {
		this.textString = text;
	}
	
	public String getText() {
		return this.textString;
	}
	
	public String getFont() {
		return font;
	}

	public UIText setColor(float r, float g, float b) {
		color.set(r, g, b);
		return this;
	}

	public Vector3f getColor() {
		return color;
	}
	
	public UIText setColorOutline(float r, float g, float b) {
		outlineColor.set(r, g, b);
		return this;
	}

	public Vector3f getColorOutline() {
		return outlineColor;
	}
	
	public int getMaxNumberOfLines() {
		return maxNumberOfLines;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public Vector2f getPosition() {
		return position;
	}

	public int getMesh() {
		return textMeshVao;
	}

	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	protected float getFontSizeX() {
		return fontSizeX;
	}
	
	protected float getFontSizeY() {
		return fontSizeY;
	}

	protected UIText setNumberOfLines(int number) {
		this.numberOfLines = number;
		return this;
	}

	protected boolean isCentered() {
		return centerText;
	}

	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	protected String getTextString() {
		return textString;
	}
	
	public static UIText updateTextMesh(UIText text){
		// load the text data
		TextMeshData data = ScreenManager.fonts.get(text.font).loadText(text);
		// get a vao from the text mesh data
		int vao = ScreenManager.loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords(), 2).getVaoID();
		// update the text with the mesh
		text.setMeshInfo(vao, data.getVertexCount());
		return text;
	}

}
