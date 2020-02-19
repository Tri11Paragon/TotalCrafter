package com.brett.networking;

/**
*
* @author brett
*
*/

public abstract class Packet {
	
	// oh boy its been a while since i've used these
	// but they are useful
	/**
	 * used to define what we are sending to the server
	 */
	public static enum packetTypes {
		// please don't make these bigger then 1 byte.
		// they take valuable space!
		INVALID(-1),
		LOGIN(0),
		DISCONNECT(1),
		DATA(2);
		
		private int packetId;
		/**
		 * @param packetId used for identification on the server
		 */
		private packetTypes(int packetId) {
			this.packetId = packetId;
		}
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}
	
	public abstract void writeData(Client client);
	public abstract void writeData(Server server);
	public abstract byte[] getData();
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}
	
	// i don't like this
	// maybe TODO: use a lookup table??
	public static packetTypes lookupPacket(int id) {
		for (packetTypes p : packetTypes.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return packetTypes.INVALID;
	}
}
