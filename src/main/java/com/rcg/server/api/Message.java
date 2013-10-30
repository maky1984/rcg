package com.rcg.server.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Message {

	private byte[] data;
	
	public Message(byte[] data) {
	}

	@JsonIgnore
	public long getSizeInBytes() {
		return data.length;
	}
	
	public byte[] getData() {
		return data;
	}
	
}
