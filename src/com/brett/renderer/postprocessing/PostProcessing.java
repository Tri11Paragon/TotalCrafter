package com.brett.renderer.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.DisplayManager;
import com.brett.datatypes.ModelVAO;
import com.brett.renderer.Loader;
import com.brett.renderer.postprocessing.bloom.CombineFilter;
import com.brett.renderer.postprocessing.gaussianblur.HorizontalBlur;
import com.brett.renderer.postprocessing.gaussianblur.VerticalBlur;

/**
 * @author brett
 * These are old classes and im not currently using them
 * please ignore the post processing classes (2020-6-11)
 *
 */
public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static ModelVAO quad;
	//private static ContrastChanger contrastChanger;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static CombineFilter combineFilter;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		//contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(DisplayManager.WIDTH/5, DisplayManager.HEIGHT/5);
		vBlur = new VerticalBlur(DisplayManager.WIDTH/5, DisplayManager.HEIGHT/5);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colorTexture, int brightTexture){
		start();
		hBlur.render(brightTexture);
		vBlur.render(hBlur.getOutputTexture());
		combineFilter.render(colorTexture, vBlur.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){
		combineFilter.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}
