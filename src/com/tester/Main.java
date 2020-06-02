package com.tester;

import com.brett.voxel.VoxelScreenManager;

public class Main {
	
	public static void main(String[] args) {
		//Client c = new Client("", "player");
		VoxelScreenManager.init();
	}

}

/* TODO List
 * 
 * make lights when they are inside objects work.
 * Background resource loader
 * make FBOs have more then 2 color attachments?
 * add multi-terrain support (check)
 * add settings saver / loader
 * add world loader / saver
 * add world builder
 * merge FBO classes
 * add entity picking
 * add shadows to entities
 * finish water renderer / clean up water renderer
 * add some uniforms for font rendering
 * add bash
 * add console (checkish)
 * ^.*$
 */
