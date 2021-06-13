package com.brett.utils;

import java.util.HashMap;
import java.util.Map;

/**
* @author Brett
* @date 12-Jun-2021
*/

public class OptionsParser {
	
	private Map<String, String> options = new HashMap<String, String>();
	@SuppressWarnings("unused")
	private String[] args;
	
	
	public OptionsParser(String[] args) {
		this.args = args;
	}
	
	
	public void addRequiredArg(String name) {
		if (options.get(name) != null)
			throw new RuntimeException("Argument already added");
		options.put(name, "");
	}
	
	public void addRequiredArg(char c) {
		if (options.get(c + "") != null)
			throw new RuntimeException("Argument already added");
		options.put(c + "", "");
	}
	
	
}
