package com.brett.voxel.renderer;

import com.brett.cameras.Camera;
import com.brett.cameras.ICamera;
import com.brett.renderer.DisplaySource;
import com.brett.renderer.MasterRenderer;
import com.brett.tools.Maths;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Mar. 9, 2020
*/

public class VoxelRenderer implements DisplaySource {
	
	private MasterRenderer renderer;
	private Camera camera;
	private VoxelWorld world;
	
	public VoxelRenderer(MasterRenderer renderer, ICamera camera, VoxelWorld world) {
		this.renderer = renderer;
		this.camera = (Camera) camera;
		this.world = world;
	}
	
	@Override
	public void render() {
		camera.move();
		camera.calculateFrustum(renderer.getProjectionMatrix(), Maths.createViewMatrixOTHER(camera));
		
		//AudioController.setListenerPosition(camera.getPosition(), camera.getYaw(), camera.getPitch(), camera.getRoll());
		
		//rdCamera.move();
		//rdCamera.checkCollision(terrain);
		//player.update();
		//player.checkCollision(terrain);
		//renderer.processEntity(player);
		
		//advSys.generateParticles(player.getPosition());
		//fireSys.generateParticles(new Vector3f(50, 0, -50));
		//smokeSys.generateParticles(new Vector3f(150, 3, -150));
		
		//ParticleMaster.update(camera);
		
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
		//VoxelScreenManager.ls.render();
		//VoxelScreenManager.pt.render();
	}

}
