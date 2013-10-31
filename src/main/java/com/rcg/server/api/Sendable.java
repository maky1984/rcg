package com.rcg.server.api;

public interface Sendable {

	public byte[] toBytes();
	
	public void fromBytes(byte[] data);
	
}
