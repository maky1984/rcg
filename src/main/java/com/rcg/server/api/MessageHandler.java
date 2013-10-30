package com.rcg.server.api;

public interface MessageHandler {

	public void accept(Message message, ClientHandle caller);
	
}
