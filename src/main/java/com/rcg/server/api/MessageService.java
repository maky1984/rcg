package com.rcg.server.api;

public interface MessageService {

	public void open(int port);

	public void setDefaultMessageHandler(MessageHandler handler);
	
	public void addClientHandle(ClientHandle clientHandle);

	public ClientHandle[] getClients();

	public void send(ClientHandle client, Message message);

	public void stop();

}
