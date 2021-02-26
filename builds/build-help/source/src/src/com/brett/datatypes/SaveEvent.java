package com.brett.datatypes;

/**
*
* @author brett
* @date Mar. 30, 2020
* Interface for handling before the world saves and after the world saves.
*/

public interface SaveEvent {
	
	public void preSaveEvent();
	
	public void postSaveEvent();
	
}
