package com.brett.console;

/**
*
* @author brett
*
*/

public abstract class Command {

	public Command() {
		
	}
	
	/**
	 * Called when the command is typed by the player
	 * returned string is what is echoed into the console
	 * @data the whole text entered into the text bar.
	 * @return command response
	 */
	public abstract String run(String data, String[] vars);
	
}
