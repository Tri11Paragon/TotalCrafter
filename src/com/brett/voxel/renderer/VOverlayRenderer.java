package com.brett.voxel.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.cameras.Camera;
import com.brett.cameras.ICamera;
import com.brett.datatypes.ModelVAO;
import com.brett.renderer.Loader;
import com.brett.renderer.MasterRenderer;
import com.brett.tools.Maths;
import com.brett.voxel.renderer.shaders.VOverlayShader;
import com.brett.voxel.world.MeshStore;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Apr. 14, 2020
*/

public class VOverlayRenderer {
	
	private Camera camera;
	private VOverlayShader shader;
	private ModelVAO model;
	private int[] textures = new int[11];
	private int texture;
	
	public VOverlayRenderer(MasterRenderer renderer, ICamera camera, VoxelWorld world) {
		this.camera = (Camera) camera;
		this.shader = new VOverlayShader();
		this.shader.start();
		this.shader.loadProjectionMatrix(renderer.getProjectionMatrix());
		this.shader.stop();
		Loader loader = world.getLoader();
		this.model = loader.loadToVAO(MeshStore.vertsBig, MeshStore.uv, MeshStore.indicies);
		textures[0] = loader.loadTexture("outline");
		textures[1] = loader.loadTexture("break0");
		textures[2] = loader.loadTexture("break1");
		textures[3] = loader.loadTexture("break2");
		textures[4] = loader.loadTexture("break3");
		textures[5] = loader.loadTexture("break4");
		textures[6] = loader.loadTexture("break5");
		textures[7] = loader.loadTexture("break6");
		textures[8] = loader.loadTexture("break7");
		textures[9] = loader.loadTexture("break8");
		textures[10] = loader.loadTexture("break9");
		this.texture = textures[0];
	}
	
	public void renderOverlay(int[] pos) {
		if (pos[3] == 1)
			return;
		this.shader.start();
		this.shader.loadViewMatrix(camera);
		// enable transparenty
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// standard openGL draw commands
		// shouldn't have to explain these at this point
		GL30.glBindVertexArray((int)model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		this.shader.loadTransformationMatrix(Maths.createTransformationMatrixCube(pos[0], pos[1], pos[2]));
		GL11.glDrawElements(0x4, (int)model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		GL11.glDisable(GL11.GL_BLEND);
		this.shader.stop();
	}
	
	public void changeOverlayProgress(float q, float mq) {
		if (q == mq)
			this.texture = textures[10];
		// reverse q;
		float qb = mq-q;
		// make it a per 10
		float per = (qb/mq) * 10;
		// convert to int
		int v = (int) (per);
		// limit the bounds of the int
		if (v > 10)
			v = 10;
		if (v < 0)
			v = 0;
		// set the texture
		texture = textures[v];
	}
	
}
