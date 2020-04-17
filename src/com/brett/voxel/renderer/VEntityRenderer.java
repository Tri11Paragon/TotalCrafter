package com.brett.voxel.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.voxel.world.VoxelWorld;
import com.brett.voxel.world.entity.VEntity;

/**
*
* @author brett
* @date Apr. 10, 2020
*/

public class VEntityRenderer {
	
	private VoxelWorld world;
	private List<VEntity> ents = new ArrayList<VEntity>();
	private HashMap<TexturedModel, List<VEntity>> entMap = new HashMap<TexturedModel, List<VEntity>>();
	
	public VEntityRenderer(VoxelWorld world) {
		this.world = world;
	}
	
	public void render() {
		for (int i = 0; i < ents.size(); i++) {
			processEntity(ents.get(i));
		}
		for (TexturedModel texture : entMap.keySet()) {
			RawModel mod = texture.getRawModel();
			GL30.glBindVertexArray((int)mod.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTexture().getID());
			GL11.glDrawElements(0x4, (int)mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}
	
	public void processEntity(VEntity ent) {
		List<VEntity> lst = entMap.get(ent.getModel());
		if (lst == null) {
			List<VEntity> nlst = new ArrayList<VEntity>();
			nlst.add(ent);
			entMap.put(ent.getModel(), nlst);
		} else {
			lst.add(ent);
		}
	}
	
	public void spawnEntity(VEntity e) {
		e.onEntitySpawned(world);
		ents.add(e);
	}
	
	public void deleteEntity(VEntity e) {
		ents.remove(e);
	}
	
}
