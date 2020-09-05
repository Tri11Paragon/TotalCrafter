package com.brett.networking;

import java.io.DataInputStream;

/**
* @author Brett
* @date 4-Sep-2020
*/

public interface NetworkEventReciever {
	
	public void dataRecieved(DataInputStream dis);
	
}
