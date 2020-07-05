package com.brett.engine.ui.font;

import java.io.Serializable;
import org.joml.Vector3f;

import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.ui.AnchorPoint;
import com.brett.engine.ui.RescaleEvent;

/**
 * 
 * @author brett
 * Holds the information about the text
 *
 */
public class UIText implements Serializable, RescaleEvent {

	private static final long serialVersionUID = 463395900565370628L;
	protected String textString;
	public float fontSizeX;
	public float fontSizeY;

	private int textMeshVao;
	private int vertexCount;
	// inner color of the text
	public Vector3f color = new Vector3f(0f, 0f, 0f);
	// outline color of the text
	public Vector3f outlineColor = new Vector3f(1f, 1f, 1f);

	// this should be in some kind of NDC, starting at 0 I think
	private float lineMaxSize;
	private int numberOfLines;
	private float height = 0;
	public double maxWidth, maxHeight;
	
	// how many lines will this text allow
	private int maxNumberOfLines = Integer.MAX_VALUE;

	private String font;

	private float x,y;
	public float rx, ry, sx, sy;
	public float minx,miny,maxx,maxy;
	
	private boolean atMax = false;
	private boolean centerText = false;

	public AnchorPoint anchorPoint = AnchorPoint.TOPLEFT;
	
	public UIText(String text, float fontSize, String font, float x, float y, float maxLineLength) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = 1.5f;
		this.sy = 1.0f;
	}
	
	public UIText(String text, float fontSize, String font, float x, float y, float maxLineLength, int maxNumberOfLines) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.maxNumberOfLines = maxNumberOfLines;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = 1.5f;
		this.sy = 1.0f;
	}
	
	public UIText(String text, float fontSize, String font, float x, float y, float sx, float sy, float maxLineLength, int maxNumberOfLines) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.maxNumberOfLines = maxNumberOfLines;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = sx;
		this.sy = sy;
	}
	
	public UIText(String text, float fontSize, String font, float x, float y, float maxLineLength, AnchorPoint anchorPoint) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = 1.5f;
		this.sy = 1.0f;
		this.anchorPoint = anchorPoint;
		DisplayManager.rescales.add(this);
		rescale();
	}
	
	public UIText(String text, float fontSize, String font, float x, float y, float maxLineLength, int maxNumberOfLines, AnchorPoint anchorPoint) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.maxNumberOfLines = maxNumberOfLines;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = 1.5f;
		this.sy = 1.0f;
		this.anchorPoint = anchorPoint;
		DisplayManager.rescales.add(this);
		rescale();
	}
	
	public UIText(String text, float fontSize, String font, float x, float y, float sx, float sy, float maxLineLength, int maxNumberOfLines, AnchorPoint anchorPoint) {
		this.textString = text;
		this.fontSizeX = fontSize;
		this.fontSizeY = fontSize;
		this.font = font;
		this.lineMaxSize = maxLineLength;
		this.centerText = false;
		this.maxNumberOfLines = maxNumberOfLines;
		this.x = x;
		this.y = y;
		this.rx = x;
		this.ry = y;
		this.sx = sx;
		this.sy = sy;
		this.anchorPoint = anchorPoint;
		DisplayManager.rescales.add(this);
		rescale();
	}
	
	public UIText setBoundingBox(float minx, float miny, float maxx, float maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
		return this;
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
	
	public void changeText(String text) {
		this.textString = text;
		updateTextMesh(this);
	}
	
	public void destroy() {
		if (anchorPoint != AnchorPoint.TOPLEFT)
			DisplayManager.rescales.remove(this);
	}
	
	public static UIText updateTextMesh(UIText text){
		// load the text data
		TextMeshData data = ScreenManager.fonts.get(text.font).loadText(text);
		ScreenManager.loader.deleteVAO(text.getMesh());
		// get a vao from the text mesh data
		int vao = ScreenManager.loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords(), 2).getVaoID();
		// update the text with the mesh
		text.setMeshInfo(vao, data.getVertexCount());
		
		text.maxHeight = data.maxHeight;
		text.maxWidth = data.totalWidth;
		
		return text;
	}

	@Override
	public void rescale() {
		switch (anchorPoint) {
			case CENTER:
				rx = DisplayManager.WIDTH/2-sx/2 + x;
				ry = DisplayManager.HEIGHT/2-sy/2 + y;
				break;
			case BOTTOMCENTER:
				rx = DisplayManager.WIDTH/2-sx/2 + x;
				ry = DisplayManager.HEIGHT-sy + y;
				break;
			case TOPCENTER:
				rx = DisplayManager.WIDTH/2-sx/2 + x;
				ry = y;
				break;
			case LEFTCENTER:
				rx = x;
				ry = DisplayManager.HEIGHT/2-sy/2 + y;
				break;
			case RIGHTCENTER:
				rx = DisplayManager.WIDTH-sx + x;
				ry = DisplayManager.HEIGHT/2-sy/2 + y;
				break;
			case TOPRIGHT:
				rx = DisplayManager.WIDTH-sx + x;
				break;
			case BOTTOMLEFT:
				ry = DisplayManager.HEIGHT-sy + y;
				break;
			case BOTTOMRIGHT:
				rx = DisplayManager.WIDTH-sx + x;
				ry = DisplayManager.HEIGHT-sy + y;
				break;
			default:
				break;
		}
	}

}
