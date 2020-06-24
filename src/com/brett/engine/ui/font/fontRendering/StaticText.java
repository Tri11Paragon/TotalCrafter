package com.brett.engine.ui.font.fontRendering;

import com.brett.engine.Loader;

/**
 * 
 * @author brett 
 * handles static text rendering. Not the best as texts are not ordered in the UI
 * but I really don't feel like doing it properly at this time
 * mc4 will use a better font system.
 * 
 */
public class StaticText {
	
	public static void init(Loader theLoader){
	}
	
	/**
	 * loads a text to be rendered
	 */
	/*public static void loadText(UIText text){
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
	}*/

}
