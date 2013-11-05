package com.rcg.common;

public abstract class ClientRequest {

	private String name;

	public ClientRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
