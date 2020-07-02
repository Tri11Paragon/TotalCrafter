package com.brett.engine.ui.screen;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.brett.engine.cameras.CreativeCamera;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.shaders.ProjectionMatrix;
import com.brett.engine.shaders.VoxelShader;
import com.brett.engine.tools.Maths;
import com.brett.engine.ui.UIElement;
import com.brett.engine.ui.font.UIText;
import com.brett.world.GameRegistry;
import com.brett.world.World;
import com.brett.world.block.Block;
import com.brett.world.chunks.Chunk;
import com.brett.world.chunks.data.ShortBlockStorage;

/**
* @author Brett
* @date Jun. 22, 2020
*/

public class SinglePlayer extends Screen {

	public static Matrix4f chunkViewMatrix;
	public static Matrix4f viewMatrix;
	public CreativeCamera camera;
	public int textureAtlas;
	public World world;
	public VoxelShader shader;
	
	public SinglePlayer() {
		textureAtlas = ScreenManager.loader.loadSpecialTextureATLAS(16, 16);
		GameRegistry.registerBlocks();
		GameRegistry.registerItems();
	}
	
	@Override
	public void onSwitch() {
		super.onSwitch();
		//elements.add(new UITexture(ScreenManager.loader.loadTexture("dirt"), -2, -2, 0, 0, 200, 200, AnchorPoint.CENTER).setBoundingBox(200, 200, 200, 200));
		
		/*UIText text = new UIText("he l loe there", 250.0f, "mono", 600, 300, 20);
		elements.add(new UIButton(ScreenManager.loader.loadTexture("dirt"), ScreenManager.loader.loadTexture("clay"), 600, 300, 115, 100).setText(text));
		
		UITexture bar = new UITexture(ScreenManager.loader.loadTexture("coal_block"), -1, -1, 50, 50, 10, 30);
		elements.add(new UISlider(ScreenManager.loader.loadTexture("sand"), bar, 50, 50, 300, 30));
		elements.add(bar);
		UIText textd = new UIText("", 250, "mono", 50, 200, 500);
		addText(textd);
		elements.add(new UITextInput(ScreenManager.loader.loadTexture("clay"), textd, 31, 50, 200, 400, 50));*/
		camera = new CreativeCamera(new Vector3f(0,4,0));
		
		
		world = new World();
		shader = new VoxelShader();
		shader.start();
		shader.loadProjectionMatrox(ProjectionMatrix.projectionMatrix);
		shader.stop();
		
		ShortBlockStorage shrt = new ShortBlockStorage();
		
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				shrt.setWorld(i, 1, k, Block.DIRT);
			}
		}
		shrt.setWorld(2, 1, 10, Block.STONE);
		
		Chunk c = new Chunk(world, shrt, null, null, 0, 0, 0);
		world.setChunk(0, 0, 0, c);
		c.meshChunk();
		
		ShortBlockStorage shrt2 = new ShortBlockStorage();
		
		for (int i = 0; i < 16; i++) {
			for (int k = 0; k < 16; k++) {
				shrt2.setWorld(i, 1, k, Block.STONE);
			}
		}
		
		Chunk c2 = new Chunk(world, shrt2, null, null, 1, 0, 0);
		world.setChunk(1, 0, 0, c2);
		c2.meshChunk();
		
		//GL13.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
	}
	
	@Override
	public void onLeave() {
		world.save();
		super.onLeave();
	}
	
	@Override
	public List<UIElement> render() {
		camera.move();
		chunkViewMatrix = Maths.createViewMatrixROT(camera);
		viewMatrix = Maths.createViewMatrix(camera);
		
		ScreenManager.enableCulling();
		ScreenManager.enableTransparentcy();
		
		Chunk c = world.getChunk(0, 0, 0);
		Chunk c2 = world.getChunk(1, 0, 0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureAtlas);
		
		shader.start();
		shader.loadViewMatrix(chunkViewMatrix);
		
		
		if (c != null) {
			c.render(shader, 0, 0, 0);
		}
		if (c2 != null) {
			c2.render(shader, 1, 0, 0);
		}
		
		shader.stop();
		ScreenManager.disableCulling();
		ScreenManager.disableTransparentcy();
		
		return super.render();
	}
	
	@Override
	public Map<String, List<UIText>> renderText() {
		return super.renderText();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
}
