package com.brett.engine.managers;

import com.brett.Main;

/**
* @author Brett
* @date Jul. 1, 2020
*/

public class ThreadPool {
	
	public static int reservedThreads;
	
	public static int reserveQuarterThreads() {
		int quart = Main.processors/4;
		reservedThreads += quart;
		if (reservedThreads > Main.processors)
			return 1;
		return quart;
	}
	
	public static int getReservedThreads() {
		return reservedThreads;
	}
	
	public static int reserveThreads(int amount) {
		reservedThreads += amount;
		if (reservedThreads > Main.processors)
			return 1;
		return reservedThreads;
	}
	
}
