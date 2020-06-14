package com.brett.renderer.font.fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brett.renderer.Loader;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.UIText;
import com.brett.renderer.font.TextMeshData;

/**
 * 
 * @author brett 
 * handles static text rendering. Not the best as texts are not ordered in the UI
 * but I really don't feel like doing it properly at this time
 * mc4 will use a better font system.
 * 
 */
public class StaticText {
	
	private static Loader loader;
	private static Map<FontType, List<UIText>> textMap = new HashMap<FontType, List<UIText>>();
	public static FontRenderer renderer;
	
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
		renderer.render(textMap);
	}
	
	/**
	 * loads a text to be rendered
	 */
	public static void loadText(UIText text){
		FontType font = text.getFont();
		// load the text data
		TextMeshData data = font.loadText(text);
		// get a vao from the text mesh data
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords(), 2).getVaoID();
		// update the text with the mesh
		text.setMeshInfo(vao, data.getVertexCount());
		// add it to the batched renderer
		List<UIText> textBatch = textMap.get(font);
		if(textBatch == null){
			// create a new batch if it doesn't exist
			textBatch = new ArrayList<UIText>();
			textMap.put(font, textBatch);
		}
		textBatch.add(text);
	}
	
	/**
	 * removes the text from the batched renderer
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static void removeText(UIText text){
		List<UIText> batch = textMap.get(text.getFont());
		if (batch == null)
			return;
		batch.remove(text);
		// make sure we don't have empty batchs in the map
		if(batch.isEmpty()){
			textMap.remove(textMap.get(text.getFont()));
		}
	}
	
	public static void cleanUp(){
		renderer.cleanUp();
	}

}
