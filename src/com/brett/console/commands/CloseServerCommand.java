package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.voxel.networking.PACKETS;
import com.brett.voxel.world.VoxelWorld;

/**
*
* @author brett
* @date Jun. 3, 2020
*/

public class CloseServerCommand extends Command{

	@Override
	public String run(String data, String[] vars) {
		if (VoxelWorld.localClient != null)
			VoxelWorld.localClient.sendData(new byte[] {PACKETS.EXIT, PACKETS.EXIT, PACKETS.EXIT});
		return "Closing server\n you should close this game window.";
	}

}
