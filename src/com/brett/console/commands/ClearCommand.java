package com.brett.console.commands;

import com.brett.console.Command;
import com.brett.console.Console;

public class ClearCommand extends Command {

	private Console c;
	public ClearCommand(Console c) {
		this.c = c;
	}
	
	@Override
	public String run(String data) {
		c.clear();
		return "";
	}

}
