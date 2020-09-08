package com.brett.networking;

/**
* @author Brett
* @date 4-Sep-2020
*/

public class Flags {
	
	/**
	 * static begining of message flags
	 */
	public static final byte B_CHUNKREQ = 0x1;
	public static final byte B_BLOCKSET = 0x2;
	public static final byte B_BLOCKREQ = 0x3;
	
	public static final byte P_PLYSYNC = 0x4;
	
	public static final byte S_STOP = 0x5;
	public static final byte S_LOGIN = 0x6;
	public static final byte S_FAILED = 0x7;
	
	
}
