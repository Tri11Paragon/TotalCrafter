package com.brett.renderer.font.fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import com.brett.renderer.font.FontType;
import com.brett.renderer.font.GUIDynamicText;
import com.brett.renderer.font.GUIText;

public class FontRenderer {

	private FontShader shader;
	
	public FontRenderer() {
		shader = new FontShader();
	}
	
	public void render(Map<FontType, List<GUIText>> texts){
		prepare();
		for(FontType font : texts.keySet()){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText text : texts.get(font)){
				renderText(text);
			}
		}
		endRendering();
	}
	
	/**
	 * This should never be used unless you are dynamicly rendering text.
	 * such as the console where it would be turned off / on
	 * 
	 * or you can use it only if one font is being used.
	 * if you are using more then one font, please use the built-in fontmaster class.
	 * 
	 * @param texts -> texts to be rendered
	 * @param font -> the font to use when rendering the texts.
	 */
	public void renderBAD(List<GUIText> texts, FontType font) {
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
		for(GUIText text : texts){
			renderText(text);
		}
		endRendering();
	}
	
	public void renderBAD(List<GUIDynamicText> texts, FontType font, boolean d) {
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
		for(GUIText text : texts){
			renderText(text);
		}
		endRendering();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.start();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColour(text.getColour());
		shader.loadTranslation(text.getPosition());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
