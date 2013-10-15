package com.rcg.server.api;

public interface Message {

	public boolean hasTask();
	
	public Task getTask();
	
	public long getSizeInBytes();
}
