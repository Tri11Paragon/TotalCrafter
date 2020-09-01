package com.brett.engine.ui.console;

import com.brett.engine.cameras.ICamera;

/**
 * @author Brett
 * @date Jul. 5, 2020
 */

public class TeleportCommand implements Command {

	public ICamera camera;

	public TeleportCommand(ICamera camera) {
		this.camera = camera; 
	}

	@Override
	public String commandEntered(String full, String[] args) {
		if (args.length < 3)
			return "PLEASE ENTER X, Y, Z";
		double x = Double.parseDouble(args[0].replaceAll("[^\\d.]", ""));
		double y = Double.parseDouble(args[1].replaceAll("[^\\d.]", ""));
		double z = Double.parseDouble(args[2].replaceAll("[^\\d.]", ""));
		double xoff = 0;
		double yoff = 0;
		double zoff = 0;
		if (args[0].startsWith("/"))
			xoff = camera.getX();
		if (args[1].startsWith("/"))
			yoff = camera.getY();
		if (args[2].startsWith("/"))
			zoff = camera.getZ();
		camera.setPosition(xoff + x, yoff + y, zoff + z);
		return "Position set!";
	}

}
