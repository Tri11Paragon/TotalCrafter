package com.brett.console.commands;

import com.brett.console.Command;

/** 
*	Brett Terpstra
*	Mar 2, 2020
*	
*/
public class TeleportCommand extends Command {

	@Override
	public String run(String data, String[] vars) {
		if (vars.length < 3)
			return "Please enter";
		return "";
	}
	
}