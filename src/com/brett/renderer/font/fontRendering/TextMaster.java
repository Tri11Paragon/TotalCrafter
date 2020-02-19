package com.brett.renderer.font.fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.renderer.Loader;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.GUIText;
import com.brett.renderer.font.TextMeshData;

public class TextMaster {
	
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static void init(Loader theLoader){
		renderer = new FontRenderer();
		loader = theLoader;
	}
	
	/**
	 * Use this to get the renderer then call the required draw calls if you are using
	 * dynamic text.
	 * @return font renderer instance
	 */
	public static FontRenderer getRenderer() {
		return renderer;
	}
	
	public static void render(){
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	/**
	 * Do not use this. please incorporate the font renderer if this is really needed.
	 */
	@Deprecated
	public static void renderSingle(List<GUIText> texts, FontType font) {
		renderer.renderBAD(texts, font);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if(textBatch.isEmpty()){
			texts.remove(texts.get(text.getFont()));
		}
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}

}
