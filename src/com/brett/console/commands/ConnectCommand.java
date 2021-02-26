package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.voxel.gui.MainMenu;

/**
* @author Brett
* @date 25-Feb-2021
*/

public class ConnectCommand extends Command {

	private MainMenu menu;
	
	public ConnectCommand(MainMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public String run(String data, String[] vars) {
		menu.connect(vars[1], vars[0]);
		return "";
	}

}
