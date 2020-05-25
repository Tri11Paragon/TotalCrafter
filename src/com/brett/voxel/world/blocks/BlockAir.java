package com.brett.voxel.world.blocks;

import com.brett.voxel.renderer.COLLISIONTYPE;
import com.brett.voxel.renderer.RENDERMODE;

/**
*
* @author brett
* @date Feb. 13, 2020
*/

public class BlockAir extends Block {
	
	
	public BlockAir() {
		super(null, 0);
		// THIS MAKES 0 SENSE
		// IF I SET THIS THEN THE CHUNK MESHER BREAKS
		// AIR IS TRANSPARENT THOUGH SO IT SHOULD'NT BREAK
		// BUT IT DOES
		// AND ITS NOT EVEN CONSTANT!
		// FIRST FEW BLOCKS DON'T GET MESHED BUT THE ONES UDNER IT DOES AND ITS ONLY
		// EVERY LIKE 32 BLOCKS OR SOMETHING
		// THIS IS STUPID
		/**
		 * turns out the retard that coded the chunk mesher forgot to change the blocks[][][] in the exception :/							
		 */
		this.setRendermode(RENDERMODE.TRANSPARENT);
		this.setCollisiontype(COLLISIONTYPE.NOT);
	}
	
}
