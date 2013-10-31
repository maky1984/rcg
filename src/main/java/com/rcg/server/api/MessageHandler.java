package com.rcg.server.api;

public interface MessageHandler {

	public boolean accept(Message message, ClientHandle caller);
	
}
