package com.brett.console;

/**
*
* @author brett
* The command class. Base class for all commands.
*/

public abstract class Command {
	
	/**
	 * Called when the command is typed by the player
	 * returned string is what is echoed into the console
	 * @data the whole text entered into the text bar. (Including the command name)
	 * @vars variables entered (not including command name)
	 * @return command response
	 */
	public abstract String run(String data, String[] vars);
	
}
