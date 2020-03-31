package com.brett.voxel.renderer;

import org.lwjgl.util.vector.Vector3f;

import com.brett.renderer.DisplaySource;
import com.brett.renderer.MasterRenderer;
import com.brett.renderer.particles.ParticleMaster;
import com.brett.sound.AudioController;
import com.brett.voxel.VoxelScreenManager;
import com.brett.voxel.world.VoxelWorld;
import com.brett.world.cameras.ICamera;

/**
*
* @author brett
* @date Mar. 9, 2020
*/

public class VoxelRenderer implements DisplaySource {
	
	private MasterRenderer renderer;
	private ICamera camera;
	private VoxelWorld world;
	
	public VoxelRenderer(MasterRenderer renderer, ICamera camera, VoxelWorld world) {
		this.renderer = renderer;
		this.camera = camera;
		this.world = world;
	}
	
	@Override
	public void render() {
		camera.move();
		//camera.calculateFrustum(renderer.getProjectionMatrix(), Maths.createViewMatrixOTHER(camera));
		//System.out.println(camera.planeIntersection(new Vector3f(0,0,0), 1));
		AudioController.setListenerPosition(camera.getPosition(), camera.getYaw(), camera.getPitch(), camera.getRoll());
		VoxelScreenManager.ls.loadViewMatrix(camera);
		VoxelScreenManager.pt.loadViewMatrix(camera);
		//rdCamera.move();
		//rdCamera.checkCollision(terrain);
		//player.update();
		//player.checkCollision(terrain);
		//renderer.processEntity(player);
		
		//advSys.generateParticles(player.getPosition());
		//fireSys.generateParticles(new Vector3f(50, 0, -50));
		//smokeSys.generateParticles(new Vector3f(150, 3, -150));
		
		ParticleMaster.update(camera);
		//picker.update();
		//Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		//if (terrainPoint != null & !Mouse.isGrabbed()) {ng 
			//entity.setPosition(terrainPoint);
		//}
		//System.out.println(Mouse.getX() + " " + Mouse.getY());
		//world.update();
		//world.render(camera, sun, true);
		renderer.renderSkyNone(camera);
		world.render(camera);
		world.update();
		VoxelScreenManager.ls.render();
		VoxelScreenManager.ls.renderIN(new Vector3f(0,70,0), new Vector3f(0,90,0));
		VoxelScreenManager.ls.renderIN(new Vector3f(0,70,0), new Vector3f(10,90,10));
		VoxelScreenManager.pt.render();
	}

}
