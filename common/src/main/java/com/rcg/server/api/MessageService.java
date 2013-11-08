package com.rcg.server.api;

public interface MessageService {

	public static final int PORT_UNDEFINED = -1;

	public void open(int port);

	public void setDefaultMessageHandler(MessageHandler handler);
	
	public void addClientHandle(ClientHandle clientHandle);

	public ClientHandle[] getClients();

	public void send(ClientHandle client, Message message);

	public void stop();

}
