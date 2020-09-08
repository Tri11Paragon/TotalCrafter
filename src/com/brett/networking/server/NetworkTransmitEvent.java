package com.brett.networking.server;

import java.io.DataOutputStream;

/**
* @author Brett
* @date Sep 6, 2020
*/

public interface NetworkTransmitEvent {
	
	public void transmit(DataOutputStream dos);
	
}
