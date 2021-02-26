package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.voxel.world.player.Player;

/**
*
* @author brett
* @date Jun. 18, 2020
*/

public class FlightCommand extends Command {

	@Override
	public String run(String data, String[] vars) {
		// toggles flight
		Player.flight = !Player.flight;
		return "Flight Enabled: " + Player.flight;
	}

}
