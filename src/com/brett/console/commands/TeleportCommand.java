package com.brett.console.commands;

import com.brett.cameras.Camera;
import com.brett.console.Command;

/** 
*	Brett Terpstra
*	Mar 2, 2020
*	
*/
public class TeleportCommand extends Command {
	
	public Camera cam;
	
	public TeleportCommand(Camera c) {
		this.cam = c;
	}
	
	@Override
	public String run(String data, String[] vars) {
		if (vars.length < 3)
			return "Please enter an x,y,z";
		cam.getPosition().x = Float.parseFloat(vars[0]);
		cam.getPosition().y = Float.parseFloat(vars[1]);
		cam.getPosition().z = Float.parseFloat(vars[2]);
		return "";
	}
	
}
