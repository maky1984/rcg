package com.rcg.server.api;

public interface MessageFactory {

	public int registerMessageType(Class<?> classObject);
	
	public Class<?> getMessageClassByType(int messageType);
	
}
