package com.brett.engine.ui.console;

import org.lwjgl.opengl.GL11;

/**
* @author Brett
* @date 2-Sep-2020
*/

public class PolygonCommand implements Command {
	
	@Override
	public String commandEntered(String full, String[] args) {
		if (args.length < 1)
			return "PLEASE ENTER MODE ((P)OINT, (F)ILL, (L)INE)";
		String mode = args[0].toLowerCase();
		if (mode.contentEquals("p") || mode.contentEquals("point"))
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_POINT);
		if (mode.contentEquals("l") || mode.contentEquals("line"))
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		if (mode.contentEquals("f") || mode.contentEquals("fill"))
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		return "Mode set!";
	}
	
}
