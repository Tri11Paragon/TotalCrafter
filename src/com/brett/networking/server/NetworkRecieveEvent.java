package com.brett.networking.server;

import java.io.DataInputStream;

/**
* @author Brett
* @date 4-Sep-2020
*/

public interface NetworkRecieveEvent {
	
	public void dataRecieved(DataInputStream dis);
	
}
