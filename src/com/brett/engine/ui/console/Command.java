package com.brett.engine.ui.console;

/**
* @author Brett
* @date Jul. 5, 2020
*/

public interface Command {
	
	/**
	 * @param full the full line of the command
	 * @param args all the arguments of the command. not including the command itself.
	 * @return
	 */
	public String commandEntered(String full, String[] args);
	
}
