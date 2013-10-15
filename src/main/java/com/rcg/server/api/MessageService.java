package com.rcg.server.api;

import com.rcg.server.api.ClientHandle.AckStatus;

public interface MessageService {
	
	public void open();
	
	public ClientHandle[] getClients();

	public boolean send(ClientHandle client, Message message);
	
	public void stop();
	
}
